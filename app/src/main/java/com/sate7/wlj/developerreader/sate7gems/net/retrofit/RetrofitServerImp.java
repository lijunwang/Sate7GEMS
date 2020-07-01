package com.sate7.wlj.developerreader.sate7gems.net.retrofit;

import android.content.ContentValues;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.SPUtils;
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
import com.sate7.wlj.developerreader.sate7gems.util.Constants;

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

public class RetrofitServerImp implements Server {
    private final String TAG = "ServerImp";
    private final boolean DEBUG_SERVER = true;
    private final boolean DEBUG_RAW = false;
    private RetrofitGEMSServer mGEMSServer;

    public static RetrofitServerImp getInstance() {
        return RetrofitServerImpHolder.mInstance;
    }

    private RetrofitServerImp() {
        mGEMSServer = new Retrofit.Builder().baseUrl(RetrofitGEMSServer.BASE_URL).
                addConverterFactory(ScalarsConverterFactory.create()).
                addConverterFactory(GsonConverterFactory.create()).
                client(UnsafeOkHttpClient.getUnsafeOkHttpClient()).
                build().create(RetrofitGEMSServer.class);
    }

    private static class RetrofitServerImpHolder {
        private static RetrofitServerImp mInstance = new RetrofitServerImp();
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
                    if (response.code() != 200) {
                        log("login error ... ");
                        return;
                    }
                    LoginBean loginBean = response.body();
                    log("login onResponse ... " + response.code() + "," + loginBean.getCode());
                    if (loginBean.getCode() == 0) {
                        saveNamePwd(userName, pwd);
                        Sate7EGMSApplication.setToken(loginBean.getData().getToken());
//                        Sate7EGMSApplication.setOrgCode(loginBean.getData().getOrgs().get(0).getOrgCode());
                    }
                    if (loginBean.getCode() == 0 && callBack != null) {
                        callBack.onLoginSuccess(loginBean.getData().getToken());
                    } else if (callBack != null) {
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

    public void filterDevices(int pageNumber,ContentValues values,DevicesQueryCallBack callBack){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", pageNumber);
        jsonObject.addProperty("page_size", Server.PageSize);
        jsonObject.addProperty(Constants.FilterDeviceQueryKey, values.getAsString(Constants.FilterDeviceQueryKey));
        jsonObject.addProperty(Constants.FilterDeviceValue, values.getAsString(Constants.FilterDeviceValue));
        jsonObject.addProperty("orderby", "lastUpdateTime");
        jsonObject.addProperty("desc", "-1");
        String body = jsonObject.toString();
        log("filterDevices ww ... " + body + "," + Sate7EGMSApplication.getToken());
        Call<EquipmentListBean> call = mGEMSServer.queryAllEquipments(Sate7EGMSApplication.getToken(), /*"application/json",*/ body);
        call.enqueue(new Callback<EquipmentListBean>() {
            @Override
            public void onResponse(Call<EquipmentListBean> call, Response<EquipmentListBean> response) {
                if (response.code() != 200) {
                    log("queryAllDevices error ... ");
                    return;
                }
                EquipmentListBean equipmentListBean = response.body();
                log("filterDevices onResponse " + equipmentListBean.getCode() + "," + equipmentListBean.getMsg());
                checkIfNeedRefresh(equipmentListBean.getCode(), equipmentListBean.getMsg());
                if (callBack != null && equipmentListBean.getCode() == 0) {
                    ArrayList<EquipmentListBean.DataBean.Device> devices = (ArrayList<EquipmentListBean.DataBean.Device>) equipmentListBean.getData().getDevices();
                    boolean hasMore = pageNumber < equipmentListBean.getData().getPageCount();
                    callBack.onDeviceQuerySuccess(devices == null ? new ArrayList<>() : devices, hasMore);
                    log("filterDevices return data " + hasMore + "," + (devices == null ? " null " : devices.size()));
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
    public void queryAllDevices(int pageNumber,DevicesQueryCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", pageNumber);
        jsonObject.addProperty("page_size", Server.PageSize);
        jsonObject.addProperty("orderby", "lastUpdateTime");
        jsonObject.addProperty("desc", "-1");
        String body = jsonObject.toString();
        log("queryAllDevices ... " + body + "," + Sate7EGMSApplication.getToken());
        Call<EquipmentListBean> call = mGEMSServer.queryAllEquipments(Sate7EGMSApplication.getToken(), /*"application/json",*/ body);
        call.enqueue(new Callback<EquipmentListBean>() {
            @Override
            public void onResponse(Call<EquipmentListBean> call, Response<EquipmentListBean> response) {
                if (response.code() != 200) {
                    log("queryAllDevices error ... ");
                    return;
                }
                EquipmentListBean equipmentListBean = response.body();
                log("queryAllDevices onResponse " + equipmentListBean.getCode() + "," + equipmentListBean.getMsg());
                checkIfNeedRefresh(equipmentListBean.getCode(), equipmentListBean.getMsg());
                if (callBack != null && equipmentListBean.getCode() == 0) {
                    ArrayList<EquipmentListBean.DataBean.Device> devices = (ArrayList<EquipmentListBean.DataBean.Device>) equipmentListBean.getData().getDevices();
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

        if (!DEBUG_RAW) {
            return;
        }
        Call<ResponseBody> bodyCall = mGEMSServer.queryAllEquipmentsRaw(Sate7EGMSApplication.getToken(), body);
        bodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    log("equipment onResponse raw ... " + response.body().string());
//                    log("equipment onResponse raw toString ... " + response.toString());
//                    log("equipment onResponse raw body ... " + response.raw().body().string());

                } catch (IOException e) {
                    e.printStackTrace();
                    log("equipment onResponse IOException ... " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                log("equipment onFailure ... " + t.getMessage());
            }
        });
    }

    @Override
    public void queryAllWarningInfo(int pageNumber, String imei, WarningInfoQueryCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", pageNumber);
//        jsonObject.addProperty("page_size", Server.PageSize);
        jsonObject.addProperty("page_size", 80);//TODO server Bug Fix
        String body = jsonObject.toString();
        Call<WarningInfoBean> call = mGEMSServer.queryAllWarnings(Sate7EGMSApplication.getToken(), body, imei);
        call.enqueue(new Callback<WarningInfoBean>() {
            @Override
            public void onResponse(Call<WarningInfoBean> call, Response<WarningInfoBean> response) {
                if (response.code() != 200) {
                    log("queryAllWarningInfo error ... ");
                    return;
                }
                WarningInfoBean warningInfoBean = response.body();
                int size = warningInfoBean.getData().getMessages().size();
                log("warning info ww ... " + warningInfoBean.getCode() + ", " + warningInfoBean.getMsg() + "," + size);
                callBack.onWarningInfoQuerySuccess((ArrayList<WarningInfoBean.DataBean.MessagesBean>) warningInfoBean.getData().getMessages(), false);
            }

            @Override
            public void onFailure(Call<WarningInfoBean> call, Throwable t) {
                callBack.onWarningInfoQueryFailed(t.getMessage());
            }
        });
    }

    @Override
    public void queryHomePageWarningInfo(int pageNumber, WarningInfoQueryCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", pageNumber);
        jsonObject.addProperty("page_size", 100);//TODO server Bug Fix
        String body = jsonObject.toString();
        Call<WarningInfoBean> call = mGEMSServer.queryHomePageWarnings(Sate7EGMSApplication.getToken(), body);
        call.enqueue(new Callback<WarningInfoBean>() {
            @Override
            public void onResponse(Call<WarningInfoBean> call, Response<WarningInfoBean> response) {
                log("queryHomePageWarningInfo ... " + response.code());
                if (response.code() != 200) {
                    log("queryHomePageWarningInfo error ... ");
                    return;
                }
                WarningInfoBean warningInfoBean = response.body();
                if (warningInfoBean.getCode() == 0) {
                    callBack.onWarningInfoQuerySuccess((ArrayList<WarningInfoBean.DataBean.MessagesBean>) warningInfoBean.getData().getMessages(), false);
                } else {
                    callBack.onWarningInfoQueryFailed(warningInfoBean.getMsg());

                }
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
            gps.add(tmp.longitude);
            gps.add(tmp.latitude);
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
        jsonObject.addProperty("monitor_fence_type", "ACCESS_EXIT_FENCE");
        String body = jsonObject.toString();
        log("create fence body = " + body);
        Call<SimplestResponseBean> create = mGEMSServer.createFence(Sate7EGMSApplication.getToken(), body);
        create.enqueue(new Callback<SimplestResponseBean>() {
            @Override
            public void onResponse(Call<SimplestResponseBean> call, Response<SimplestResponseBean> response) {
                if (response.code() != 200) {
                    log("createFence error ... ");
                    return;
                }
                SimplestResponseBean responseBean = response.body();
                log("create fence onResponse ... " + responseBean.getCode() + "," + responseBean.getMsg());
                if (responseBean.getCode() == 0 && callBack != null) {
                    callBack.onFenceCreateSuccess(responseBean.getMsg());
                } else if (callBack != null) {
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
    public void createDataMonitor(String name, String startTime, String endTime, ArrayList<String> imeiList, String label, String operation, String value, FenceCreateCallBack callBack) {
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
        jsonObject.addProperty("interval", "ONE_SHOT");
        jsonObject.addProperty("mtd", "STATE");
        JsonObject labels = new JsonObject();
        labels.addProperty("label", label);
        labels.addProperty("operation", operation);
        labels.addProperty("value", value);
        jsonObject.add("labels", labels);
        JsonArray imeis = new JsonArray();
        for (String tmp : imeiList) {
            imeis.add(tmp);
        }
        jsonObject.add("imeis", imeis);
        jsonObject.addProperty("monitor_fence_type", "ACCESS_EXIT_FENCE");
        String body = jsonObject.toString();
        log("create state body = " + body);
        /*Call<ResponseBody> call = mGEMSServer.createStateMonitor(Sate7EGMSApplication.getToken(), body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    log("create state onResponse ... " + (response.body() == null ? " null " : response.body().string()));
                    log("create state onResponse code ... " + response.code() + "," + response.message());
                } catch (IOException e) {
                    e.printStackTrace();
                    log("create state IOException ... " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                log("create state onFailure ... " + t.getMessage());
            }
        });*/
        Call<SimplestResponseBean> create = mGEMSServer.createStateMonitor(Sate7EGMSApplication.getToken(), body);
        create.enqueue(new Callback<SimplestResponseBean>() {
            @Override
            public void onResponse(Call<SimplestResponseBean> call, Response<SimplestResponseBean> response) {
                if (response.code() != 200) {
                    callBack.onFenceCreateFailed("server not support!");
                    return;
                }
                SimplestResponseBean responseBean = response.body();
                log("create state onResponse ... " + responseBean);
                if (responseBean.getCode() == 0 && callBack != null) {
                    callBack.onFenceCreateSuccess(responseBean.getMsg());
                } else if (callBack != null) {
                    callBack.onFenceCreateFailed(responseBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call<SimplestResponseBean> call, Throwable t) {
                log("create state onFailure ... " + t.getMessage());
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
        jsonObject.addProperty("page_size", Server.PageSizeForFence);
        String body = jsonObject.toString();
        Call<FenceListBean> call = mGEMSServer.queryAllFences(Sate7EGMSApplication.getToken(), body);
        call.enqueue(new Callback<FenceListBean>() {
            @Override
            public void onResponse(Call<FenceListBean> call, Response<FenceListBean> response) {
                if (response.code() != 200) {
                    log("queryAllFence error ... ");
                    return;
                }
                FenceListBean fenceListBean = response.body();
                ArrayList<FenceListBean.DataBean.FenceBean> fenceBeans = (ArrayList<FenceListBean.DataBean.FenceBean>) fenceListBean.getData().getFenceList();
//                log("fence onResponse ... " + fenceListBean.getCode() + "," + fenceListBean.getMsg() + "," + fenceBeans);
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
                if (response.code() != 200) {
                    log("queryDetailInfo error ... ");
                    return;
                }
                DeviceDetailInfoBean detailInfoBean = response.body();
                log("queryDetailInfo response ... " + detailInfoBean.getCode() + "," + detailInfoBean.getMsg());
                if (detailInfoBean.getCode() == 0 && callBack != null) {
                    callBack.onDetailQuerySuccess(detailInfoBean);
                }
            }

            @Override
            public void onFailure(Call<DeviceDetailInfoBean> call, Throwable t) {
                log("queryDetailInfo onFailure ... " + t.getMessage());
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
                if (response.code() != 200) {
                    log("queryLocationByDate error ... ");
                    return;
                }
                LogInfoByDateBean logInfoByDateBean = response.body();
                if (logInfoByDateBean.getCode() == 0 && callBack != null) {
                    callBack.onLocationsListQuerySuccess(logInfoByDateBean);
//                    log("onResponse by date ... " + logInfoByDateBean.getData().getLocation().size() + "," + logInfoByDateBean.getData().getMessages().size());
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
    public void updateDeviceTag(EquipmentListBean.DataBean.Device device, String tag, DeviceUpdateCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("imei", device.getImei());
        jsonObject.addProperty("tag", tag);
        jsonObject.addProperty("d_tag", device.getTagQx());
        jsonObject.addProperty("bind_number", device.getBindNumber());
        jsonObject.addProperty("type", device.getType());
        String content = jsonObject.toString();
        log("updateDevice content ... " + content);
        Call<SimplestResponseBean> update = mGEMSServer.updateNumber(Sate7EGMSApplication.getToken(), content);
        update.enqueue(new Callback<SimplestResponseBean>() {
            @Override
            public void onResponse(Call<SimplestResponseBean> call, Response<SimplestResponseBean> response) {
                if (response.code() != 200) {
                    log("updateDeviceTag error ... ");
                    return;
                }
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

    @Override
    public void updateDeviceFrq(EquipmentListBean.DataBean.Device device, String frq, DeviceUpdateCallBack callBack) {
        JsonObject jsonObject = new JsonObject();
        // 12h ：12小时上报一次　　　2d: 2天上报一次   0: 立即上报
        jsonObject.addProperty("interval", frq);
        String content = jsonObject.toString();
        Call<SimplestResponseBean> update = mGEMSServer.updateFrq(Sate7EGMSApplication.getToken(), content);
        update.enqueue(new Callback<SimplestResponseBean>() {
            @Override
            public void onResponse(Call<SimplestResponseBean> call, Response<SimplestResponseBean> response) {
                log("updateDeviceFrq onResponse ..." + response.code() + "," + response.message());
            }

            @Override
            public void onFailure(Call<SimplestResponseBean> call, Throwable t) {
                log("updateDeviceFrq onFailure ..." + t.getMessage());
            }
        });
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
        log("updateDevice content ... " + content);
        Call<SimplestResponseBean> update = mGEMSServer.updateNumber(Sate7EGMSApplication.getToken(), content);
        update.enqueue(new Callback<SimplestResponseBean>() {
            @Override
            public void onResponse(Call<SimplestResponseBean> call, Response<SimplestResponseBean> response) {
                if (response.code() != 200) {
                    log("updateDevicePhone error ... ");
                    return;
                }
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

    private void saveNamePwd(String userName, String pwd) {
        log("saveNamePwd ..." + userName + "," + pwd);
        SPUtils.getInstance().put(Constants.LOGIN_USER_NAME, userName);
        SPUtils.getInstance().put(Constants.LOGIN_PWD, pwd);
    }

    private String getSavedName() {
        return SPUtils.getInstance().getString(Constants.LOGIN_USER_NAME, "qx_admin");
    }

    private String getSavedPwd() {
        return SPUtils.getInstance().getString(Constants.LOGIN_PWD, "qx");
    }

    private void checkIfNeedRefresh(int code, String msg) {
        log("checkIfNeedRefresh ... " + code + "," + msg);
        if (code == 1004 && msg.contains("token")) {
            login(getSavedName(), getSavedPwd(), null);
        }
    }

    public void getHomeArticle(){
        RetrofitGEMSServer retrofitServer = new Retrofit.Builder().baseUrl("https://www.wanandroid.com/").
                addConverterFactory(ScalarsConverterFactory.create()).
                addConverterFactory(GsonConverterFactory.create()).
                client(UnsafeOkHttpClient.getUnsafeOkHttpClient()).
                build().create(RetrofitGEMSServer.class);

        Call<ResponseBody> call = retrofitServer.getHomeArticle(0);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String msg = response.body().string();
                    log("get home article onResponse ..." + msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    log("get home article onResponse IO Exception  ..." + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                log("get home article onFailure ...");
            }
        });

    }
}
