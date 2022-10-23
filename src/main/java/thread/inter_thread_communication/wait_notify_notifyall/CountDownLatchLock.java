package thread.inter_thread_communication.wait_notify_notifyall;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CountDownLatchLock implements CountDownLatch {

    private final Lock lock;
    private final Condition condition;
    private int count;

    public CountDownLatchLock(Lock lock, int count) {
        this.lock = lock;
        this.condition = lock.newCondition();
        this.count = count;
    }

    @Override
    public void await() throws InterruptedException {
        lock.lock();
        try {
            while (count > 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void countdown() {
        lock.lock();
        try {
            if (count > 0 && --count == 0) {
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getCount() {
        return 0;
    }
}
