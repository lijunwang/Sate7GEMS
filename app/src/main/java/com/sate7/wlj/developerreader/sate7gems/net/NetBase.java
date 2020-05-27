package com.sate7.wlj.developerreader.sate7gems.net;

public interface NetBase {
    String BASE_URL = "https://qx-new.tsingk.net:8443";
    String LOGIN_TYPE_USERNAME_PASSWORD_KEY = "type";
    int LOGIN_TYPE_USERNAME_PASSWORD_VALUE = 1;
    String LOGIN_USER_NAME_KEY = "user_name";
    String LOGIN_USER_NAME_VALUE = "qx_admin";
    String LOGIN_USER_PASSWORD_KEY = "pass";
    String LOGIN_USER_PASSWORD_VALUE = "qx";
    String LOGIN_URL = BASE_URL + "/api/v1/auth/web/login";
    String LIST_EQUIPMENT_URL = BASE_URL + "/api/v1/device/query/list";
    String GET_DEVICE_INFO = BASE_URL + "/api/v1/device/query/imei/";
    String GET_WARNING_INFO = BASE_URL + "/api/v1/message/query/imei/warning/";
    String GET_HOME_PAGE_WARNING_INFO = BASE_URL + "/api/v1/message/query/warning";
    String UPDATE_NUMBER_INFO = BASE_URL + "/api/v1/device/tag/update";
//    String FENCE_LIST = BASE_URL + "/api/v1/task/query/list/";//{org_code}
//    String FENCE_LIST = BASE_URL + "/api/v1/task/list/";//{org_code}
    String FENCE_LIST = BASE_URL + "/api/v1/task/list";
    String CREATE_FENCE = BASE_URL + "/api/v1/task/create";//{org_code}
    String GET_DATA_INFO_BY_DATE = BASE_URL + "/api/v1/device/query/logs/date/";
}
