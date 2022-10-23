package thread.inter_thread_communication.semaphore;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Semaphore full = new Semaphore(0);
        Semaphore empty = new Semaphore(5);
        Queue<String> queue = new ArrayDeque<>();
        Lock lock = new ReentrantLock();

        List<Consumer> consumers = Stream.generate(() -> new Consumer(full, empty, lock, queue))
                .limit(5)
                .peek(t -> t.setDaemon(true))
                .toList();
        List<Producer> producers = Stream.generate(() -> new Producer(full, empty, lock, queue))
                .limit(5)
                .peek(t -> t.setDaemon(true))
                .toList();

        for (Consumer consumer : consumers) consumer.start();
        for (Producer producer : producers) producer.start();
        for (Consumer consumer : consumers) consumer.join();
        for (Producer producer : producers) producer.join();

    }

    private static class Consumer extends Thread {

        private final Semaphore full;
        private final Semaphore empty;
        private final Lock lock;
        private final Queue<String> queue;

        private Consumer(Semaphore full, Semaphore empty, Lock lock, Queue<String> queue) {
            this.full = full;
            this.empty = empty;
            this.queue = queue;
            this.lock = lock;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    full.acquire();
                    lock.lock();
                    String elem = queue.poll();
                    Thread.sleep(2000);
                    System.out.printf("Consumed %s\n", elem);
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                } finally {
                    lock.unlock();
                    empty.release();
                }
            }
        }
    }

    private static class Producer extends Thread {

        private final Semaphore full;
        private final Semaphore empty;
        private final Lock lock;
        private final Random random;
        private final Queue<String> queue;

        private Producer(Semaphore full, Semaphore empty, Lock lock, Queue<String> queue) {
            this.full = full;
            this.empty = empty;
            this.queue = queue;
            this.random = new Random();
            this.lock = lock;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    String item = produce();
                    System.out.printf("Produced %s item\n", item);
                    empty.acquire();
                    lock.lock();
                    queue.add(item);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                    full.release();
                }
            }
        }

        private String produce() {
            return String.format("%d item", random.nextInt());
        }
    }
}
