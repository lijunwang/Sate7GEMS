package com.sate7.wlj.developerreader.sate7gems.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.baidu.mapapi.model.LatLng;
import com.sate7.wlj.developerreader.sate7gems.Sate7EGMSApplication;
import com.sate7.wlj.developerreader.sate7gems.net.Sate7GEMSServer;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;

import java.util.ArrayList;

public class FenceViewModel extends ViewModel {
    private MutableLiveData<String> createResult = new MutableLiveData<>();

    public MutableLiveData<String> getCreateResult() {
        return createResult;
    }

    private MutableLiveData<ArrayList<FenceListBean.DataBean.FenceBean>> fences = new MutableLiveData<>();

    public MutableLiveData<ArrayList<FenceListBean.DataBean.FenceBean>> getFences() {
        return fences;
    }

    public void startQueryFenceList() {
        Sate7GEMSServer.getInstance().queryFenceList(Sate7EGMSApplication.getOrgCode(), new Sate7GEMSServer.OnFenceListQueryCallback() {
            @Override
            public void onFenceListQuery(ArrayList<FenceListBean.DataBean.FenceBean> result) {
                fences.postValue(result);
            }
        });
    }

    public void createFence(String name, String start, String end, ArrayList<LatLng> vertex, ArrayList<String> imei) {
        Sate7GEMSServer.getInstance().createFence(name, start, end, vertex, imei, new Sate7GEMSServer.FaceCreateCallback() {
            @Override
            public void onFenceCreateResult(String msg) {
                createResult.postValue(msg);
            }
        });
    }
}
