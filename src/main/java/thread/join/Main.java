package thread.join;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<Long> numbers = Arrays.asList(1000000L, 1L, 23L, 4124L, 5555L, 41241L, 33L, 212121L);

        List<FactorialThread> threads = new LinkedList<>();

        for (long num : numbers) {
            threads.add(new FactorialThread(num));
        }

        for (Thread thread : threads) {
            thread.start();
//            thread.setDaemon(true); here we can use daemon threads to not wait them to complete
        }

        for (Thread thread : threads) {
            // We will wait for 2000 ms to complete the computation
            // if computation took longer - skip the join and interrupt the thread next
            thread.join(2000);
        }

        for (int i = 0; i < numbers.size(); i++) {
            FactorialThread factorialThread = threads.get(i);
            Long currentNumber = numbers.get(i);
            if (factorialThread.isFinished()) {
                System.out.printf("Factorial of %d is %s\n", currentNumber, factorialThread.getResult().toString());
            } else {
                System.out.printf("The calculation for %d is still in progress. Interrupt\n", currentNumber);
                // here we call interrupt to cancel long computation. Do not forget to add if statement to check if thread
                // was interrupted from outside.
                factorialThread.interrupt();
            }
        }
    }
}
