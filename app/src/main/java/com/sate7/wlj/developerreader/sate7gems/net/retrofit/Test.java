package com.sate7.wlj.developerreader.sate7gems.net.retrofit;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.sate7.wlj.developerreader.sate7gems.net.bean.DeviceDetailInfoBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.LogInfoByDateBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.WarningInfoBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.ArrayList;

public class Test {
    private int pageNumber = 1;

    public void testWarning() {
        ServerImp.getInstance().queryAllWarningInfo(pageNumber, "356963090002204", new Server.WarningInfoQueryCallBack() {
            @Override
            public void onWarningInfoQuerySuccess(ArrayList<WarningInfoBean.DataBean.MessagesBean> warningMessages, boolean hasMore) {

            }

            @Override
            public void onWarningInfoQueryFailed(String msg) {

            }
        });
        pageNumber++;
    }

    public void testQueryFence() {
        log("fenceTest ... ");
        ServerImp.getInstance().queryAllFence(pageNumber, new Server.FenceQueryCallBack() {
            @Override
            public void onFenceQuerySuccess(ArrayList<FenceListBean.DataBean.FenceBean> fenceBeans, boolean hasMore) {
                log("fenceTest ... " + fenceBeans + "," + hasMore);
            }

            @Override
            public void onFenceQueryFailed(String msg) {
                log("fenceTest onFenceQueryFailed ... " + msg);
            }
        });
        pageNumber++;
    }

    public void testCreateFence(){
        ArrayList<LatLng> vert = new ArrayList<>();
        vert.add(new LatLng(40.012365,116.3045));
        vert.add(new LatLng(40.022365,116.4045));
        vert.add(new LatLng(40.032365,116.5045));
        vert.add(new LatLng(40.042365,116.6045));
        ArrayList<String> imeis = new ArrayList<>();
        imeis.add("356963090002204");
        ServerImp.getInstance().createFence("testCreateFence","2020-4-8","2020-12-20",vert,imeis,null);
    }

    public void testDetail() {
        ServerImp.getInstance().queryDetailInfo("158287024424070", new Server.DetailInfoQueryCallBack() {
            @Override
            public void onDetailQuerySuccess(DeviceDetailInfoBean device) {
                EquipmentListBean.DataBean.Device basic = device.getData().getBasic();
                log("onDetailQuerySuccess ... " + basic.getLastUpdateTime() + "," + basic);

                updateDevice(basic);
            }

            @Override
            public void onDetailQueryFailed(String msg) {
                log("onDetailQueryFailed ... " + msg);
            }
        });
    }

    public void testByDate() {
        String startTime = "2020-3-8";
        String endTime = "2020-4-8";
        ServerImp.getInstance().queryLocationByDate(pageNumber, "158287024424070", startTime, endTime, new Server.LocationsListQueryCallBack() {

            @Override
            public void onLocationsListQuerySuccess(LogInfoByDateBean logInfoByDateBean) {

            }

            @Override
            public void onLocationsListQueryFailed(String msg) {

            }
        });
    }

    public void updateDevice(EquipmentListBean.DataBean.Device device){
        ServerImp.getInstance().updateDevice(device, "18682145730", new Server.DeviceUpdateCallBack() {
            @Override
            public void onDeviceUpdateSuccess(String msg) {

            }

            @Override
            public void onDeviceUpdateFailed(String msg) {

            }
        });
    }

    private final boolean debug = true;
    private final String TAG = "Test";

    private void log(String msg) {
        if (debug) {
            Log.d(TAG, msg);
        }
    }
}
