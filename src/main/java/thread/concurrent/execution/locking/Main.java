package thread.concurrent.execution.locking;

import java.util.Random;

/*
Conclusion:
- Enforcing a strict order on lock acquisition prevents deadlocks (Acquire lock in the same order in methods below)
- Easy to do with a small number of locks
- Hard to follow if we have big amount of locks

Advance solutions:
- Deadlock detections - Watchdog
- Thread interruption (not possible with synchronized)
- tryLock operations (not possible with synchronized)

Locking strategies:
- Coarse-grained locking
- Fine-grained locking

Deadlock:
- Solved by avoiding circular wait and hold
- Lock resources in the same order, everywhere
 */
public class Main {

    public static void main(String[] args) {
        final Intersection intersection = new Intersection();

        Thread threadA = new Thread(new TrainA(intersection));
        Thread threadB = new Thread(new TrainB(intersection));

        threadA.start();
        threadB.start();
    }

    private static class TrainA implements Runnable {
        private final Intersection intersection;
        private final Random random;

        private TrainA(Intersection intersection) {
            this.intersection = intersection;
            this.random = new Random();
        }

        @Override
        public void run() {
            while (true) {
                long sleepingTime = random.nextInt(5);

                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                }
                intersection.takeRoadA();
            }
        }
    }

    private static class TrainB implements Runnable {
        private final Intersection intersection;
        private final Random random;

        private TrainB(Intersection intersection) {
            this.intersection = intersection;
            this.random = new Random();
        }

        @Override
        public void run() {
            while (true) {
                long sleepingTime = random.nextInt(5);

                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                }
                intersection.takeRoadB();
            }
        }
    }

    private static class Intersection {
        private final Object roadA = new Object();
        private final Object roadB = new Object();

        public void takeRoadA() {
            synchronized (roadA) {
                System.out.printf("Road A is locked by Thread: %s\n", Thread.currentThread().getName());
                synchronized (roadB) {
                    System.out.println("Train is passing through road A");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }

                }
            }
        }

        public void takeRoadB() {
            synchronized (roadB) {
                System.out.printf("Road B is locked by Thread: %s\n", Thread.currentThread().getName());
                synchronized (roadA) {
                    System.out.println("Train is passing through road B");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }

                }
            }
        }
    }
}
