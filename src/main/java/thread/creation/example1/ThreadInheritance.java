package thread.creation.example1;

public class ThreadInheritance {

    private static class CustomThread extends Thread {
        @Override
        public void run() {
            System.out.println("Hello from " + this.getName());
        }
    }

    public static void main(String[] args) {
        Thread th = new CustomThread();
        th.start();
    }
}
