package com.sate7.wlj.developerreader.lib_test_java.pool;

public class Coder {
    private int age = 30;
    private String name = "coder";
    private final int POOL_SIZE_MAX = 10;
    private Coder mNext;
    private static Coder mPool;
    private static int mPoolSize = 0;
    private Coder(){
    }

    public static Coder obtain(){
        return obtain(0,null);
    }

    public static Coder obtain(int age,String name){
        Coder coder = null;
        if(mPool != null){
            coder = mPool;
            mPool = coder.mNext;
            coder.mNext = null;
            mPoolSize--;
        }

        if(coder == null){
            coder = new Coder();
            coder.age = age;
            coder.name = name;
        }


        return coder;
    }

    public void release(){
        if(mPoolSize<POOL_SIZE_MAX){
            mNext = mPool;
            mPool = this;
            mPoolSize ++;
        }
    }


    @Override
    public String toString() {
        return "name == " + name + ",age == " + age + "," + super.toString();
    }

    public static void main(String[] args) {
        Coder coder1 = Coder.obtain(21,"wang");
        Coder coder2 = Coder.obtain(22,"li");
        Coder coder3 = Coder.obtain(23,"jun");
        Coder coder4 = Coder.obtain(24,"love");

        coder1.release();
        System.out.println(Coder.mPoolSize);
        coder2.release();
        System.out.println(Coder.mPoolSize);
        coder3.release();
        System.out.println(Coder.mPoolSize);
        coder4.release();
        System.out.println(Coder.mPoolSize);

        System.out.println(coder1);
        System.out.println(coder2);
        System.out.println(coder3);
        System.out.println(coder4);

        Coder coder5 = Coder.obtain(25,"you 11");
        System.out.println(Coder.mPoolSize + ",coder5 " + coder5);

        Coder coder7 = Coder.obtain(27,"you 22");
        System.out.println(Coder.mPoolSize + ",coder7 " + coder7);

        Coder coder8 = Coder.obtain(28,"you 33");
        System.out.println(Coder.mPoolSize + ",coder8 " + coder8);

        Coder coder9 = Coder.obtain(29,"you 44");
        System.out.println(Coder.mPoolSize + ",coder9 " + coder9);

        Coder coder19 = Coder.obtain(29,"you 44");
        System.out.println(Coder.mPoolSize + ",coder9 " + coder19);
    }
}
