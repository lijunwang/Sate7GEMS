package com.sate7.wlj.developerreader.sate7gems.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sate7.wlj.developerreader.sate7gems.net.Sate7GEMSServer;
import com.sate7.wlj.developerreader.sate7gems.net.bean.DeviceDetailInfoBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

public class DetailViewMode extends ViewModel {
    private MutableLiveData<DeviceDetailInfoBean> locationData = new MutableLiveData<>();

    public MutableLiveData<DeviceDetailInfoBean> getLocationData() {
        return locationData;
    }

    public void getLastLocationInfo(String imei) {
        Sate7GEMSServer.getInstance().getLatestLocationInfo(imei, new Sate7GEMSServer.LocationQueryListener() {
            @Override
            public void onLocationQuery(DeviceDetailInfoBean deviceDetailInfoBean) {
                XLog.dReport("Location query ... " + deviceDetailInfoBean.getData().getBasic());
                locationData.postValue(deviceDetailInfoBean);
            }
        });
    }
}
