import java.util.*;

public class ProducerConsumer {
    public static void main (String[] args) {
        Queue<Integer> queue = new LinkedList<Integer>();

        Producer producer = new Producer(queue);
        Consumer consumer = new Consumer(queue);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();
    }
}

class Producer implements Runnable {
    private Random random = new Random();
    private Queue<Integer> queue;
    private int maxSize = 5;

    public Producer(Queue<Integer> queue) {
        this.queue = queue;
    }

    public void run() {
        while(true) {
            try{
                produce(random.nextInt(10));
            } catch (Exception e) {
                System.out.println("Error in produce!");
            }
        }
    }

    private void produce(int num) throws InterruptedException {
        synchronized(queue) {
            while(queue.size() == maxSize) {
                System.out.println("queue is full, producer is waiting for consumer...");
                queue.wait();
            }
            queue.offer(num);
            System.out.println("Producer added number to queue with value of " + num + ".");
            queue.notify();
            Thread.sleep((long) (Math.random() * 1000));
        }
        
    }
}

class Consumer implements Runnable {
    private Queue<Integer> queue;

    public Consumer(Queue<Integer> queue) {
        this.queue = queue;
    }

    public void run() {
        while(true) {
            try{
                consume();
            } catch (Exception e) {
                System.out.println("Error in consume!");
            }
        }
    }

    private void consume() throws InterruptedException {
        synchronized(queue) {
            while(queue.size() == 0) {
                System.out.println("queue is empty, consumer is waiting for producer...");
                queue.wait();
            }
            int num = queue.poll();
            System.out.println("Consumer removed a number from queue with value of " + num + ".");
            queue.notify();
            Thread.sleep((long) (Math.random() * 1000));
        }
        
    }
}