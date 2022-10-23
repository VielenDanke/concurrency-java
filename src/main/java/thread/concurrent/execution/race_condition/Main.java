package thread.concurrent.execution.race_condition;

/*
  We face race condition
  Non-atomic variable is changed by two different threads
  To prevent it we can use synchronized key word:

      private synchronized void increment() {
          count++;
      }
  --------------------------------------------------------
      private final Object object = new Object();

      private void increment() {
          synchronized (object) {
              count++;
          }
      }
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Inventory inventory = new Inventory();

        CalculationThread increment = new CalculationThread(inventory, true);
        CalculationThread decrement = new CalculationThread(inventory, false);

        increment.start();
        decrement.start();

        increment.join();
        decrement.join();

        System.out.printf("Final result: %d\n", inventory.getCount());
    }

    private static class CalculationThread extends Thread {

        private final Inventory inventory;
        private final boolean increment;

        private CalculationThread(Inventory inventory, boolean increment) {
            this.inventory = inventory;
            this.increment = increment;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                if (increment) {
                    inventory.increment();
                } else {
                    inventory.decrement();
                }
            }
        }
    }

    private static class Inventory {

        private int count = 0;

        private void increment() {
            count++;
        }

        private void decrement() {
            count--;
        }

        public int getCount() {
            return count;
        }
    }
}
