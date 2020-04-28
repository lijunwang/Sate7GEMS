package com.sate7.wlj.developerreader.sate7gems.util;

import android.util.Log;

public class XLog {
    public static String TAG = "Sate7GEMS";
    public static String TAG_REFRESH = "Refresh";

    public static void dReport(String msg) {
        Log.d(TAG, "" + msg);
    }

    public static void dRefresh(String msg) {
        Log.d(TAG_REFRESH, "" + msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, "" + msg);
    }
}
