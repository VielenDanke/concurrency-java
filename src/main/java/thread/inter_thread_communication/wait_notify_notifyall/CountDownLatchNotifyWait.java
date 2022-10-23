package thread.inter_thread_communication.wait_notify_notifyall;

public class CountDownLatchNotifyWait implements CountDownLatch {

    private int count;

    public CountDownLatchNotifyWait(int count) {
        this.count = count;
    }

    @Override
    public synchronized void await() throws InterruptedException {
        while (count > 0) {
            wait();
        }
    }

    @Override
    public synchronized void countdown() {
        if (count > 0 && --count == 0) {
            notifyAll();
        }
    }

    @Override
    public int getCount() {
        return count;
    }
}
