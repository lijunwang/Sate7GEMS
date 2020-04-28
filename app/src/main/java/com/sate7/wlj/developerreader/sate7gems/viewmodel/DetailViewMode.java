package com.sate7.wlj.developerreader.sate7gems.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.baidu.mapapi.model.LatLng;
import com.sate7.wlj.developerreader.sate7gems.net.bean.DeviceDetailInfoBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;

import java.util.List;

public class DetailViewMode extends GEMSViewModel {
    private MutableLiveData<EquipmentListBean.DataBean.Device> locationData = new MutableLiveData<>();

    public MutableLiveData<EquipmentListBean.DataBean.Device> getLatestLocation() {
        return locationData;
    }

    public void queryLatestLocationInfo(String imei) {
        server.queryDetailInfo(imei, new Server.DetailInfoQueryCallBack() {
            @Override
            public void onDetailQuerySuccess(DeviceDetailInfoBean device) {
                EquipmentListBean.DataBean.Device basic = device.getData().getBasic();
                List<List<Double>> locationList = device.getData().getLocation();
                if (locationList != null && locationList.size() > 0) {
                    //是拿第一个还是拿最后一个要问姜振；TODO
                    List<Double> data = locationList.get(locationList.size() - 1);
                    LatLng location = new LatLng(data.get(1), data.get(0));
                    basic.setLocation(location);
                    locationData.postValue(basic);
                } else {
                    basic.setLocation(null);
                    locationData.postValue(basic);
                    log("getLatestLocationInfo failed ...");
                }

            }

            @Override
            public void onDetailQueryFailed(String msg) {
                log("getLatestLocationInfo failed 22 ..." + msg);
            }
        });

        /*OkHttpServerImp.getInstance().getLatestLocationInfo(imei, new OkHttpServerImp.LocationQueryListener() {
            @Override
            public void onLocationQuery(DeviceDetailInfoBean deviceDetailInfoBean) {
                XLog.dReport("Location query ... " + deviceDetailInfoBean.getData().getBasic());
                locationData.postValue(deviceDetailInfoBean);
            }
        });*/
    }
}
