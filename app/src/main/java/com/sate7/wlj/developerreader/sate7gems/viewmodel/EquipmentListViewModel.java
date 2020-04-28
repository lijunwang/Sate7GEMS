package com.sate7.wlj.developerreader.sate7gems.viewmodel;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sate7.wlj.developerreader.sate7gems.net.OkHttpServerImp;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.ArrayList;

public class EquipmentListViewModel extends GEMSViewModel {
    public static class EquipmentListResult {
        private ArrayList<EquipmentListBean.DataBean.Device> devices;
        private boolean hasMore;

        public EquipmentListResult(ArrayList<EquipmentListBean.DataBean.Device> devices, boolean hasMore) {
            this.devices = devices;
            this.hasMore = hasMore;
        }

        public ArrayList<EquipmentListBean.DataBean.Device> getDevices() {
            return devices;
        }

        public boolean isHasMore() {
            return hasMore;
        }
    }

    private MutableLiveData<EquipmentListResult> result = new MutableLiveData<>();

    public void observeDeviceListResult(LifecycleOwner owner, Observer<EquipmentListResult> observer) {
        result.observe(owner, observer);
    }


    public void listAllEquipment(int pageNumber) {
        log("listAllEquipment ... " + pageNumber);
        server.queryAllDevices(pageNumber, new Server.DevicesQueryCallBack() {
            @Override
            public void onDeviceQuerySuccess(ArrayList<EquipmentListBean.DataBean.Device> devices, boolean more) {
                log("listAllEquipment onDeviceQuerySuccess ... " + devices.size() + "," + more);
                result.postValue(new EquipmentListResult(devices, more));
            }

            @Override
            public void onDeviceQueryFailed(String msg) {

            }
        });
    }
}
