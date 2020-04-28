package com.sate7.wlj.developerreader.sate7gems.net;

import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.WarningInfoBean;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;

import java.util.ArrayList;

public class OkhttpTest {
    private OkHttpServerImp sate7GEMSServer = OkHttpServerImp.getInstance();
    private final boolean debug = true;
    private final String TAG = "OkhttpTest";

    private void log(String msg) {
        if (debug) {
            Log.d(TAG, msg);
        }
    }

    public void testQueryFence(){
        sate7GEMSServer.queryAllFence(1, new Server.FenceQueryCallBack() {
            @Override
            public void onFenceQuerySuccess(ArrayList<FenceListBean.DataBean.FenceBean> fenceList, boolean hasMore) {
                log("onFenceQuerySuccess ... " + fenceList.size() + "," + hasMore);
            }

            @Override
            public void onFenceQueryFailed(String msg) {
                log("onFenceQueryFailed ... " + msg);
            }
        });
    }

    public void testLogin() {
        sate7GEMSServer.login("qx_admin", "qx", new Server.LoginCallBack() {
            @Override
            public void onLoginSuccess(String token) {
                log("onLoginSuccess ... " + token);
            }

            @Override
            public void onLoginFailed(String reason) {
                log("onLoginFailed ... " + reason);
            }
        });
    }

    private int pageNumber = 1;

    public void testQueryDevices() {
        sate7GEMSServer.queryAllDevices(pageNumber, new Server.DevicesQueryCallBack() {
            @Override
            public void onDeviceQuerySuccess(ArrayList<EquipmentListBean.DataBean.Device> devices, boolean hasMore) {
                log("onDeviceQuerySuccess ... " + hasMore + "," + devices);
                if (hasMore) {
                    pageNumber++;
                } else {
                    ToastUtils.showShort(R.string.no_more);
                }
            }

            @Override
            public void onDeviceQueryFailed(String msg) {
                log("onDeviceQueryFailed ... " + msg);
            }
        });
    }

    public void testQueryWarningInfo() {
        sate7GEMSServer.queryAllWarningInfo(pageNumber, "356963090002204", new Server.WarningInfoQueryCallBack() {
            @Override
            public void onWarningInfoQuerySuccess(ArrayList<WarningInfoBean.DataBean.MessagesBean> warningMessages, boolean hasMore) {
                log("onWarningInfoQuerySuccess ... " + hasMore + "," + warningMessages);
                if (hasMore) {
                    pageNumber++;
                } else {
                    ToastUtils.showShort(R.string.no_more);
                }
            }

            @Override
            public void onWarningInfoQueryFailed(String msg) {
                log("onWarningInfoQueryFailed ... " + msg);
            }
        });
    }
}
