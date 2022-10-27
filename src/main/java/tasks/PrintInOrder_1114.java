package tasks;

import java.util.concurrent.Semaphore;

// https://leetcode.com/problems/print-in-order/

public class PrintInOrder_1114 {

    static class Solution {

        private final Semaphore s = new Semaphore(0);
        private final Semaphore p = new Semaphore(0);

        public Solution() {

        }

        public void first(Runnable printFirst) throws InterruptedException {
            printFirst.run();
            s.release();
        }

        public void second(Runnable printSecond) throws InterruptedException {
            s.acquire();
            printSecond.run();
            p.release();
            s.release();

        }

        public void third(Runnable printThird) throws InterruptedException {
            p.acquire();
            printThird.run();
            p.release();
        }
    }
}
