package tasks;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TheDiningPhilosophers_1226 {

    static class SolutionLock {
        private final Lock[] forks = new ReentrantLock[5];
        private final Semaphore semaphore = new Semaphore(4);

        public SolutionLock() {
            for (int i = 0; i < 5; i++) forks[i] = new ReentrantLock();
        }

        void pickFork(int id, Runnable pick) {
            forks[id].lock();
            pick.run();
        }

        void putFork(int id, Runnable put) {
            put.run();
            forks[id].unlock();
        }

        // call the run() method of any runnable to execute its code
        public void wantsToEat(int philosopher,
                               Runnable pickLeftFork,
                               Runnable pickRightFork,
                               Runnable eat,
                               Runnable putLeftFork,
                               Runnable putRightFork) throws InterruptedException {
            int leftFork = philosopher;
            int rightFork = (philosopher + 4) % 5;

            semaphore.acquire();

            pickFork(leftFork, pickLeftFork);
            pickFork(rightFork, pickRightFork);
            eat.run();
            putFork(rightFork, putRightFork);
            putFork(leftFork, putLeftFork);

            semaphore.release();
        }
    }

    static class SolutionSemaphore {

        private final Semaphore sem;
        private final Set<Integer> set;

        public SolutionSemaphore() {
            this.set = new HashSet<>();
            this.sem = new Semaphore(1);
            fillUpSet();
        }

        // call the run() method of any runnable to execute its code
        public void wantsToEat(int philosopher,
                               Runnable pickLeftFork,
                               Runnable pickRightFork,
                               Runnable eat,
                               Runnable putLeftFork,
                               Runnable putRightFork) throws InterruptedException {
            sem.acquire();
            if (set.isEmpty()) {
                fillUpSet();
            }
            if (!set.contains(philosopher)) {
                return;
            }
            set.remove(philosopher);
            pickLeftFork.run();
            pickRightFork.run();
            eat.run();
            putLeftFork.run();
            putRightFork.run();
            sem.release();
        }

        private void fillUpSet() {
            for (int i = 0; i < 5; i++) {
                set.add(i);
            }
        }
    }

    static class SolutionSynchronized {
        private final Set<Integer> set;

        public SolutionSynchronized() {
            this.set = new HashSet<>();
            fillUpSet();
        }

        // call the run() method of any runnable to execute its code
        public synchronized void wantsToEat(int philosopher,
                                            Runnable pickLeftFork,
                                            Runnable pickRightFork,
                                            Runnable eat,
                                            Runnable putLeftFork,
                                            Runnable putRightFork) throws InterruptedException {
            if (set.isEmpty()) {
                fillUpSet();
            }
            if (!set.contains(philosopher)) {
                return;
            }
            set.remove(philosopher);
            pickLeftFork.run();
            pickRightFork.run();
            eat.run();
            putLeftFork.run();
            putRightFork.run();
        }

        private void fillUpSet() {
            for (int i = 0; i < 5; i++) {
                set.add(i);
            }
        }
    }
}
