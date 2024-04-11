public class SynchronizedExample extends Thread {
    private int count = 0;

    Thread thisThread = new Thread(this);

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}