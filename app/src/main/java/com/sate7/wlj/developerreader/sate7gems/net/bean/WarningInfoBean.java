package com.sate7.wlj.developerreader.sate7gems.net.bean;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WarningInfoBean {

    /**
     * code : 0
     * msg : SUCCESS
     * data : {"messages":[{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°19\u203252\u2032\u2032, 39°51\u203259\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-20 07:27"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°19\u203251\u2032\u2032, 39°51\u203256\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-20 07:27"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°19\u203252\u2032\u2032, 39°51\u203252\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-20 07:26"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°19\u203247\u2032\u2032, 39°51\u203249\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-20 07:26"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°19\u203241\u2032\u2032, 39°51\u203246\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-20 07:26"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°19\u203239\u2032\u2032, 39°51\u203242\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-20 07:26"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°19\u203239\u2032\u2032, 39°51\u203239\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-20 07:26"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°19\u203239\u2032\u2032, 39°51\u203239\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-20 07:26"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°18\u20327\u2032\u2032, 39°51\u203233\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-19 19:22"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°18\u20327\u2032\u2032, 39°51\u203233\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-19 19:22"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°18\u20327\u2032\u2032, 39°51\u203233\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-19 19:21"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°18\u20327\u2032\u2032, 39°51\u203233\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-19 19:21"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°18\u20327\u2032\u2032, 39°51\u203232\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-19 19:21"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°18\u20327\u2032\u2032, 39°51\u203232\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-19 19:21"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°18\u20327\u2032\u2032, 39°51\u203233\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-19 19:21"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°18\u20327\u2032\u2032, 39°51\u203233\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-19 19:21"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°18\u20326\u2032\u2032, 39°51\u203233\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-19 19:20"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°18\u20325\u2032\u2032, 39°51\u203233\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-19 19:20"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°18\u20324\u2032\u2032, 39°51\u203234\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-19 19:20"},{"device":"356963090002204 (测试（张阀手机）) ","message":"位置警告 gps[116°18\u20324\u2032\u2032, 39°51\u203234\u2032\u2032] 类型:出 围栏[托尔斯泰]","date_time":"2020-03-19 19:20"}]}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<MessagesBean> messages;

        public List<MessagesBean> getMessages() {
            return messages;
        }

        public void setMessages(List<MessagesBean> messages) {
            this.messages = messages;
        }

        public static class MessagesBean {
            /**
             * device : 356963090002204 (测试（张阀手机）)
             * message : 位置警告 gps[116°19′52′′, 39°51′59′′] 类型:出 围栏[托尔斯泰]
             * date_time : 2020-03-20 07:27
             */

            private String device;
            private String message;
            @SerializedName("date_time")
            private String dateTime;

            public String getDevice() {
                return device;
            }

            public void setDevice(String device) {
                this.device = device;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getDateTime() {
                return dateTime;
            }

            public void setDateTime(String dateTime) {
                this.dateTime = dateTime;
            }

            @NonNull
            @Override
            public String toString() {
                return "device:" + device + ",message:" + message + ",dataTime:" + dateTime;
            }
        }
    }
}
