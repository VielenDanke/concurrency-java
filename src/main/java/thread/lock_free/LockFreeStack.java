package thread.lock_free;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class LockFreeStack implements Stack<Integer> {

    private final AtomicReference<StackNode<Integer>> head = new AtomicReference<>();
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void push(Integer value) {
        StackNode<Integer> newHead = new StackNode<>(value);

        while (true) {
            StackNode<Integer> currentHead = head.get();
            newHead.setNext(currentHead);
            if (head.compareAndSet(currentHead, newHead)) {
                break;
            } else {
                LockSupport.parkNanos(1);
            }
        }
        atomicInteger.incrementAndGet();
    }

    @Override
    public Integer pop() {
        StackNode<Integer> currentHead = head.get();
        StackNode<Integer> newHead;

        while (currentHead != null) {
            newHead = currentHead.getNext();
            if (head.compareAndSet(currentHead, newHead)) {
                break;
            } else {
                LockSupport.parkNanos(1);
                currentHead = head.get();
            }
        }
        atomicInteger.incrementAndGet();
        return currentHead != null ? currentHead.getValue() : null;
    }

    @Override
    public int getCounter() {
        return atomicInteger.get();
    }
}
