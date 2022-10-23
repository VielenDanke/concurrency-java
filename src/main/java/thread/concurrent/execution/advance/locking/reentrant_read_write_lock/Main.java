package thread.concurrent.execution.advance.locking.reentrant_read_write_lock;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

    private static final int HIGHEST_PRICE = 1000;
    private static final int NUMBER_OF_THREADS = 7;

    public static void main(String[] args) throws InterruptedException {
        Database database = new Database(new ReentrantReadWriteLock());

        Random random = new Random();

        for (int i = 0; i < 100000; i++) {
            database.addItem(random.nextInt(HIGHEST_PRICE));
        }
        Thread writer = new Thread(() -> {
            while (true) {
                database.addItem(random.nextInt(HIGHEST_PRICE));
                database.removeItem(random.nextInt(HIGHEST_PRICE));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        });
        writer.setDaemon(true);
        writer.start();

        List<Thread> threads = new LinkedList<>();

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    int upperBound = random.nextInt(HIGHEST_PRICE);
                    int lowerBound = upperBound > 0 ? random.nextInt(upperBound) : 0;
                    database.readFromRange(lowerBound, upperBound);
                }
            });
            thread.setDaemon(true);
            threads.add(thread);
        }
        long start = System.currentTimeMillis();

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        long end = System.currentTimeMillis();

        System.out.printf("Reading took %d ms\n", end - start);
    }

    static class Database {

        private final TreeMap<Integer, Integer> database = new TreeMap<>();
        private final ReentrantReadWriteLock.ReadLock readLock;
        private final ReentrantReadWriteLock.WriteLock writeLock;

        public Database(ReentrantReadWriteLock reentrantReadWriteLock) {
            this.readLock = reentrantReadWriteLock.readLock();
            this.writeLock = reentrantReadWriteLock.writeLock();
        }

        public Integer readFromRange(int lowerBound, int upperBound) {
            readLock.lock();
            try {
                Integer from = database.ceilingKey(lowerBound);
                Integer to = database.floorKey(upperBound);

                if (from == null || to == null) return null;

                return database.subMap(from, true, to, true).values().stream()
                        .mapToInt(i -> i)
                        .sum();
            } finally {
                readLock.unlock();
            }
        }

        public void addItem(int price) {
            writeLock.lock();
            try {
                database.put(price, database.getOrDefault(price, 0) + 1);
            } finally {
                writeLock.unlock();
            }
        }

        public void removeItem(int price) {
            writeLock.lock();
            try {
                Integer num = database.getOrDefault(price, null);

                if (num == null || num - 1 == 0) {
                    database.remove(price);
                } else {
                    database.put(price, database.get(price) - 1);
                }
            } finally {
                writeLock.unlock();
            }
        }
    }
}
