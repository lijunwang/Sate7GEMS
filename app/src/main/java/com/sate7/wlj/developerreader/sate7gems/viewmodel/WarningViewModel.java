package com.sate7.wlj.developerreader.sate7gems.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sate7.wlj.developerreader.sate7gems.net.Sate7GEMSServer;
import com.sate7.wlj.developerreader.sate7gems.net.bean.WarningInfoBean;

import java.util.List;

public class WarningViewModel extends ViewModel {

    private MutableLiveData<List<WarningInfoBean.DataBean.MessagesBean>> warningInfoList = new MutableLiveData<>();

    public MutableLiveData<List<WarningInfoBean.DataBean.MessagesBean>> getWarningInfoList() {
        return warningInfoList;
    }
    public void fetchWarningInfo(String imei){
        Sate7GEMSServer.getInstance().getWarningInfo(imei, new WarningInfoCallBack() {
            @Override
            public void onWarningInfoResult(List<WarningInfoBean.DataBean.MessagesBean> warnings) {
                warningInfoList.postValue(warnings);
            }
        });
    }

    public interface WarningInfoCallBack{
        void onWarningInfoResult(List<WarningInfoBean.DataBean.MessagesBean> warnings);
    }
}
