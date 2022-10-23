package thread.performance;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * By increasing number of thread we are increasing throughput
 */
public class IncreaseThroughput {

    private record WordCountHandler(String text) implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String[] parts = query.split("=");
            String action = parts[0];
            String word = parts[1];
            if (!action.equals("word")) {
                exchange.sendResponseHeaders(400, 0);
                return;
            }
            long count = countWord(word);

            byte[] response = Long.toString(count).getBytes();
            exchange.sendResponseHeaders(200, response.length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response);
            outputStream.close();
        }

        private long countWord(String word) {
            long count = 0;
            int index = 0;

            while (index >= 0) {
                index = text.indexOf(word, index);

                if (index >= 0) {
                    count++;
                    index++;
                }
            }
            return count;
        }
    }

    public static void main(String[] args) throws IOException {
        String inputFile = args[0];
        int numberOfThreads = Integer.parseInt(args[1]);
        String text = new String(Files.readAllBytes(Paths.get(inputFile)));
        startServer(text, numberOfThreads);
    }

    private static void startServer(String text, int numberOfThreads) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/search", new WordCountHandler(text));
        Executor executor = Executors.newFixedThreadPool(numberOfThreads);
        server.setExecutor(executor);
        server.start();
    }
}
