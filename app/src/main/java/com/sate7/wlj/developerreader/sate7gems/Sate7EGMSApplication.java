package com.sate7.wlj.developerreader.sate7gems;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.blankj.utilcode.util.SPUtils;
import com.sate7.wlj.developerreader.sate7gems.net.OkHttpServerImp;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.util.Constants;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.squareup.leakcanary.LeakCanary;
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
//        refreshToken();
        setupLeakCanary();
    }

    private void setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    public static void setToken(String token) {
        mToken = token;
        XLog.dReport("setToken ... " + mToken);
        mCotext.sendBroadcast(new Intent(Constants.ACTION_START_LOAD_DATA));
        SPUtils.getInstance().put("token", token);
    }

    public static String getToken() {
        XLog.dReport("getToken ... " + mToken);
        if (TextUtils.isEmpty(mToken)) {
            mToken = SPUtils.getInstance().getString(Constants.SERVER_TOKEN, "");
        }
        return mToken;
    }

    public static void setOrgCode(String orgCode) {
        mOrgCode = orgCode;
        SPUtils.getInstance().put(Constants.SERVER_ORG_CODE, orgCode);
        XLog.dReport("setOrgCode ... " + mOrgCode);
    }

    public static String getOrgCode() {
        XLog.dReport("getOrgCode ... " + mOrgCode);
        if (TextUtils.isEmpty(mOrgCode)) {
            mOrgCode = SPUtils.getInstance().getString(Constants.SERVER_ORG_CODE, "");
        }
        return mOrgCode;
    }

    public static void refreshToken() {
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
