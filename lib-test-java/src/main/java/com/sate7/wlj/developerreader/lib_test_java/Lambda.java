package com.sate7.wlj.developerreader.lib_test_java;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Lambda {
    public static void main(String[] args) {
        Lambda lambda = new Lambda();
//        lambda.advance1();
        lambda.advance2();

    }

    //一、替代匿名类，解决高度问题(High problem)
    private void advance1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("传统匿名方式run ...");
            }
        }).start();

        new Thread(() -> {
            System.out.println("lambda 表达式方式 ...");
        }).start();
    }

    //2、迭代更简洁
    private void advance2() {
        List<String> list = Arrays.asList("wang", "li", "jun");
        System.out.println("------------for循环方式----------");
        for (int i = 0; i < list.size(); i++) {
            System.out.print("\t" + list.get(i));
        }
        System.out.println("\n" + "------------遍历方式----------");
        for (String s : list) {
            System.out.print("\t" + s);
        }
        System.out.println("\n" + "------------迭代器方式----------");
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.print("\t" + iterator.next());
        }
        System.out.println("\n" + "------------迭代器方式二----------");
        Iterator<String> iterator2 = list.iterator();
        do {
            System.out.print("\t" + iterator2.next());
        } while (iterator2.hasNext());
        System.out.println("\n" + "------------流+lambda----------");
//        list.stream().forEach(n -> System.out.print("\t" + n));
        list.forEach(n -> System.out.print("\t" + n));
        System.out.println("\n" + "------------::神奇的一逼----------");
//        list.forEach(System.out::println);
        list.forEach(Lambda::nani);
    }

    public static void nani(String msg) {
        System.out.print("\t" + msg);
    }

}
