package com.sate7.wlj.developerreader.lib_test_java.nani;

import java.util.ArrayList;
import java.util.List;

public class NaniClient {
    public static void main(String[] args) {
        List<Integer> taskQueue = new ArrayList<Integer>();
        int MAX_CAPACITY = 15;
        Thread tProducer = new Thread(new Producer(taskQueue, MAX_CAPACITY), "Producer");
        Thread tConsumer = new Thread(new Consumer(taskQueue), "Consumer");
        tProducer.start();
        tConsumer.start();
    }
}
