package thread.inter_thread_communication.wait_notify_notifyall;

public interface CountDownLatch {

    void await() throws InterruptedException;

    void countdown();

    int getCount();
}
