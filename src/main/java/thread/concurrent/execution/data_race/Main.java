package thread.concurrent.execution.data_race;

import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        final Incrementer incrementer = new Incrementer();

        Thread incrThread = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                if (Thread.currentThread().isInterrupted()) break;
                incrementer.increment();
            }
        });

        Thread checkThread = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                if (Thread.currentThread().isInterrupted()) break;
                incrementer.checkRaceCondition();
            }
        });

        List<Thread> l = new LinkedList<>();
        l.add(incrThread);
        l.add(checkThread);

        incrementer.setExecutedThread(l);

        incrThread.start();
        checkThread.start();
    }

    private static class Incrementer {

        private volatile int x;
        private volatile int y;
        private List<Thread> threads;

        public Incrementer() {
            x = 0;
            y = 0;
        }

        public void increment() {
            x++;
            y++;
        }

        public void checkRaceCondition() {
            if (y > x) {
                interruptThreads();
                System.out.println("Data race detected");
            }
        }

        public void setExecutedThread(List<Thread> threads) {
            this.threads = threads;
        }

        private void interruptThreads() {
            this.threads.forEach(Thread::interrupt);
        }
    }
}
