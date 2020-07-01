package com.sate7.wlj.developerreader.lib_test_java.socket;

import java.text.SimpleDateFormat;
import java.util.Calendar;
class Util {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    public static String getTime(){
        return simpleDateFormat.format(Calendar.getInstance().getTimeInMillis());
    }

    public static void printLogWithTime(String msg){
        System.out.println(msg + " AT " + getTime());
    }

    private static final boolean debug = false;
    public static void debug(String msg){
        if(debug){
            System.err.println(msg);
        }
    }
}
