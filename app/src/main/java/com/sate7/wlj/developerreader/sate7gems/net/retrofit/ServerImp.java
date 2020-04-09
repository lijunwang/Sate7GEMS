package com.sate7.wlj.developerreader.sate7gems.net.retrofit;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sate7.wlj.developerreader.sate7gems.Sate7EGMSApplication;
import com.sate7.wlj.developerreader.sate7gems.net.NetBase;
import com.sate7.wlj.developerreader.sate7gems.net.bean.DeviceDetailInfoBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.LogInfoByDateBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.LoginBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.SimplestResponseBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.WarningInfoBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ServerImp implements Server {
    private final String TAG = "ServerImp";
    private final boolean DEBUG_SERVER = true;
    private RetrofitGEMSServer mGEMSServer;

    public static ServerImp getInstance() {
        return ServerImpHolder.mInstance;
    }

    private ServerImp() {
        mGEMSServer = new Retrofit.Builder().baseUrl(RetrofitGEMSServer.BASE_URL).
                addConverterFactory(ScalarsConverterFactory.create()).
                addConverterFactory(GsonConverterFactory.create()).
                client(UnsafeOkHttpClient.getUnsafeOkHttpClient()).
                build().create(RetrofitGEMSServer.class);
    }

    private static class ServerImpHolder {
        private static ServerImp mInstance = new ServerImp();
    }

    @Override
    public void login(String userName, String pwd, LoginCallBack callBack) {
        JSONObject para = new JSONObject();
        try {
            para.put("type", "1");
            para.put("user_name", userName);
            para.put("pass", pwd);
            log("login  ..." + para.toString());
            Call<LoginBean> call = mGEMSServer.login(para.toString());
            call.enqueue(new Callback<LoginBean>() {
                @Override
                public void onResponse(Call<LoginBean> call, Response<LoginBean> response) {
                    LoginBean loginBean = response.body();
                    log("login onResponse ... " + response.code() + "," + loginBean.getCode() + "," + loginBean.getData().getToken());
                    if (loginBean.getCode() == 0) {
                        callBack.onLoginSuccess(loginBean.getData().getToken());
                    } else {
                        callBack.onLoginFailed(loginBean.getMsg());
                    }
                }

                @Override
                public void onFailure(Call<LoginBean> call, Throwable t) {
                    log("login onFailure ... " + t.getMessage());
                    callBack.onLoginFailed(t.getMessage());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            log("JSONException ... " + e.getMessage());
            callBack.onLoginFailed(e.getMessage());
        }
    }

    @Override
    public void queryAllDevices(int pageNumber, DevicesQueryCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", pageNumber);
        jsonObject.addProperty("page_size", Server.PageSize);
        String body = jsonObject.toString();
        log("queryAllDevices ... " + body + "," + Sate7EGMSApplication.getToken());
        Call<EquipmentListBean> call = mGEMSServer.queryAllEquipments(Sate7EGMSApplication.getToken(), /*"application/json",*/ body);
        call.enqueue(new Callback<EquipmentListBean>() {
            @Override
            public void onResponse(Call<EquipmentListBean> call, Response<EquipmentListBean> response) {
                EquipmentListBean equipmentListBean = response.body();
                ArrayList<EquipmentListBean.DataBean.Device> devices = (ArrayList<EquipmentListBean.DataBean.Device>) equipmentListBean.getData().getDevices();
                log("queryAllDevices onResponse totalPage " + equipmentListBean.getData().getPageCount());
                if (callBack != null) {
                    boolean hasMore = pageNumber < equipmentListBean.getData().getPageCount();
                    callBack.onDeviceQuerySuccess(devices == null ? new ArrayList<>() : devices, hasMore);
                    log("queryAllDevices return data " + hasMore + "," + (devices == null ? " null " : devices.size()));
                }
            }

            @Override
            public void onFailure(Call<EquipmentListBean> call, Throwable t) {
                log("queryAllDevices onFailure ... " + t.getMessage());
                if (callBack != null) {
                    callBack.onDeviceQueryFailed(t.getMessage());
                }
            }
        });
    }

    @Override
    public void queryAllWarningInfo(int pageNumber, String imei, WarningInfoQueryCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", pageNumber);
//        jsonObject.addProperty("page_size", Server.PageSize);
        jsonObject.addProperty("page_size", 100);//TODO server Bug Fix
        String body = jsonObject.toString();
        Call<WarningInfoBean> call = mGEMSServer.queryAllWarnings(Sate7EGMSApplication.getToken(), body, imei);
        call.enqueue(new Callback<WarningInfoBean>() {
            @Override
            public void onResponse(Call<WarningInfoBean> call, Response<WarningInfoBean> response) {
                WarningInfoBean warningInfoBean = response.body();
                int size = warningInfoBean.getData().getMessages().size();
                log("warning info ... " + warningInfoBean.getCode() + ", " + warningInfoBean.getMsg() + "," + size);
                callBack.onWarningInfoQuerySuccess((ArrayList<WarningInfoBean.DataBean.MessagesBean>) warningInfoBean.getData().getMessages(), false);
            }

            @Override
            public void onFailure(Call<WarningInfoBean> call, Throwable t) {
                callBack.onWarningInfoQueryFailed(t.getMessage());
            }
        });
    }

    @Override
    public void createFence(String name, String startTime, String endTime, ArrayList<LatLng> vertexList, ArrayList<String> imeiList, FenceCreateCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("start_date", startTime);
        jsonObject.addProperty("end_date", endTime);
        jsonObject.addProperty("day_type", "WEEK");
        JsonArray days = new JsonArray();
        days.add(1);
        days.add(2);
        days.add(3);
        days.add(4);
        days.add(5);
        days.add(6);
        days.add(7);
        jsonObject.add("days", days);
        jsonObject.addProperty("interval", "INTERVAL");
        jsonObject.addProperty("mtd", "GEOFENCE");
        JsonArray gps = new JsonArray();
        for (LatLng tmp : vertexList) {
            gps.add(tmp.latitude);
            gps.add(tmp.longitude);
        }
        jsonObject.add("gps", gps);
        JsonArray labelsArray = new JsonArray();
        JsonObject label = new JsonObject();
        label.addProperty("label", "label");
        label.addProperty("operation", ">");
        label.addProperty("value", "14");
        labelsArray.add(label);
        jsonObject.add("labels", labelsArray);
        JsonArray imeis = new JsonArray();
        for (String tmp : imeiList) {
            imeis.add(tmp);
        }
        jsonObject.add("imeis", imeis);
        String body = jsonObject.toString();
        log("create fence body == " + body);
        Call<SimplestResponseBean> create = mGEMSServer.createFence(Sate7EGMSApplication.getToken(), body);
        create.enqueue(new Callback<SimplestResponseBean>() {
            @Override
            public void onResponse(Call<SimplestResponseBean> call, Response<SimplestResponseBean> response) {
                SimplestResponseBean responseBean = response.body();
                log("create fence onResponse ... " + responseBean.getCode() + "," + responseBean.getMsg());
                if (responseBean.getCode() == 0 && callBack != null) {
                    callBack.onFenceCreateSuccess(responseBean.getMsg());
                } else if(callBack != null){
                    callBack.onFenceCreateFailed(responseBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call<SimplestResponseBean> call, Throwable t) {
                log("create fence onFailure ... " + t.getMessage());
                if (callBack != null) {
                    callBack.onFenceCreateFailed(t.getMessage());
                }
            }
        });
    }

    @Override
    public void queryAllFence(int pageNumber, FenceQueryCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", pageNumber);
        jsonObject.addProperty("page_size", Server.PageSize);
        String body = jsonObject.toString();
        Call<FenceListBean> call = mGEMSServer.queryAllFences(Sate7EGMSApplication.getToken(), body, Sate7EGMSApplication.getOrgCode());
        call.enqueue(new Callback<FenceListBean>() {
            @Override
            public void onResponse(Call<FenceListBean> call, Response<FenceListBean> response) {
                log("fence onResponse ... " + response.body().getCode() + "," + response.body().getMsg() + "," + response.body().getData().getPageCount());
                ArrayList<FenceListBean.DataBean.FenceBean> fenceBeans = (ArrayList<FenceListBean.DataBean.FenceBean>) response.body().getData().getFenceList();
                if (response.body().getCode() == 0 && callBack != null) {
                    boolean hasMore = pageNumber < response.body().getData().getPageCount();
                    callBack.onFenceQuerySuccess(fenceBeans == null ? new ArrayList<>() : fenceBeans, hasMore);
                }
            }

            @Override
            public void onFailure(Call<FenceListBean> call, Throwable t) {
                log("fence onFailure ... " + t.getMessage());
                if (callBack != null) {
                    callBack.onFenceQueryFailed(t.getMessage());
                }
            }
        });
    }

    @Override
    public void queryDetailInfo(String imei, DetailInfoQueryCallBack callBack) {
        log("queryDetailInfo ... " + imei + "," + Sate7EGMSApplication.getToken());
        Call<DeviceDetailInfoBean> detailInfoBeanCall = mGEMSServer.queryDetailInfo(Sate7EGMSApplication.getToken(), imei);
        detailInfoBeanCall.enqueue(new Callback<DeviceDetailInfoBean>() {
            @Override
            public void onResponse(Call<DeviceDetailInfoBean> call, Response<DeviceDetailInfoBean> response) {
                DeviceDetailInfoBean detailInfoBean = response.body();
                log("detail response ... " + detailInfoBean.getCode() + "," + detailInfoBean.getMsg());
                if (detailInfoBean.getCode() == 0 && callBack != null) {
                    callBack.onDetailQuerySuccess(detailInfoBean);
                }
            }

            @Override
            public void onFailure(Call<DeviceDetailInfoBean> call, Throwable t) {
                if (callBack != null) {
                    callBack.onDetailQueryFailed(t.getMessage());
                }
            }
        });

    }

    @Override
    public void queryLocationByDate(int pageNum, String imei, String startTime, String endTime, LocationsListQueryCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        JsonArray types = new JsonArray();
        types.add("LOCATION_REC");
        jsonObject.add("types", types);
        JsonObject date = new JsonObject();
        date.addProperty("start", startTime);
        date.addProperty("end", endTime);
        jsonObject.add("date", date);
        JsonObject page = new JsonObject();
        page.addProperty("page_no", pageNum);
        page.addProperty("page_size", Server.PageSize);
        jsonObject.add("page", page);
        String content = jsonObject.toString();
        Call<LogInfoByDateBean> logInfoByDateBeanCall = mGEMSServer.queryLocationsListByDate(Sate7EGMSApplication.getToken(), content, imei);
        logInfoByDateBeanCall.enqueue(new Callback<LogInfoByDateBean>() {
            @Override
            public void onResponse(Call<LogInfoByDateBean> call, Response<LogInfoByDateBean> response) {
                LogInfoByDateBean logInfoByDateBean = response.body();
                if (logInfoByDateBean.getCode() == 0 && callBack != null) {
                    callBack.onLocationsListQuerySuccess(logInfoByDateBean);
                    log("onResponse by date ... " + logInfoByDateBean.getData().getLocation().size());
                }
            }

            @Override
            public void onFailure(Call<LogInfoByDateBean> call, Throwable t) {
                log("onFailure by date ... " + t.getMessage());
                if (callBack != null) {
                    callBack.onLocationsListQueryFailed(t.getMessage());
                }
            }
        });
    }

    @Override
    public void updateDevice(EquipmentListBean.DataBean.Device device, String phoneNumber, DeviceUpdateCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("imei", device.getImei());
        jsonObject.addProperty("tag", device.getTag());
        jsonObject.addProperty("d_tag", device.getTagQx());
        jsonObject.addProperty("bind_number", phoneNumber);
        jsonObject.addProperty("type", device.getType());
        String content = jsonObject.toString();
        log("updateDevice content ... " + content);
        Call<SimplestResponseBean> update = mGEMSServer.updateNumber(Sate7EGMSApplication.getToken(), content);
        update.enqueue(new Callback<SimplestResponseBean>() {
            @Override
            public void onResponse(Call<SimplestResponseBean> call, Response<SimplestResponseBean> response) {
                SimplestResponseBean updateDeviceBean = response.body();
                if (updateDeviceBean.getCode() == 0 && callBack != null) {
                    callBack.onDeviceUpdateSuccess(updateDeviceBean.getMsg());
                    log("updateDevice onResponse ... " + updateDeviceBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call<SimplestResponseBean> call, Throwable t) {
                if (callBack != null) {
                    callBack.onDeviceUpdateFailed(t.getMessage());
                }
            }
        });
    }


    private void log(String msg) {
        if (DEBUG_SERVER) {
            Log.d(TAG, msg);
        }
    }
}
