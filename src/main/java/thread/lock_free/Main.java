package thread.lock_free;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Stack<Integer> stack = new LockFreeStack();
        Random random = new Random();

        for (int i = 0; i < 100000; i++) {
            stack.push(random.nextInt());
        }
        List<Thread> threads = new LinkedList<>();

        int pushingThreads = 2;
        int poppingThreads = 2;

        for (int i = 0; i < pushingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.push(random.nextInt());
                }
            });
            thread.setDaemon(true);
            threads.add(thread);
        }
        for (int i = 0; i < poppingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.pop();
                }
            });
            thread.setDaemon(true);
            threads.add(thread);
        }
        for (Thread thread : threads) thread.start();

        Thread.sleep(10000);

        System.out.printf("%,d operations was executed", stack.getCounter());
    }
}
