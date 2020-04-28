package com.sate7.wlj.developerreader.sate7gems.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.baidu.mapapi.model.LatLng;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;

import java.util.ArrayList;

import javax.security.auth.callback.Callback;

public class FenceViewModel extends GEMSViewModel {
    public static class FenceListData {
        private ArrayList<FenceListBean.DataBean.FenceBean> fenceBeans;
        private boolean hasMore;

        public FenceListData(ArrayList<FenceListBean.DataBean.FenceBean> fenceBeans, boolean hasMore) {
            this.fenceBeans = fenceBeans;
            this.hasMore = hasMore;
        }

        public ArrayList<FenceListBean.DataBean.FenceBean> getFenceBeans() {
            return fenceBeans;
        }

        public void setFenceBeans(ArrayList<FenceListBean.DataBean.FenceBean> fenceBeans) {
            this.fenceBeans = fenceBeans;
        }

        public boolean isHasMore() {
            return hasMore;
        }

        public void setHasMore(boolean hasMore) {
            this.hasMore = hasMore;
        }
    }

    private MutableLiveData<String> createResult = new MutableLiveData<>();

    public MutableLiveData<String> getCreateResult() {
        return createResult;
    }

    private MutableLiveData<FenceListData> fences = new MutableLiveData<>();

    public MutableLiveData<FenceListData> getFences() {
        return fences;
    }

    public void startQueryFenceList(int pageNumber) {
        server.queryAllFence(pageNumber, new Server.FenceQueryCallBack() {
            @Override
            public void onFenceQuerySuccess(ArrayList<FenceListBean.DataBean.FenceBean> fenceList, boolean hasMore) {
                log("startQueryFenceList ... " + pageNumber + "," + fenceList.size() + "," + hasMore);
                fences.postValue(new FenceListData(fenceList, hasMore));
            }

            @Override
            public void onFenceQueryFailed(String msg) {

            }
        });
    }

    public void createFence(String name, String start, String end, ArrayList<LatLng> vertex, ArrayList<String> imei) {
        server.createFence(name, start, end, vertex, imei, new Server.FenceCreateCallBack() {
            @Override
            public void onFenceCreateSuccess(String msg) {
                createResult.postValue(msg);
            }

            @Override
            public void onFenceCreateFailed(String msg) {
                createResult.postValue(msg);
            }
        });
    }

    //状态监听
    public void createDataMonitor(String name, String start, String end, ArrayList<String> imei, String label, String operation, String value) {
        server.createDataMonitor(name,start,end,imei,label,operation,value, new Server.FenceCreateCallBack() {
            @Override
            public void onFenceCreateSuccess(String msg) {
                createResult.postValue(msg);
            }

            @Override
            public void onFenceCreateFailed(String msg) {
                createResult.postValue(msg);
            }
        });
    }
}
