package com.sate7.wlj.developerreader.sate7gems.net.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.ArrayList;
import java.util.List;

public class FenceListBean {

    /**
     * code : 0
     * msg : SUCCESS
     * data : {"page_no":1,"total_count":17,"page_count":1,"page_size":20,"mt_list":[{"name":"0","imeis":[],"start_date":"2020-02-19","end_date":"2020-02-27","geofence_data":[116.46056662170871,39.91815564878112,116.4760844718016,39.91691111555171,116.4753453839385,39.91086790029015,116.4596793734743,39.91001278576602],"mdt":"GEOFENCE","mi":"ONE_SHOT","operators":null},{"name":"ss","imeis":[],"start_date":"2020-02-21","end_date":"2020-02-28","geofence_data":[116.43420270122199,39.9343584262252,116.44563188353847,39.93423377921478,116.44929430942302,39.925585866084994,116.43514950481111,39.9258913664044],"mdt":"GEOFENCE","mi":"ONE_SHOT","operators":null}]}
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
         * total_count : 17
         * page_count : 1
         * page_size : 20
         * mt_list : [{"name":"0","imeis":[],"start_date":"2020-02-19","end_date":"2020-02-27","geofence_data":[116.46056662170871,39.91815564878112,116.4760844718016,39.91691111555171,116.4753453839385,39.91086790029015,116.4596793734743,39.91001278576602],"mdt":"GEOFENCE","mi":"ONE_SHOT","operators":null},{"name":"ss","imeis":[],"start_date":"2020-02-21","end_date":"2020-02-28","geofence_data":[116.43420270122199,39.9343584262252,116.44563188353847,39.93423377921478,116.44929430942302,39.925585866084994,116.43514950481111,39.9258913664044],"mdt":"GEOFENCE","mi":"ONE_SHOT","operators":null}]
         */

        @SerializedName("page_no")
        private int pageNo;
        @SerializedName("total_count")
        private int totalCount;
        @SerializedName("page_count")
        private int pageCount;
        @SerializedName("page_size")
        private int pageSize;
        @SerializedName("mt_list")
        private List<FenceBean> fenceList;

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public List<FenceBean> getFenceList() {
            return fenceList;
        }

        public void setFenceList(List<FenceBean> fenceList) {
            this.fenceList = fenceList;
        }

        public static class FenceBean implements Parcelable {
            /**
             * name : 0
             * imeis : []
             * start_date : 2020-02-19
             * end_date : 2020-02-27
             * geofence_data : [116.46056662170871,39.91815564878112,116.4760844718016,39.91691111555171,116.4753453839385,39.91086790029015,116.4596793734743,39.91001278576602]
             * mdt : GEOFENCE
             * mi : ONE_SHOT
             * operators : null
             */

            private String name;
            @SerializedName("start_date")
            private String startDate;
            @SerializedName("end_date")
            private String endDate;
            private String mdt;
            private String mi;
            private Object operators;
            private List<String> imeis;
            @SerializedName("geofence_data")
            private List<Double> geofenceData;
            //add by wlj for if fence list all its devices
            private boolean isOpened = false;

            protected FenceBean(Parcel in) {
                name = in.readString();
                startDate = in.readString();
                endDate = in.readString();
                mdt = in.readString();
                mi = in.readString();
                imeis = in.createStringArrayList();
                isOpened = in.readByte() != 0;
            }

            public static final Creator<FenceBean> CREATOR = new Creator<FenceBean>() {
                @Override
                public FenceBean createFromParcel(Parcel in) {
                    return new FenceBean(in);
                }

                @Override
                public FenceBean[] newArray(int size) {
                    return new FenceBean[size];
                }
            };

            public boolean isOpened() {
                return isOpened;
            }

            public void setIsOpened(boolean opened) {
                isOpened = opened;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMdt() {
                return mdt;
            }

            public void setMdt(String mdt) {
                this.mdt = mdt;
            }

            public String getMi() {
                return mi;
            }

            public void setMi(String mi) {
                this.mi = mi;
            }

            public Object getOperators() {
                return operators;
            }

            public void setOperators(Object operators) {
                this.operators = operators;
            }

            public List<String> getImeis() {
                return imeis;
            }

            public void setImeis(List<String> imeis) {
                this.imeis = imeis;
            }

            public String getStartDate() {
                return startDate;
            }

            public void setStartDate(String startDate) {
                this.startDate = startDate;
            }

            public String getEndDate() {
                return endDate;
            }

            public void setEndDate(String endDate) {
                this.endDate = endDate;
            }

            public List<Double> getGeofenceData() {
                return geofenceData;
            }

            public void setGeofenceData(List<Double> geofenceData) {
                this.geofenceData = geofenceData;
                XLog.dReport("wlj debug setGeofenceData ... " + geofenceData);
            }

            @NonNull
            @Override
            public String toString() {
//                return super.toString();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("{").
                        append("name=").append(name).append(",").
                        append("startDate=").append(startDate).append(",").
                        append("endDate=").append(endDate).append(",").
                        append("type=").append(mdt).append(",").
                        append("imeis=").append(imeis.toString()).append(",").
                        append("geoData=").append(geofenceData).append(",").
                        append("}");


                return stringBuffer.toString();
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(name);
                dest.writeString(startDate);
                dest.writeString(endDate);
                dest.writeString(mdt);
                dest.writeString(mi);
                dest.writeStringList(imeis);
                dest.writeByte((byte) (isOpened ? 1 : 0));
            }
        }
    }
}
