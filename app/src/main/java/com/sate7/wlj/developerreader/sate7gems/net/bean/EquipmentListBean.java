package com.sate7.wlj.developerreader.sate7gems.net.bean;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EquipmentListBean {

    /**
     * code : 0
     * msg : SUCCESS
     * data : {"page_no":1,"total_count":27,"page_count":2,"page_size":20,"devices":[{"imei":"867935030003213","build_number":"","mode":"未知","type":"M2M","produce_date":null,"tag":"测试（北京数据）","tag_qx":"水利","register_time":"2020-02-20 17:51","last_update_time":"2020-03-06 09:37","bind_number":"13720020329"},{"imei":"356963090002204","build_number":"","mode":"未知","type":"V1","produce_date":null,"tag":"测试（张阀手机）","tag_qx":"水利","register_time":"2020-02-21 09:04","last_update_time":"2020-03-13 12:55","bind_number":""},{"imei":"863323040678023","build_number":"","mode":"未知","type":"V1","produce_date":null,"tag":"测试（杨手机）","tag_qx":"水利","register_time":"2020-02-21 09:11","last_update_time":"2020-03-04 08:20","bind_number":""},{"imei":"356963090002205","build_number":"","mode":"未知","type":"M2M","produce_date":null,"tag":"","tag_qx":"","register_time":"","last_update_time":"2020-02-21 09:22","bind_number":""}]}
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
        /**
         * page_no : 1
         * total_count : 27
         * page_count : 2
         * page_size : 20
         * devices : [{"imei":"867935030003213","build_number":"","mode":"未知","type":"M2M","produce_date":null,"tag":"测试（北京数据）","tag_qx":"水利","register_time":"2020-02-20 17:51","last_update_time":"2020-03-06 09:37","bind_number":"13720020329"},{"imei":"356963090002204","build_number":"","mode":"未知","type":"V1","produce_date":null,"tag":"测试（张阀手机）","tag_qx":"水利","register_time":"2020-02-21 09:04","last_update_time":"2020-03-13 12:55","bind_number":""},{"imei":"863323040678023","build_number":"","mode":"未知","type":"V1","produce_date":null,"tag":"测试（杨手机）","tag_qx":"水利","register_time":"2020-02-21 09:11","last_update_time":"2020-03-04 08:20","bind_number":""},{"imei":"356963090002205","build_number":"","mode":"未知","type":"M2M","produce_date":null,"tag":"","tag_qx":"","register_time":"","last_update_time":"2020-02-21 09:22","bind_number":""}]
         */

        private int page_no;
        @SerializedName("total_count")
        private int totalCount;
        @SerializedName("page_count")
        private int pageCount;
        @SerializedName("page_size")
        private int pageSize;
        private List<Device> devices;
        private List<Device> m2mDevices;

        public List<Device> getM2mDevices() {
            ArrayList<Device> m2m = new ArrayList<>();
            for (Device device : m2m) {
                if (device.getType().equals("M2M")) {
                    m2m.add(device);
                }
            }
            return m2m;
        }

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public List<Device> getDevices() {
            return devices;
        }

        public void setDevices(List<Device> devices) {
            this.devices = devices;
        }

        public static class Device implements Parcelable, Comparable<Device> {
            /**
             * imei : 867935030003213
             * build_number :
             * mode : 未知
             * type : M2M
             * produce_date : null
             * tag : 测试（北京数据）
             * tag_qx : 水利
             * register_time : 2020-02-20 17:51
             * last_update_time : 2020-03-06 09:37
             * bind_number : 13720020329
             */

            private String imei;
            @SerializedName("build_number")
            private String buildNumber;
            private String mode;
            private String type;
            @SerializedName("produceDate")
            private Object produce_date;
            private String tag;
            @SerializedName("tag_qx")
            private String tagQx;
            @SerializedName("register_time")
            private String registerTime;
            @SerializedName("last_update_time")
            private String lastUpdateTime;
            @SerializedName("bind_number")
            private String bindNumber;
            private LatLng location;

            public LatLng getLocation() {
                return location;
            }

            public void setLocation(LatLng location) {
                this.location = location;
            }
            public Device(String imei){
                this.imei = imei;
            }
            protected Device(Parcel in) {
                imei = in.readString();
                buildNumber = in.readString();
                mode = in.readString();
                type = in.readString();
                tag = in.readString();
                tagQx = in.readString();
                registerTime = in.readString();
                lastUpdateTime = in.readString();
                bindNumber = in.readString();
                isChecked = in.readByte() != 0;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(imei);
                dest.writeString(buildNumber);
                dest.writeString(mode);
                dest.writeString(type);
                dest.writeString(tag);
                dest.writeString(tagQx);
                dest.writeString(registerTime);
                dest.writeString(lastUpdateTime);
                dest.writeString(bindNumber);
                dest.writeByte((byte) (isChecked ? 1 : 0));
            }

            public static final Creator<Device> CREATOR = new Creator<Device>() {
                @Override
                public Device createFromParcel(Parcel in) {
                    return new Device(in);
                }

                @Override
                public Device[] newArray(int size) {
                    return new Device[size];
                }
            };

            public boolean isChecked() {
                return isChecked;
            }

            public void setChecked(boolean checked) {
                isChecked = checked;
            }

            private boolean isChecked = false;

            public String getImei() {
                return imei;
            }

            public void setImei(String imei) {
                this.imei = imei;
            }


            public String getMode() {
                return mode;
            }

            public void setMode(String mode) {
                this.mode = mode;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public Object getProduce_date() {
                return produce_date;
            }

            public void setProduce_date(Object produce_date) {
                this.produce_date = produce_date;
            }

            public String getTag() {
                return TextUtils.isEmpty(tag) ? tagQx : tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            @NonNull
            @Override
            public String toString() {
                return "(imei == " + imei + ",type ==" + type + ",tag == " + tag + ",lastUpdateTime==" + lastUpdateTime  + ",isChecked == " + isChecked + ")";
            }

            public String getLastUpdateTime() {
                return lastUpdateTime;
            }

            public void setLastUpdateTime(String lastUpdateTime) {
                this.lastUpdateTime = lastUpdateTime;
            }

            public String getBuildNumber() {
                return buildNumber;
            }

            public void setBuildNumber(String buildNumber) {
                this.buildNumber = buildNumber;
            }

            public String getTagQx() {
                return tagQx;
            }

            public void setTagQx(String tagQx) {
                this.tagQx = tagQx;
            }

            public String getRegisterTime() {
                return registerTime;
            }

            public void setRegisterTime(String registerTime) {
                this.registerTime = registerTime;
            }

            public String getBindNumber() {
                return TextUtils.isEmpty(bindNumber) ? "unknown" : bindNumber;
            }

            public void setBindNumber(String bindNumber) {
                this.bindNumber = bindNumber;
            }

            @Override
            public boolean equals(@Nullable Object obj) {
                if(!(obj instanceof Device)){
                    return false;
                }else{
                    Device device = (Device) obj;
                    XLog.dReport("Device equals ww ... " + device.getTag() + "," + this.tag);
                    boolean imeiEquals = device.getImei().equals(imei);
                    if(imeiEquals && device.getBindNumber() != null && bindNumber != null){
                        XLog.dReport("Device equals aa22 ... " + device.getTag() + "," + this.tag);
                        device.bindNumber = "1234";
                    }else if(imeiEquals && device.getTag() != null && tag!= null){
                        XLog.dReport("Device equals bb22 ... " + device.getTag() + "," + this.tag);
                        device.tag = "ABC";
                    }
                    return imeiEquals;
                }
            }

            @Override
            public int hashCode() {
//                XLog.dReport("Device hashCode ... " + this.imei);
                return imei.hashCode();
            }

            @Override
            public int compareTo(Device o) {
//                return lastUpdateTime.compareTo(o.getLastUpdateTime());
                return o.getLastUpdateTime().compareTo(lastUpdateTime);
            }
        }
    }
}
