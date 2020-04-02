package com.sate7.wlj.developerreader.sate7gems.net.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginBean {

    /**
     * code : 0
     * msg : SUCCESS
     * data : {"token":"eyJwaG9uZSIgOiAicXhfYWRtaW4gIiwiY3JlYXRlX3RpbWUiIDogIjIwMjAtMDMtMTYgMTM6MTA6MTUgIiwiZXhwaXJlZF9pbiIgOiAzNjg3MH0=","orgs":[{"visible":false,"org_name":"柒星科技","org_code":"qx","org_type":"COMP","org_level":1,"org_root_code":"qx"}]}
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
         * token : eyJwaG9uZSIgOiAicXhfYWRtaW4gIiwiY3JlYXRlX3RpbWUiIDogIjIwMjAtMDMtMTYgMTM6MTA6MTUgIiwiZXhwaXJlZF9pbiIgOiAzNjg3MH0=
         * orgs : [{"visible":false,"org_name":"柒星科技","org_code":"qx","org_type":"COMP","org_level":1,"org_root_code":"qx"}]
         */

        private String token;
        private List<OrgsBean> orgs;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public List<OrgsBean> getOrgs() {
            return orgs;
        }

        public void setOrgs(List<OrgsBean> orgs) {
            this.orgs = orgs;
        }

        public static class OrgsBean {
            /**
             * visible : false
             * org_name : 柒星科技
             * org_code : qx
             * org_type : COMP
             * org_level : 1
             * org_root_code : qx
             */
            private boolean visible;
            @SerializedName("org_name")
            private String orgName;
            @SerializedName("org_code")
            private String orgCode;
            @SerializedName("org_type")
            private String orgType;
            @SerializedName("org_level")
            private int orgLevel;
            @SerializedName("org_root_code")
            private String orgRootCode;

            public boolean isVisible() {
                return visible;
            }

            public void setVisible(boolean visible) {
                this.visible = visible;
            }

            public String getOrgName() {
                return orgName;
            }

            public void setOrgName(String orgName) {
                this.orgName = orgName;
            }

            public String getOrgCode() {
                return orgCode;
            }

            public void setOrgCode(String orgCode) {
                this.orgCode = orgCode;
            }

            public String getOrgType() {
                return orgType;
            }

            public void setOrgType(String orgType) {
                this.orgType = orgType;
            }

            public int getOrgLevel() {
                return orgLevel;
            }

            public void setOrgLevel(int orgLevel) {
                this.orgLevel = orgLevel;
            }

            public String getOrgRootCode() {
                return orgRootCode;
            }

            public void setOrgRootCode(String orgRootCode) {
                this.orgRootCode = orgRootCode;
            }
        }
    }
}
