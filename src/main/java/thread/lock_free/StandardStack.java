package thread.lock_free;

public class StandardStack implements Stack<Integer> {

    private StackNode<Integer> head;
    private int counter;

    @Override
    public synchronized void push(Integer value) {
        StackNode<Integer> newHead = new StackNode<>(value);
        newHead.setNext(head);
        head = newHead;
        counter++;
    }

    @Override
    public synchronized Integer pop() {
        if (head == null) {
            counter++;
            return null;
        }
        Integer value = head.getValue();
        head = head.getNext();
        counter++;
        return value;
    }

    @Override
    public int getCounter() {
        return counter;
    }
}
