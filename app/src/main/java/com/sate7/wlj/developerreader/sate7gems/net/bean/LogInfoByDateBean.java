package com.sate7.wlj.developerreader.sate7gems.net.bean;


import java.util.List;

public class LogInfoByDateBean {

    /**
     * code : 0
     * msg : SUCCESS
     * data : {"status":"异常","messages":["867935030002256 于 2020-03-18 15:28:39 状态更新","867935030002256 于 2020-03-18 15:28:39 状态更新","867935030002256 于 2020-03-18 15:27:22 状态更新","867935030002256 于 2020-03-18 15:27:22 状态更新"],"location":[[113.86446771550065,22.589101872452407],[113.86446771550065,22.589101872452407],[113.86446771550065,22.589101872452407],[113.86446771550065,22.589101872452407]],"basic":{"imei":"867935030002256","build_number":"","mode":"功能机-单卡","type":"P1","produce_date":null,"tag":"测试P1","tag_qx":"神秘","register_time":"2020-03-18 15:27","last_update_time":"2020-03-18 15:28","bind_number":""},"owner":null,"pic1":null,"device_state":null,"report":[{"tag":"v10008","label":"海拔","is_normal":true,"data":[{"date":"2020-03-18 15:28","value":"0.0"},{"date":"2020-03-18 15:28","value":"0.0"},{"date":"2020-03-18 15:27","value":"0.0"},{"date":"2020-03-18 15:27","value":"0.0"}]},{"tag":"v10040","label":"电量","is_normal":true,"data":[{"date":"2020-03-18 15:28","value":"0"},{"date":"2020-03-18 15:28","value":"0"},{"date":"2020-03-18 15:27","value":"0"},{"date":"2020-03-18 15:27","value":"0"}]},{"tag":"v10200","label":"速度","is_normal":true,"data":[{"date":"2020-03-18 15:28","value":"0"},{"date":"2020-03-18 15:28","value":"0"},{"date":"2020-03-18 15:27","value":"0"},{"date":"2020-03-18 15:27","value":"0"}]}],"gadget":null}
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
         * status : 异常
         * messages : ["867935030002256 于 2020-03-18 15:28:39 状态更新","867935030002256 于 2020-03-18 15:28:39 状态更新","867935030002256 于 2020-03-18 15:27:22 状态更新","867935030002256 于 2020-03-18 15:27:22 状态更新"]
         * location : [[113.86446771550065,22.589101872452407],[113.86446771550065,22.589101872452407],[113.86446771550065,22.589101872452407],[113.86446771550065,22.589101872452407]]
         * basic : {"imei":"867935030002256","build_number":"","mode":"功能机-单卡","type":"P1","produce_date":null,"tag":"测试P1","tag_qx":"神秘","register_time":"2020-03-18 15:27","last_update_time":"2020-03-18 15:28","bind_number":""}
         * owner : null
         * pic1 : null
         * device_state : null
         * report : [{"tag":"v10008","label":"海拔","is_normal":true,"data":[{"date":"2020-03-18 15:28","value":"0.0"},{"date":"2020-03-18 15:28","value":"0.0"},{"date":"2020-03-18 15:27","value":"0.0"},{"date":"2020-03-18 15:27","value":"0.0"}]},{"tag":"v10040","label":"电量","is_normal":true,"data":[{"date":"2020-03-18 15:28","value":"0"},{"date":"2020-03-18 15:28","value":"0"},{"date":"2020-03-18 15:27","value":"0"},{"date":"2020-03-18 15:27","value":"0"}]},{"tag":"v10200","label":"速度","is_normal":true,"data":[{"date":"2020-03-18 15:28","value":"0"},{"date":"2020-03-18 15:28","value":"0"},{"date":"2020-03-18 15:27","value":"0"},{"date":"2020-03-18 15:27","value":"0"}]}]
         * gadget : null
         */

        private String status;
        private EquipmentListBean.DataBean.Device basic;
        private Object owner;
        private Object pic1;
        private Object device_state;
        private Object gadget;
        private List<String> messages;
        private List<List<Double>> location;
        private List<ReportBean> report;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Object getOwner() {
            return owner;
        }

        public void setOwner(Object owner) {
            this.owner = owner;
        }

        public Object getPic1() {
            return pic1;
        }

        public void setPic1(Object pic1) {
            this.pic1 = pic1;
        }

        public Object getDevice_state() {
            return device_state;
        }

        public EquipmentListBean.DataBean.Device getBasic() {
            return basic;
        }

        public void setBasic(EquipmentListBean.DataBean.Device basic) {
            this.basic = basic;
        }

        public void setDevice_state(Object device_state) {
            this.device_state = device_state;
        }

        public Object getGadget() {
            return gadget;
        }

        public void setGadget(Object gadget) {
            this.gadget = gadget;
        }

        public List<String> getMessages() {
            return messages;
        }

        public void setMessages(List<String> messages) {
            this.messages = messages;
        }

        public List<List<Double>> getLocation() {
            return location;
        }

        public void setLocation(List<List<Double>> location) {
            this.location = location;
        }

        public List<ReportBean> getReport() {
            return report;
        }

        public void setReport(List<ReportBean> report) {
            this.report = report;
        }


        public static class ReportBean {
            /**
             * tag : v10008
             * label : 海拔
             * is_normal : true
             * data : [{"date":"2020-03-18 15:28","value":"0.0"},{"date":"2020-03-18 15:28","value":"0.0"},{"date":"2020-03-18 15:27","value":"0.0"},{"date":"2020-03-18 15:27","value":"0.0"}]
             */

            private String tag;
            private String label;
            private boolean is_normal;
            private List<ReportInfoBean> data;

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public boolean isIs_normal() {
                return is_normal;
            }

            public void setIs_normal(boolean is_normal) {
                this.is_normal = is_normal;
            }

            public List<ReportInfoBean> getData() {
                return data;
            }

            public void setData(List<ReportInfoBean> data) {
                this.data = data;
            }

            public static class ReportInfoBean {
                /**
                 * date : 2020-03-18 15:28
                 * value : 0.0
                 */

                private String date;
                private String value;

                public String getDate() {
                    return date;
                }

                public void setDate(String date) {
                    this.date = date;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }
    }
}
