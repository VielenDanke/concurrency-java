package thread.lock_free;

public interface Stack<T> {

    void push(T value);

    T pop();

    int getCounter();
}
