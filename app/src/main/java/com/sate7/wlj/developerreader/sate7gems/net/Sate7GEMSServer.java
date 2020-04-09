package com.sate7.wlj.developerreader.sate7gems.net;

import android.content.ContentValues;

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
import com.sate7.wlj.developerreader.sate7gems.net.bean.WarningInfoBean;
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

public class Sate7GEMSServer implements NetBase {
    private OkHttpClient okHttpClient;

    private static class Sate7GEMSServerHolder {
        private static Sate7GEMSServer mInstance = new Sate7GEMSServer();
    }

    private Sate7GEMSServer() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        okHttpClient = builder.
                sslSocketFactory(RxUtils.createSSLSocketFactory()).
                hostnameVerifier(new RxUtils.TrustAllHostnameVerifier()).
                build();
    }

    public static Sate7GEMSServer getInstance() {
        return Sate7GEMSServerHolder.mInstance;
    }


    public void login(String userName, String password, LoginCallBack callback) {
        XLog.dReport("login ... " + userName + "," + password);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LOGIN_TYPE_USERNAME_PASSWORD_KEY, LOGIN_TYPE_USERNAME_PASSWORD_VALUE);
        jsonObject.addProperty(LOGIN_USER_NAME_KEY, userName);
        jsonObject.addProperty(LOGIN_USER_PASSWORD_KEY, password);
        String content = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder().
                url(LOGIN_URL).post(body).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public interface ListEquipmentCallBack {
        void onResponse(ArrayList<EquipmentListBean.DataBean.Device> devices);
    }

    public void listEquipment(ListEquipmentCallBack callBack) {
        if (Sate7EGMSApplication.getEquipmentsList() != null) {
            callBack.onResponse(Sate7EGMSApplication.getEquipmentsList());
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", 1);
        jsonObject.addProperty("page_size", 80);
        String content = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        final Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                url(LIST_EQUIPMENT_URL).post(body).build();
        XLog.dReport("listEquipment ... " + Sate7EGMSApplication.getToken());
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.dReport("listEquipment onFailure ... " + e.getMessage());
                callBack.onResponse(new ArrayList<>());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Gson gson = new Gson();
                EquipmentListBean deviceBean = gson.fromJson(body, EquipmentListBean.class);
                if (deviceBean.getCode() == 0) {
                    List<EquipmentListBean.DataBean.Device> deviceList = deviceBean.getData().getDevices();
                    XLog.dReport("onResponse listEquipment counts ... " + deviceList.size());
                    callBack.onResponse((ArrayList<EquipmentListBean.DataBean.Device>) deviceList);
                    /*for (EquipmentListBean.DataBean.Device device : deviceList) {
                        XLog.dReport("onResponse device ... " + device.getImei() + "," + device.getType() + "," + device.getTag());
                    }*/
                    Sate7EGMSApplication.setEquipmentsList((ArrayList<EquipmentListBean.DataBean.Device>) deviceList);
                } else {
                    XLog.dReport("onResponse listEquipment failed ... " + body);
                    if (body.contains("No such token")) {
                        Sate7EGMSApplication.refreshToken();
                    }
                }

            }
        });
    }

    public void getLatestLocationInfo(String imei, LocationQueryListener listener) {
        Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                addHeader("content-type", "application/json").
                url(GET_DEVICE_INFO + imei).get().build();
        XLog.dReport("getLatestLocationInfo url == " + request.url());
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.dReport("getLatestLocationInfo detail onFailure ... " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Gson gson = new Gson();
//                XLog.dReport("Location detail gson ... " + msg);
                DeviceDetailInfoBean detailInfoBean = gson.fromJson(msg, DeviceDetailInfoBean.class);
                if (detailInfoBean.getCode() == 0 && detailInfoBean.getData() != null) {
                    EquipmentListBean.DataBean.Device device = detailInfoBean.getData().getBasic();
                    List<List<Double>> locationList = detailInfoBean.getData().getLocation();
                    XLog.dReport("Location detail onResponse ... " + device + "," + locationList.size());
                    listener.onLocationQuery(detailInfoBean);
                    /*if (locationList.size() >= 1) {
                        List<Double> l = locationList.get(0);//first or last;
                    }*/
                }

            }
        });
    }

    public void getDetailInfo(String imei, LocationQueryListener listener) {
//        String test = "867935030002256";
        Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                addHeader("content-type", "application/json").
                url(GET_DEVICE_INFO + imei).get().build();
        XLog.dReport("url == " + request.url());
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.dReport("detail onFailure ... " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Gson gson = new Gson();
                DeviceDetailInfoBean detailInfoBean = gson.fromJson(msg, DeviceDetailInfoBean.class);
                if (detailInfoBean.getCode() == 0 && detailInfoBean.getData() != null) {
                    EquipmentListBean.DataBean.Device device = detailInfoBean.getData().getBasic();
                    List<List<Double>> locationList = detailInfoBean.getData().getLocation();
//                    XLog.dReport("detail onResponse 22 ... " + device + "," + locationList.size());
                    for (List<Double> l : locationList) {
//                        XLog.dReport("detail ww ... " + l.size() + "," + l.get(0) + "," + l.get(1));
                    }
                }

            }
        });
    }

    public interface LocationQueryListener {
        void onLocationQuery(DeviceDetailInfoBean deviceDetailInfoBean);
    }

    public interface LocationsCallback {
        void onLocationsGet(ArrayList<LatLng> points);
    }

    public void getLocationInfoByDate(String imei, LocationsCallback callback) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JsonObject jsonObject = new JsonObject();
        JsonArray types = new JsonArray();
        types.add("LOCATION_REC");
        jsonObject.add("types", types);
        JsonObject date = new JsonObject();
        Calendar calendar = Calendar.getInstance();
        String end = simpleDateFormat.format(calendar.getTime());
        date.addProperty("end", end);
        calendar.add(Calendar.MONTH, -1);
        String start = simpleDateFormat.format(calendar.getTime());
        XLog.dReport("getLocationInfoByDate ... " + imei + "," + start + "," + end);
        date.addProperty("start", start);
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
        XLog.dReport("url == " + request.url());
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.dReport("getDataInfoByDate onFailure ... " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                XLog.dReport("nani ddd:" + msg);
                Gson gson = new Gson();
                LogInfoByDateBean logInfoByDateBean = gson.fromJson(msg, LogInfoByDateBean.class);
                if (logInfoByDateBean.getCode() == 0) {
                    EquipmentListBean.DataBean.Device device = logInfoByDateBean.getData().getBasic();
                    XLog.dReport("getDataInfoByDate device info ... " + device);
                    List<List<Double>> list = logInfoByDateBean.getData().getLocation();
                    XLog.dReport("getDataInfoByDate location info size ... " + list.size());
                    ArrayList<LatLng> locations = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        XLog.dReport("getDataInfoByDate lat lng ... " + i + "," + list.get(i).size() + "," + list.get(i).get(0) + "," + list.get(i).get(1));
                        locations.add(new LatLng(list.get(i).get(1), list.get(i).get(0)));
                    }
                    callback.onLocationsGet(locations);
                }
            }
        });
    }

    public void getWarningInfo(String imei, WarningViewModel.WarningInfoCallBack callBack) {
        String test = "356963090002204";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", 1);
        jsonObject.addProperty("page_size", 20);
        String content = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                addHeader("content-type", "application/json").
                url(GET_WARNING_INFO + imei).post(body).build();
//        XLog.dReport("getWarningInfo url == " + request.url());
//        XLog.dReport("getWarningInfo content == " + content);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.dReport("detail onFailure ... " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Gson gson = new Gson();
                WarningInfoBean warningInfoBean = gson.fromJson(msg, WarningInfoBean.class);
                if (warningInfoBean.getCode() == 0 && warningInfoBean.getData() != null) {
                    List<WarningInfoBean.DataBean.MessagesBean> messagesBeanList = warningInfoBean.getData().getMessages();
//                    XLog.dReport("getWarningInfo onResponse ... " + messagesBeanList.size() + "," + messagesBeanList);
                    callBack.onWarningInfoResult(messagesBeanList);
                }
            }
        });
    }

    public interface BindCallback {
        void onBindResult(String msg);
    }

    public void bindNumber(EquipmentListBean.DataBean.Device device, String lineNumber, BindCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("imei", device.getImei());
        jsonObject.addProperty("tag", device.getTag());
        jsonObject.addProperty("d_tag", device.getTagQx());
        jsonObject.addProperty("bind_number", lineNumber);
        jsonObject.addProperty("type", device.getType());
        String content = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                addHeader("content-type", "application/json").
                url(UPDATE_NUMBER_INFO).post(body).build();
        XLog.dReport("getWarningInfo url == " + request.url());
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.dReport("bindNumber onFailure ... " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String msg = response.body().string();
                    XLog.dReport("bindNumber msg ... " + msg);
                    callback.onBindResult(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    XLog.dReport("bindNumber IOException ... " + e.getMessage());
                }
            }
        });
    }


    public static abstract class LoginCallBack implements Callback {

        @Override
        public void onFailure(Call call, IOException e) {
            loginFailed("LoginFailed" + e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String msg = response.body().string();
            XLog.dReport("activity_login onResponse ... " + msg + "," + response.code());
            Gson gson = new Gson();
            LoginBean loginBean = gson.fromJson(msg, LoginBean.class);
            if (loginBean.getCode() == 0 && loginBean.getMsg().equals("SUCCESS")) {
                String token = loginBean.getData().getToken();
                loginSuccess(token);
                Sate7EGMSApplication.setToken(token);
                Sate7EGMSApplication.setOrgCode(loginBean.getData().getOrgs().get(0).getOrgCode());
            }
        }

        public abstract void loginSuccess(String token);

        public abstract void loginFailed(String msg);
    }

    public interface OnFenceListQueryCallback {
        void onFenceListQuery(ArrayList<FenceListBean.DataBean.FenceBean> result);
    }

    public void queryFenceList(String orgCode, OnFenceListQueryCallback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page_no", 1);
        jsonObject.addProperty("page_size", 100);
        String content = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder().
                addHeader("Authorization", Sate7EGMSApplication.getToken()).
                addHeader("content-type", "application/json").
                url(FENCE_LIST + orgCode).post(body).build();
        XLog.dReport("queryFenceList url == " + request.url());
        XLog.dReport("queryFenceList content == " + content);
        XLog.dReport("queryFenceList header == " + request.headers().toString());

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.dReport("queryFenceList onFailure ... " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                Gson gson = new Gson();
                FenceListBean fenceListBean = gson.fromJson(msg, FenceListBean.class);
                try {
                    if (fenceListBean.getCode() == 0) {
//                    XLog.dReport("queryFenceList msg ... " + fenceListBean.getData().getTotalCount());
                        ArrayList<FenceListBean.DataBean.FenceBean> fenceBeans = (ArrayList<FenceListBean.DataBean.FenceBean>) fenceListBean.getData().getFenceList();
//                    XLog.dReport("queryFenceList fences ... " + fenceBeans);
                        callback.onFenceListQuery(fenceBeans);
                    }
                } catch (Exception e) {
                    XLog.dReport("queryFenceList Exception ... " + e.getMessage());
                }

            }
        });
    }

    public interface FaceCreateCallback {
        void onFenceCreateResult(String msg);
    }

    public void createFence(String name, String start, String end, ArrayList<LatLng> vertexList, ArrayList<String> imeiList, FaceCreateCallback callback) {
    /*   "name"             :
        "start_date"       : yyyy-MM-dd
        "end_date"         : yyyy-MM-dd
　　    “day_type”　　　　　: 　WEEK/MONTH/
         "days"             : [], // 选择的周几或月中的某日
        "interval"         : INTERVAL/ONE_SHOT
        "mtd"              :   GEOFENCE/STATE  STATE:状态监控  GEOFENCE:围栏监控
        "gps"              : [lng1, lat1, lng2,lat2]..经纬度数组 该属性主要针对围栏监控
        "labels"           : [{label: '标签', operation: '>', value: '14'}],
        "imeis"            :[]*/

        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("name", "WangLijun22");
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("start_date", start);
        jsonObject.addProperty("end_date", end);
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
//        gps.add(116.71232);
//        gps.add(24.12457);
//        gps.add(116.34567);
//        gps.add(24.72345);
//        gps.add(116.34538);
//        gps.add(24.72326);
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
//        imeis.add("867935030003213");
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
        XLog.dReport("createFence url == " + request.url());
        XLog.dReport("createFence data == " + content);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.dReport("createFence onFailure ... " + e.getMessage());
                callback.onFenceCreateResult("failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = response.body().string();
                XLog.dReport("createFence msg ... " + msg);
                callback.onFenceCreateResult(msg);
            }
        });
    }
}
