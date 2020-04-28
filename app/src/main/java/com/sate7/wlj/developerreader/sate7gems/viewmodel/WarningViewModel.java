package com.sate7.wlj.developerreader.sate7gems.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sate7.wlj.developerreader.sate7gems.Sate7EGMSApplication;
import com.sate7.wlj.developerreader.sate7gems.net.OkHttpServerImp;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.WarningInfoBean;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

public class WarningViewModel extends GEMSViewModel {

    private MutableLiveData<List<WarningInfoBean.DataBean.MessagesBean>> warningInfoList = new MutableLiveData<>();

    public MutableLiveData<List<WarningInfoBean.DataBean.MessagesBean>> getWarningInfoList() {
        return warningInfoList;
    }

    private MutableLiveData<List<WarningInfoBean.DataBean.MessagesBean>> homePageWarnings = new MutableLiveData<>();

    public MutableLiveData<List<WarningInfoBean.DataBean.MessagesBean>> getHomePageWarnings() {
        return homePageWarnings;
    }

    public void fetchWarningInfo(String imei) {
        server.queryAllWarningInfo(1, imei, new Server.WarningInfoQueryCallBack() {
            @Override
            public void onWarningInfoQuerySuccess(ArrayList<WarningInfoBean.DataBean.MessagesBean> warningMessages, boolean hasMore) {
                warningInfoList.postValue(warningMessages);
            }

            @Override
            public void onWarningInfoQueryFailed(String msg) {
            }
        });
    }

    public void fetchHomePageWarnings() {
        server.queryHomePageWarningInfo(1, new Server.WarningInfoQueryCallBack() {
            @Override
            public void onWarningInfoQuerySuccess(ArrayList<WarningInfoBean.DataBean.MessagesBean> warningMessages, boolean hasMore) {
                log("fetchHomePageWarnings ... " + warningMessages.size() + "," + hasMore);
                homePageWarnings.postValue(warningMessages);
            }

            @Override
            public void onWarningInfoQueryFailed(String msg) {
                log("fetchHomePageWarnings Failed ... " + msg);
            }
        });
    }
}
