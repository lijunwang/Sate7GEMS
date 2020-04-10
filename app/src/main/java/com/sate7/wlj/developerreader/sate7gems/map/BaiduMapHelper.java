package com.sate7.wlj.developerreader.sate7gems.map;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.ToastUtils;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.ArrayList;
import java.util.List;

public class BaiduMapHelper {
    private static class BaiduMapHelperHolder {
        private static BaiduMapHelper mInstance = new BaiduMapHelper();
    }

    private ArrayList<LatLng> pointList = new ArrayList<>();

    public static BaiduMapHelper getInstance() {
        return BaiduMapHelperHolder.mInstance;
    }

    private BaiduMapHelper() {
    }

    public void cleanAll(MapView mapView) {
        mapView.getMap().clear();
    }

    public void addMarkerPoint(MapView mapView, LatLng point, EquipmentListBean.DataBean.Device device) {
        if (mapView == null) {
            return;
        }
        /*View view = LayoutInflater.from(mapView.getContext()).inflate(R.layout.marker_info, null, false);
        InfoWindow infoWindow = new InfoWindow(view,point,-100);*/
        XLog.dReport("nani addMarkerPoint ww ... " + point + "," + device);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.location);
        Bundle data = new Bundle();
        data.putParcelable("device", device);
        OverlayOptions option = new MarkerOptions().extraInfo(data)
                .position(point)
                .icon(bitmap)/*.infoWindow(infoWindow)*/;
        mapView.getMap().addOverlay(option);
        mapView.setTag(device);
    }


    public Overlay addPoint(MapView mapView, LatLng point) {
        /*View view = LayoutInflater.from(mapView.getContext()).inflate(R.layout.marker_info, null, false);
        InfoWindow infoWindow = new InfoWindow(view,point,-100);*/
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.point);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap)/*.infoWindow(infoWindow)*/;
        return mapView.getMap().addOverlay(option);
    }

    public void drawLines(MapView mapView, ArrayList<LatLng> points) {
        //构建折线点坐标
        XLog.dReport("drawLines ..." + points.size() + "," + points);
        if(points.size() < 2){
            ToastUtils.showLong(R.string.little_point);
            return ;
        }
        for(LatLng point : points){
            addPoint(mapView,point);
        }
//设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(10)
                .color(0xAAFF0000)
                .points(points);
//在地图上绘制折线
//mPloyline 折线对象
        Overlay mPolyline = mapView.getMap().addOverlay(mOverlayOptions);
        boolean visible = mPolyline.isVisible();
        XLog.dReport("drawLines ... " + visible + "," + mPolyline);
    }

}
