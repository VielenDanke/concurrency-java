package thread.inter_thread_communication.condition_variables;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Consumer consumer = new Consumer(lock, condition);
        Producer producer = new Producer(lock, condition, consumer);

        consumer.start();

        Thread.sleep(5000);

        producer.start();

        consumer.join();
        producer.join();
    }

    private static class Producer extends Thread {
        private final Lock lock;
        private final Condition condition;

        private final Consumer consumer;

        private Producer(Lock lock, Condition condition, Consumer consumer) {
            this.lock = lock;
            this.condition = condition;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            try {
                lock.lock();
                consumer.setUsername("abc");
                consumer.setPassword("abc");
                condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    private static class Consumer extends Thread {

        private final Lock lock;
        private final Condition condition;
        private String username;
        private String password;

        private Consumer(Lock lock, Condition condition) {
            this.lock = lock;
            this.condition = condition;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                while (username == null || password == null) {
                    condition.await();
                }
            } catch (InterruptedException e) {
            } finally {
                lock.unlock();
            }
            System.out.printf("Consumed password for user: %s\n", username);
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
