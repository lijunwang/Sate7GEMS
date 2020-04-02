package com.sate7.wlj.developerreader.sate7gems;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.sate7.wlj.developerreader.sate7gems.net.Sate7GEMSServer;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.util.Constants;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

public class Sate7EGMSApplication extends Application {
    private static String mToken = "";
    private static String mOrgCode = "";
    private static Context mCotext;

    private static ArrayList<EquipmentListBean.DataBean.Device> equipmentList;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        CrashReport.initCrashReport(getApplicationContext(), "5bc950ed84", true);
        mCotext = this;
        refreshToken();
    }

    public static void setToken(String token) {
        mToken = token;
        XLog.dReport("setToken ... " + mToken);
        mCotext.sendBroadcast(new Intent(Constants.ACTION_START_LOAD_DATA));
    }

    public static void setOrgCode(String orgCode) {
        mOrgCode = orgCode;
        XLog.dReport("setOrgCode ... " + mOrgCode);
    }

    public static String getToken() {
        XLog.dReport("getToken ... " + mToken);
        return mToken;
    }

    public static String getOrgCode() {
        XLog.dReport("getOrgCode ... " + mOrgCode);
        return mOrgCode;
    }

    public static void refreshToken() {
        Sate7GEMSServer.getInstance().login("qx_admin", "qx", new Sate7GEMSServer.LoginCallBack() {
            @Override
            public void loginSuccess(String token) {
                XLog.dReport("loginSuccess ...");
            }

            @Override
            public void loginFailed(String msg) {
                XLog.dReport("loginFailed ...");
            }
        });
    }

    public static void setEquipmentsList(ArrayList<EquipmentListBean.DataBean.Device> deviceList) {
        XLog.dReport("setEquipmentsList ..." + deviceList.size());
        equipmentList = deviceList;
    }

    public static ArrayList<EquipmentListBean.DataBean.Device> getEquipmentsList() {
        XLog.dReport("getEquipmentsList ...");
        return equipmentList;
    }

}
