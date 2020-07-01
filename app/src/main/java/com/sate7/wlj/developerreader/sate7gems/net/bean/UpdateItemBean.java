package com.sate7.wlj.developerreader.sate7gems.net.bean;

import android.os.Parcel;

import com.baidu.mapapi.model.LatLng;

import java.util.Objects;

public class UpdateItemBean {
    private LatLng location;
    private String updateTime;

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public UpdateItemBean(LatLng location, String updateTime) {
        this.location = location;
        this.updateTime = updateTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateItemBean bean = (UpdateItemBean) o;
        return location.latitude == bean.getLocation().latitude &&
                location.longitude == bean.getLocation().longitude &&
                Objects.equals(updateTime, bean.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location.latitude,location.longitude, updateTime);
    }

    @Override
    public String toString() {
        return "UpdateItemBean{" +
                "location=" + location +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
