package com.sate7.wlj.developerreader.sate7gems.viewmodel;

import android.content.ContentValues;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sate7.wlj.developerreader.sate7gems.net.OkHttpServerImp;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.RetrofitServerImp;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;
import com.sate7.wlj.developerreader.sate7gems.util.Constants;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.ArrayList;
import java.util.HashSet;

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
    private MutableLiveData<EquipmentListResult> searchResult = new MutableLiveData<>();

    public void observeDeviceListResult(LifecycleOwner owner, Observer<EquipmentListResult> observer) {
        result.observe(owner, observer);
    }

    public void observeSearchDevicesResult(LifecycleOwner owner,Observer<EquipmentListResult> observer){
        searchResult.observe(owner,observer);
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

    public void searchEquipment(int pageNumber,String filter) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.FilterDeviceQueryKey,Constants.FilterDeviceKey_TAG);
        contentValues.put(Constants.FilterDeviceValue,filter);
        log("searchEquipment ... " + pageNumber);
        RetrofitServerImp.getInstance().filterDevices(1,contentValues, new RetrofitServerImp.DevicesQueryCallBack() {
            @Override
            public void onDeviceQuerySuccess(ArrayList<EquipmentListBean.DataBean.Device> devices, boolean hasMore) {
                XLog.dReport("searchEquipment ..." + hasMore + "," + devices);
                searchResult.postValue(new EquipmentListResult(devices, hasMore));
            }

            @Override
            public void onDeviceQueryFailed(String msg) {
                XLog.dReport("searchEquipmentFailed ..." + msg);
            }
        });
    }
}
