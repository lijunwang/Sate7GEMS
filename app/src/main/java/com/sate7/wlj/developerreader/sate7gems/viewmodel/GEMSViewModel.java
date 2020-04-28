package com.sate7.wlj.developerreader.sate7gems.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.sate7.wlj.developerreader.sate7gems.net.retrofit.RetrofitServerImp;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;

public class GEMSViewModel extends ViewModel {
    private final String TAG = "GEMSViewModel";
    private final boolean debug = true;
    //动态改变服务器访问方式，用Retrofit或者用Okhttp;
    protected Server server = RetrofitServerImp.getInstance();

    public void log(String msg) {
        if (debug) {
            Log.d(TAG, msg);
        }
    }


}
