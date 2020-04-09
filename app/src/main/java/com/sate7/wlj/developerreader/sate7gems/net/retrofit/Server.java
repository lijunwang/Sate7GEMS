package com.sate7.wlj.developerreader.sate7gems.net.retrofit;

import com.baidu.mapapi.model.LatLng;
import com.sate7.wlj.developerreader.sate7gems.net.bean.DeviceDetailInfoBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.LogInfoByDateBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.WarningInfoBean;

import java.util.ArrayList;

/*
 *   @author WangLijun
 *   @Time 2020年4月8日14:04:04
 *   服务器接口
 */
public interface Server {
    //每页加载的数量
    int PageSize = 30;
    //登陆相关
    interface LoginCallBack {
        void onLoginSuccess(String token);

        void onLoginFailed(String reason);
    }

    void login(String userName, String pwd, LoginCallBack callBack);

    //获取当前账户下所有设备
    void queryAllDevices(int pageNub, DevicesQueryCallBack callBack);

    interface DevicesQueryCallBack {
        void onDeviceQuerySuccess(ArrayList<EquipmentListBean.DataBean.Device> devices,boolean hasMore);
        void onDeviceQueryFailed(String msg);
    }

    //获取报警信息
    void queryAllWarningInfo(int pageNub,String imei,WarningInfoQueryCallBack callBack);
    interface WarningInfoQueryCallBack{
        void onWarningInfoQuerySuccess(ArrayList<WarningInfoBean.DataBean.MessagesBean> warningMessages,boolean hasMore);
        void onWarningInfoQueryFailed(String msg);
    }

    //创建围栏
    /*
    *   @para vertexList 顶点信息
    *   @para imeiList   包含哪些设备，即当前的围栏对哪些设备有效;
    */
    void createFence(String name, String startTime, String endTime, ArrayList<LatLng> vertexList, ArrayList<String> imeiList, FenceCreateCallBack callBack);
    interface FenceCreateCallBack{
        void onFenceCreateSuccess(String msg);
        void onFenceCreateFailed(String msg);
    }

    //获取围栏信息
    void queryAllFence(int pageNub,FenceQueryCallBack callBack);
    interface FenceQueryCallBack{
        void onFenceQuerySuccess(ArrayList<FenceListBean.DataBean.FenceBean> fenceList, boolean hasMore);
        void onFenceQueryFailed(String msg);
    }

    //查询设备详情,包括最新的位置信息
    void queryDetailInfo(String imei,DetailInfoQueryCallBack callBack);
    interface DetailInfoQueryCallBack{
        void onDetailQuerySuccess(DeviceDetailInfoBean device);
        void onDetailQueryFailed(String msg);
    }

    //分段查询位置信息
    void queryLocationByDate(int pageNum,String imei,String start,String endTime,LocationsListQueryCallBack callBack);
    interface LocationsListQueryCallBack{
        void onLocationsListQuerySuccess(LogInfoByDateBean logInfoByDateBean);
        void onLocationsListQueryFailed(String msg);
    }

    //更新设备信息(修改电话号码)
    void updateDevice(EquipmentListBean.DataBean.Device device,String phoneNumber,DeviceUpdateCallBack callBack);
    interface DeviceUpdateCallBack{
        void onDeviceUpdateSuccess(String msg);
        void onDeviceUpdateFailed(String msg);
    }



}
