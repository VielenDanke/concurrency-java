package thread.interrupt;

import java.math.BigInteger;

public class Second {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new LongComputationTask(new BigInteger("20000"), new BigInteger("100000")));

        // set daemon allows us not to wait thread to finish long computation task
        thread.setDaemon(true);
        thread.start();
        Thread.sleep(100);

        // interrupt the thread to prevent long computation, do not forget to check if thread is being interrupted
        thread.interrupt();
    }

    private static class LongComputationTask implements Runnable {

        private final BigInteger base;
        private final BigInteger power;

        LongComputationTask(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base + "^" + power + " = " + pow(base, power));
        }

        private BigInteger pow(BigInteger base, BigInteger power) {
            BigInteger result = BigInteger.ONE;

            for (BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                // Find hot spot and call check if Thread was interrupted
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Exiting long computation");
                    return BigInteger.ZERO;
                }
                result = result.multiply(base);
            }
            return result;
        }
    }
}
