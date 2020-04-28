package com.sate7.wlj.developerreader.lib_test_java.produce;

import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args) {
        List<Integer> data = new ArrayList<>();
        int size = 15;
        long produceElapsedTime = 2000;//2秒生产一个
        long consumeElapsedTime = 1000;//1秒生产一个
        Producer producer = new Producer(data, size, produceElapsedTime);
        Consumer consumer = new Consumer(data, consumeElapsedTime);

        new Thread(producer).start();
        new Thread(consumer).start();
    }
}
