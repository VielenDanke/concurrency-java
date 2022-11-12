package tasks;

import java.util.concurrent.Semaphore;

public class BuildingH2O_1117 {

    static class H2O {

        private final Semaphore h, o;

        public H2O() {
            h = new Semaphore(2);
            o = new Semaphore(0);
        }

        public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
            h.acquire();
            releaseHydrogen.run();
            o.release();
        }

        public void oxygen(Runnable releaseOxygen) throws InterruptedException {
            o.acquire(2);
            releaseOxygen.run();
            h.release(2);
        }
    }
}
