package com.sate7.wlj.developerreader.sate7gems.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sate7.wlj.developerreader.sate7gems.net.Sate7GEMSServer;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.ArrayList;

public class EquipmentListViewModel extends ViewModel {
    private MutableLiveData<ArrayList<EquipmentListBean.DataBean.Device>> device = new MutableLiveData<>();
    public MutableLiveData<ArrayList<EquipmentListBean.DataBean.Device>> getAllDevice(){
        return device;
    }
    public void listAllEquipment(){
        Sate7GEMSServer.getInstance().listEquipment(new Sate7GEMSServer.ListEquipmentCallBack() {
            @Override
            public void onResponse(ArrayList<EquipmentListBean.DataBean.Device> devices) {
                device.postValue(devices);
                XLog.dReport("EquipmentList onResponse .." + devices.size());
            }
        });


    }
}
