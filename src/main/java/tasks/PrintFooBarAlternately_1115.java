package tasks;

import java.util.concurrent.Semaphore;

// https://leetcode.com/problems/print-foobar-alternately/

public class PrintFooBarAlternately_1115 {

    static class Solution {
        private final int n;
        private final Semaphore s = new Semaphore(0);
        private final Semaphore p = new Semaphore(1);

        public Solution(int n) {
            this.n = n;
        }

        public void foo(Runnable printFoo) throws InterruptedException {
            for (int i = 0; i < n; i++) {
                p.acquire();
                printFoo.run();
                s.release();
            }
        }

        public void bar(Runnable printBar) throws InterruptedException {
            for (int i = 0; i < n; i++) {
                s.acquire();
                printBar.run();
                p.release();
            }
        }
    }
}
