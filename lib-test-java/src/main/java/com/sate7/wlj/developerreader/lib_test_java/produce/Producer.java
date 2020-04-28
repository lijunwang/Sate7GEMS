package com.sate7.wlj.developerreader.lib_test_java.produce;

import java.util.List;

public class Producer implements Runnable {
    private List<Integer> queue;
    private int MAX_CAPACITY = 10;
    private long produceElapsedTime;//生产耗时，millisecond

    public Producer(List<Integer> queue, int size, long speed) {
        this.queue = queue;
        this.MAX_CAPACITY = size;
        this.produceElapsedTime = speed;
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            synchronized (queue) {
//                System.out.println("生产者 ... " + queue.size());
                if (queue.size() == MAX_CAPACITY) {
                    try {
                        System.out.println("仓库满了，停止生产 ... ");
                        queue.wait();//生产满了就暂时停止生产，让出锁
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(produceElapsedTime);
                    i++;
                    queue.add(i);
                    System.out.println("生产第 " + i + "个");
                    Thread.currentThread().yield();
                    queue.notifyAll();//生产了一个就可以通知消费了
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
