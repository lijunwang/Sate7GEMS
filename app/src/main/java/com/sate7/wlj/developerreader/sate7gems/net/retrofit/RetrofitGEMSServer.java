package com.sate7.wlj.developerreader.sate7gems.net.retrofit;

import com.sate7.wlj.developerreader.sate7gems.net.bean.DeviceDetailInfoBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.LogInfoByDateBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.LoginBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.SimplestResponseBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.WarningInfoBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitGEMSServer {
    String BASE_URL = "https://qx-new.tsingk.net:8443/";
    String PATH_LOGIN = "api/v1/auth/web/login";
    String PATH_EQUIPMENTS = "api/v1/device/query/list";
    String PATH_WARNINGS = "api/v1/message/query/imei/warning/{imei}";
    String PATH_HOME_PAGE_WARNINGS = "api/v1/message/query/warning";
//    String PATH_FENCES = "api/v1/task/list/{org_code}";
    String PATH_FENCES = "api/v1/task/list";
    String PATH_DETAIL = "api/v1/device/query/imei/{imei}";
    String PATH_QUERY_BY_DATE = "api/v1/device/query/logs/date/{imei}";
    String PATH_UPDATE_DEVICE = "api/v1/device/tag/update";
    String PATH_CREATE_FENCE = "api/v1/task/create";
    String PATH_UPDATE_FRQ = "api/v1/device/report_interval/update/{imei}";

    @Headers("Content-Type:application/json")
    @POST(PATH_LOGIN)
    Call<LoginBean> login(@Body String body);

    @Headers("Content-Type:application/json")
    @POST(PATH_EQUIPMENTS)
    Call<EquipmentListBean> queryAllEquipments(@Header("Authorization") String token, @Body String body);

    @Headers("Content-Type:application/json")
    @POST(PATH_EQUIPMENTS)
    Call<ResponseBody> queryAllEquipmentsRaw(@Header("Authorization") String token, @Body String body);

    @Headers("Content-Type:application/json")
    @POST(PATH_WARNINGS)
    Call<WarningInfoBean> queryAllWarnings(@Header("Authorization") String token, @Body String body, @Path("imei") String imei);
//    Call<ResponseBody> queryAllWarnings(@Header("Authorization") String token, @Body String body, @Path("imei") String imei);

    @Headers("Content-Type:application/json")
    @POST(PATH_HOME_PAGE_WARNINGS)
    Call<WarningInfoBean> queryHomePageWarnings(@Header("Authorization") String token, @Body String body);

    //登陆得到org_code,再用org_code去查询所有的围栏信息;
    @Headers("Content-Type:application/json")
    @POST(PATH_FENCES)
    Call<FenceListBean> queryAllFences(@Header("Authorization") String token, @Body String body);

    //获取设备详细消息,包括最新的位置信息
    @Headers("Content-Type:application/json")
    @GET(PATH_DETAIL)
    Call<DeviceDetailInfoBean> queryDetailInfo(@Header("Authorization") String token, @Path("imei") String imei);

    //获取设备详细消息,包括最新的位置信息
    @Headers("Content-Type:application/json")
    @POST(PATH_QUERY_BY_DATE)
    Call<LogInfoByDateBean> queryLocationsListByDate(@Header("Authorization") String token, @Body String body, @Path("imei") String imei);

    //更新设备属性
    @Headers("Content-Type:application/json")
    @POST(PATH_UPDATE_DEVICE)
    Call<SimplestResponseBean> updateNumber(@Header("Authorization") String token, @Body String body);

    //更新设备上报频率
    @Headers("Content-Type:application/json")
    @PUT(PATH_UPDATE_FRQ)
    Call<SimplestResponseBean> updateFrq(@Header("Authorization") String token, @Body String body);

    //创建围栏
    @Headers("Content-Type:application/json")
    @POST(PATH_CREATE_FENCE)
    Call<SimplestResponseBean> createFence(@Header("Authorization") String token,@Body String body);

    //创建状态监听
    @Headers("Content-Type:application/json")
    @POST(PATH_CREATE_FENCE)
//    Call<ResponseBody> createStateMonitor(@Header("Authorization") String token,@Body String body);
    Call<SimplestResponseBean> createStateMonitor(@Header("Authorization") String token,@Body String body);


    //Test
    @GET("article/list/{id}/json")
    Call<ResponseBody> getHomeArticle(@Path("id") int id);

}
