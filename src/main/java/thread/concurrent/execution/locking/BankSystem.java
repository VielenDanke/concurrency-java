package thread.concurrent.execution.locking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankSystem {

    public static void main(String[] args) throws InterruptedException {
        Client fCli = new Client(500);
        Client sCli = new Client(200);

        Processor first = new Processor(fCli, sCli, 200);
        Processor second = new Processor(sCli, fCli, 400);

        first.start();
        second.start();

        first.join();
        second.join();

        System.out.printf("First client money: %f\n", fCli.getMoney());
        System.out.printf("Second client money: %f\n", sCli.getMoney());
    }

    private static class Processor extends Thread {

        private final Client first, second;
        private final double diff;
        private final Random random = new Random();

        private Processor(Client first, Client second, double diff) {
            this.first = first;
            this.second = second;
            this.diff = diff;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    process();
                    System.out.printf("Transaction processed: %s\n", LocalDateTime.now());
                    Thread.sleep(random.nextInt(5));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void process() throws InterruptedException {
            try {
                if (first.getLock().tryLock()) {
                    if (second.getLock().tryLock()) {
                        first.withdraw(diff);
                        second.deposit(diff);
                        Thread.sleep(1);
                    }
                }
            } finally {
                first.getLock().unlock();
                second.getLock().unlock();
            }
        }
    }

    private static class Client {

        private double money;
        private final Lock lock;

        private Client(double money) {
            this.money = money;
            this.lock = new ReentrantLock();
        }

        public void withdraw(double diff) {
            this.money += diff;
        }

        public void deposit(double diff) {
            this.money += diff;
        }

        public double getMoney() {
            return money;
        }

        public Lock getLock() {
            return lock;
        }
    }
}
