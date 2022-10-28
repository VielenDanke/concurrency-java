package tasks;

import java.util.concurrent.Semaphore;
import java.util.function.IntConsumer;

public class PrintZeroEvenOdd_1116 {

    static class Solution {
        private final int n;
        private final Semaphore zero = new Semaphore(1);
        private final Semaphore even = new Semaphore(0);
        private final Semaphore odd = new Semaphore(0);

        public Solution(int n) {
            this.n = n;
        }

        // printNumber.accept(x) outputs "x", where x is an integer.
        public void zero(IntConsumer printNumber) throws InterruptedException {
            boolean isOdd = true;

            for (int i = 0; i < n; i++) {
                zero.acquire();

                printNumber.accept(0);

                if (isOdd) {
                    odd.release();
                } else {
                    even.release();
                }
                isOdd = !isOdd;
            }
        }

        public void even(IntConsumer printNumber) throws InterruptedException {
            int numTimes = n / 2;

            int nextEven = 2;

            for (int i = 0; i < numTimes; i++) {
                even.acquire();
                printNumber.accept(nextEven);
                nextEven += 2;
                zero.release();
            }
        }

        public void odd(IntConsumer printNumber) throws InterruptedException {
            int numTimes = (int)Math.ceil((double) n / 2);
            int nextOdd = 1;
            for (int i = 0; i < numTimes; i++) {
                odd.acquire();
                printNumber.accept(nextOdd);
                nextOdd += 2;
                zero.release();
            }
        }
    }
}
