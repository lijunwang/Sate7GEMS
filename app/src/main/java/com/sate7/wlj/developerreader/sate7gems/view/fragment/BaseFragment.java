package com.sate7.wlj.developerreader.sate7gems.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sate7.wlj.developerreader.sate7gems.util.Constants;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

public class BaseFragment extends Fragment {
    private boolean loaded = false;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_START_LOAD_DATA.equals(intent.getAction())) {
                startLoadData();

            }
        }
    };

    public void startLoadData() {
        XLog.dReport("startLoadData ..." + getClass().getSimpleName());
        loaded = true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getContext().registerReceiver(receiver, new IntentFilter(Constants.ACTION_START_LOAD_DATA));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!loaded){
            startLoadData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        XLog.dReport("onDestroyView ..." + getClass().getSimpleName());
        getContext().unregisterReceiver(receiver);
    }
}
