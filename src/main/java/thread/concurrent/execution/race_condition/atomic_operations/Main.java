package thread.concurrent.execution.race_condition.atomic_operations;

import java.util.Random;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        Metrics metrics = new Metrics();

        MetricsReporter metricsReporter = new MetricsReporter(metrics);

        metricsReporter.start();

        Stream.generate(() -> 1)
                .limit(10)
                .map(i -> new BusinessLogic(metrics))
                .forEach(Thread::start);
    }

    private static class MetricsReporter extends Thread {

        private final Metrics metrics;

        private MetricsReporter(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
                System.out.printf("Current metrics average: %f\n", metrics.getAverage());
            }

        }
    }

    private static class BusinessLogic extends Thread {

        private final Metrics metrics;
        private final Random random = new Random();

        private BusinessLogic(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                long start = System.currentTimeMillis();

                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {

                }
                long end = System.currentTimeMillis();

                metrics.addSample(end - start);
            }
        }
    }

    private static class Metrics {
        private long count = 0;
        private volatile double average = 0.0;

        public synchronized void addSample(long sample) {
            double currentSum = average * count;
            count++;
            average = (currentSum + sample) / count;
        }

        public double getAverage() {
            return average;
        }
    }
}
