package com.sate7.wlj.developerreader.lib_test_java.produce;

import java.util.List;

public class Consumer implements Runnable {
    private List<Integer> queue;
    private long consumerElapsedTime;//消耗速度，millisecond

    public Consumer(List<Integer> queue, long speed) {
        this.queue = queue;
        this.consumerElapsedTime = speed;
    }


    @Override
    public void run() {
        while (true) {
            synchronized (queue) {
//                System.out.println("消费者 ... " + queue.size());
                while (queue.size() == 0) {
                    try {
                        System.out.println("没有东西，等生产 ... ");
                        queue.wait();//消耗完了，等待生产者生产者生产
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(consumerElapsedTime);
                    queue.remove(0);
                    System.out.println("消耗了一个 ... ");
                    Thread.currentThread().yield();
                    queue.notifyAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
