package thread.creation.example1;

public class ThreadCapabilitiesAndDebugging {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            // code will run in a new thread
            System.out.println("We are now in a thread " + Thread.currentThread().getName());
            System.out.println("Current priority: " + Thread.currentThread().getPriority());
        });
        // Set Thread name
        thread.setName("Working thread");

        // Set Thread priority
        thread.setPriority(Thread.MAX_PRIORITY); // 1..10

        // Set Thread exception handler
        thread.setUncaughtExceptionHandler((thr, ex) -> {
            System.out.println("Critical exception happened in Thread " + thr.getName() + ". Exception " + ex.getMessage());
        });

        System.out.println("We are in thread " + Thread.currentThread().getName() + " before starting a new thread");
        thread.start();
        System.out.println("We are in thread " + Thread.currentThread().getName() + " after starting a new thread");
        Thread.sleep(1000);
    }
}
