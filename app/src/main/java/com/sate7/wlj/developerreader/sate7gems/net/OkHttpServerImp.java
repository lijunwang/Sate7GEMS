package com.sate7.wlj.developerreader.sate7gems.net;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sate7.wlj.developerreader.sate7gems.Sate7EGMSApplication;
import com.sate7.wlj.developerreader.sate7gems.net.bean.DeviceDetailInfoBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.LogInfoByDateBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.LoginBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.SimplestResponseBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.WarningInfoBean;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.WarningViewModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpServerImp implements Server, NetBase {
    private OkHttpClient okHttpClient;
    private final String TAG = "Sate7GEMSServer";
    private final boolean debug = true;

    private void log(String msg) {
        if (debug) {
            Log.d(TAG, msg);
        }
    }

    private static class OkHttpServerImpHolder {
        private static OkHttpServerImp mInstance = new OkHttpServerImp();
    }

    private OkHttpServerImp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        okHttpClient = builder.
                sslSocketFactory(RxUtils.createSSLSocketFactory()).
                hostnameVerifier(new RxUtils.TrustAllHostnameVerifier()).
                build();
    }

    public static OkHttpServerImp getInstance() {
        return OkHttpServerImpHolder.mInstance;
    }

    @Override
    public void login(String userName, String pwd, Server.LoginCallBack callBack) {
        log("login ... " + userName + "," + pwd);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LOGIN_TYPE_USERNAME_PASSWORD_KEY, LOGIN_TYPE_USERNAME_PASSWORD_VALUE);
        jsonObject.addProperty(LOGIN_USER_NAME_KEY, userName);
        jsonObject.addProperty(LOGIN_USER_PASSWORD_KEY, pwd);
        String content = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder().
                url(LOGIN_URL).post(body).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onLoginFailed(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Gson gson = new Gson();
                LoginBean loginBean = gson.fromJson(msg, LoginBean.class);
                if (response.code() == 200 && callBack != null) {
                    callBack.onLoginSuccess(loginBean.getData().getToken());
                    Sate7EGMSApplication.setToken(loginBean.getData().getToken());
                    Sate7EGMSApplication.setOrgCode(loginBean.getData().getOrgs().get(0).getOrgCode());
                } else if (callBack != null) {
                    callBack.onLoginFailed(loginBean.getMsg());
                }
            }
        });
    }

    @Override
    public void queryAllDevices(int pageNub, DevicesQueryCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", pageNub);
        jsonObject.addProperty("page_size", 80);
        String content = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        final Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                url(LIST_EQUIPMENT_URL).post(body).build();
        log("queryAllDevices ... " + pageNub);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Gson gson = new Gson();
                EquipmentListBean deviceBean = gson.fromJson(body, EquipmentListBean.class);
                if (deviceBean.getCode() == 0 && callBack != null) {
                    ArrayList<EquipmentListBean.DataBean.Device> deviceList = (ArrayList<EquipmentListBean.DataBean.Device>) deviceBean.getData().getDevices();
                    boolean hasMore = pageNub < deviceBean.getData().getPageCount();
                    callBack.onDeviceQuerySuccess(deviceList, hasMore);
                } else if (callBack != null) {
                    callBack.onDeviceQueryFailed(deviceBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                XLog.dReport("listEquipment onFailure ... " + e.getMessage());
                if (callBack != null) {
                    callBack.onDeviceQueryFailed(e.getMessage());
                }
            }
        });
    }

    @Override
    public void queryAllWarningInfo(int pageNub, String imei, WarningInfoQueryCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", pageNub);
        jsonObject.addProperty("page_size", 100);
        String content = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                addHeader("content-type", "application/json").
                url(GET_WARNING_INFO + imei).post(body).build();
        log("queryAllWarningInfo ... " + pageNub + "," + content);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Gson gson = new Gson();
                WarningInfoBean warningInfoBean = gson.fromJson(msg, WarningInfoBean.class);
                if (warningInfoBean.getCode() == 0 && callBack != null && warningInfoBean.getData() != null) {
                    ArrayList<WarningInfoBean.DataBean.MessagesBean> messagesBeanList = (ArrayList<WarningInfoBean.DataBean.MessagesBean>) warningInfoBean.getData().getMessages();
                    callBack.onWarningInfoQuerySuccess(messagesBeanList, false);
                } else if (callBack != null) {
                    callBack.onWarningInfoQueryFailed(warningInfoBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                XLog.dReport("detail onFailure ... " + e.getMessage());
                if (callBack != null) {
                    callBack.onWarningInfoQueryFailed(e.getMessage());
                }
            }
        });
    }

    @Override
    public void queryHomePageWarningInfo(int pageNub, WarningInfoQueryCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", pageNub);
        jsonObject.addProperty("page_size", 100);
        String content = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                addHeader("content-type", "application/json").
                url(GET_HOME_PAGE_WARNING_INFO).post(body).build();
        log("queryAllWarningInfo ... " + pageNub + "," + content);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Gson gson = new Gson();
                WarningInfoBean warningInfoBean = gson.fromJson(msg, WarningInfoBean.class);
                if (warningInfoBean.getCode() == 0 && callBack != null && warningInfoBean.getData() != null) {
                    ArrayList<WarningInfoBean.DataBean.MessagesBean> messagesBeanList = (ArrayList<WarningInfoBean.DataBean.MessagesBean>) warningInfoBean.getData().getMessages();
                    callBack.onWarningInfoQuerySuccess(messagesBeanList, false);
                } else if (callBack != null) {
                    callBack.onWarningInfoQueryFailed(warningInfoBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                XLog.dReport("detail onFailure ... " + e.getMessage());
                if (callBack != null) {
                    callBack.onWarningInfoQueryFailed(e.getMessage());
                }
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
        String content = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                addHeader("content-type", "application/json").
                url(CREATE_FENCE).post(body).build();
        log("createFence ..." + content);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Gson gson = new Gson();
                SimplestResponseBean responseBean = gson.fromJson(msg, SimplestResponseBean.class);
                if (responseBean.getCode() == 0 && callBack != null) {
                    callBack.onFenceCreateSuccess(responseBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onFenceCreateFailed(e.getMessage());
                }
            }
        });
    }

    @Override
    public void createDataMonitor(String name, String startTime, String endTime, ArrayList<String> imeiList, String label, String operation, String value, FenceCreateCallBack callBack) {
        //TODO
    }


    @Override
    public void queryAllFence(int pageNub, FenceQueryCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", pageNub);
        jsonObject.addProperty("page_size", PageSizeForFence);
        jsonObject.addProperty("page_size", PageSizeForFence);
        jsonObject.addProperty("page_size", PageSizeForFence);
        String content = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                addHeader("content-type", "application/json").
                url(FENCE_LIST/*+ Sate7EGMSApplication.getOrgCode()*/).post(body).build();
        log("queryAllFence ... " + content);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Gson gson = new Gson();
                FenceListBean fenceListBean = gson.fromJson(msg, FenceListBean.class);
                if (fenceListBean.getCode() == 0 && fenceListBean.getData() != null && callBack != null) {
                    boolean hasMore = pageNub < fenceListBean.getData().getPageCount();
                    callBack.onFenceQuerySuccess((ArrayList<FenceListBean.DataBean.FenceBean>) fenceListBean.getData().getFenceList(), hasMore);
                } else {
                    callBack.onFenceQueryFailed(fenceListBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onFenceQueryFailed(e.getMessage());
                }
            }
        });
    }

    @Override
    public void queryDetailInfo(String imei, DetailInfoQueryCallBack callBack) {
        Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                addHeader("content-type", "application/json").
                url(GET_DEVICE_INFO + imei).get().build();
        log("queryDetailInfo ... " + imei);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Gson gson = new Gson();
                DeviceDetailInfoBean detailInfoBean = gson.fromJson(msg, DeviceDetailInfoBean.class);
                if (detailInfoBean.getCode() == 0 && callBack != null) {
                    callBack.onDetailQuerySuccess(detailInfoBean);
                } else if (callBack != null) {
                    callBack.onDetailQueryFailed(detailInfoBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onDetailQueryFailed(e.getMessage());
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
        page.addProperty("page_no", 1);
        page.addProperty("page_size", 20);
        jsonObject.add("page", page);
        String content = jsonObject.toString();
        XLog.dReport("content == " + content);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                url(GET_DATA_INFO_BY_DATE + imei/*"867935030002256"*/).
                post(body).
                build();
        log("queryLocationByDate ... " + content);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                XLog.dReport("nani ddd:" + msg);
                Gson gson = new Gson();
                LogInfoByDateBean logInfoByDateBean = gson.fromJson(msg, LogInfoByDateBean.class);
                if (logInfoByDateBean.getCode() == 0 && callBack != null) {
                    callBack.onLocationsListQuerySuccess(logInfoByDateBean);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onLocationsListQueryFailed(e.getMessage());
                }
            }
        });
    }

    @Override
    public void updateDeviceTag(EquipmentListBean.DataBean.Device device, String tag, DeviceUpdateCallBack callBack) {
        //TODO
    }

    @Override
    public void updateDeviceFrq(EquipmentListBean.DataBean.Device device, String tag, DeviceUpdateCallBack callBack) {
        //TODO
    }

    @Override
    public void updateDevicePhone(EquipmentListBean.DataBean.Device device, String phoneNumber, DeviceUpdateCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("imei", device.getImei());
        jsonObject.addProperty("tag", device.getTag());
        jsonObject.addProperty("d_tag", device.getTagQx());
        jsonObject.addProperty("bind_number", phoneNumber);
        jsonObject.addProperty("type", device.getType());
        String content = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                addHeader("content-type", "application/json").
                url(UPDATE_NUMBER_INFO).post(body).build();
        log("updateDevice ... " + phoneNumber);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Gson gson = new Gson();
                SimplestResponseBean responseBean = gson.fromJson(msg, SimplestResponseBean.class);
                if (responseBean.getCode() == 0 && callBack != null) {
                    callBack.onDeviceUpdateSuccess(responseBean.getMsg());
                } else if (callBack != null) {
                    callBack.onDeviceUpdateFailed(responseBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onDeviceUpdateFailed(e.getMessage());
                }
            }
        });
    }
}
