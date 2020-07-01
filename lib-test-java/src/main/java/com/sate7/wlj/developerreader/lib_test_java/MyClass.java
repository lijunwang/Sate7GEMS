package com.sate7.wlj.developerreader.lib_test_java;


import java.text.SimpleDateFormat;

public class MyClass {
    private int nani() {
        System.out.println("nani ....");
        return 1;
    }

    Runnable r1 = () -> {
        System.out.println(this);
    };
    Runnable r2 = () -> {
        System.out.println(toString());
    };

    Runnable r3 = new Runnable() {
        @Override
        public void run() {
            System.out.println(this);
        }
    };

    public static void main(String[] args) throws InterruptedException {
        double tan = Math.tan(Math.PI * 45 / 180); // 求角度的tan
        double aa = Math.atan2(1,1) * 180 / Math.PI;// 根据tan求角度
        System.out.println(tan);
        System.out.println(aa);
    }

    private Thread thread = new Thread() {
        @Override
        public void run() {
            super.run();
            synchronized (this) {
                try {
                    System.out.println("before wait ..." + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(System.currentTimeMillis()));
                    this.wait();
                    System.out.println("after wait ..." + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };


    @Override
    public String toString() {
        return "Hello,World!";
    }
}
