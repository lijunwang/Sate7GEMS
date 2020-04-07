package com.sate7.wlj.developerreader.lib_test_java;

import java.util.HashSet;
import java.util.Iterator;

public class MyClass {
    public static void main(String[] args) {
        HashSet<String> test = new HashSet<>();
        test.add("wang");
        test.add("li");
        test.add("jun");
        System.out.println(test);

        /*Iterator iterator = test.iterator();
        while (iterator.hasNext()) {
            String temp = (String) iterator.next();
            if (temp.equals("li")) {
                test.remove(temp);
            }
        }*/
        System.out.println("-------------------");
        System.out.println(test);
        for (Iterator i = test.iterator(); i.hasNext(); ) {
            String delete = (String) i.next();
            if (delete.equals("li")) {
                boolean remove = test.remove(delete);
                System.out.println("remove ... " + remove);
            }
        }
        System.out.println(test);
    }
}
