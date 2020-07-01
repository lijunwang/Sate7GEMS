package com.sate7.wlj.developerreader.sate7gems.map;

import android.os.Bundle;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.blankj.utilcode.util.ToastUtils;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.UpdateItemBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.ArrayList;

public class BaiduMapHelper_BackUp {
    private static class BaiduMapHelperHolder {
        private static BaiduMapHelper_BackUp mInstance = new BaiduMapHelper_BackUp();
    }

    private ArrayList<LatLng> pointList = new ArrayList<>();

    public static BaiduMapHelper_BackUp getInstance() {
        return BaiduMapHelperHolder.mInstance;
    }

    private BaiduMapHelper_BackUp() {
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

    public PolylineData drawLines(MapView mapView, ArrayList<UpdateItemBean> points) {
        //构建折线点坐标
        XLog.dReport("drawLines ..." + points.size() + "," + points);
        ArrayList<Overlay> pointsOverlay = new ArrayList<>();
        if (points.size() < 2) {
            ToastUtils.showLong(R.string.little_point);
            return new PolylineData(pointsOverlay);
        }
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.arrow);
        BitmapDescriptor bitmapStartEnd = BitmapDescriptorFactory
                .fromResource(R.drawable.point_red);
        //经纬度相同的点，更新位置
        UpdateItemBean start = points.get(0);
        int index = 0;
        int end = points.size() - 1;
        for (UpdateItemBean point : points) {
            Bundle bundle = new Bundle();
            bundle.putString("time",point.getUpdateTime());
            OverlayOptions option = new MarkerOptions()
                    .position(point.getLocation())
                    .extraInfo(bundle)
                    .clickable(true)
//                    .rotate((index == 0 || index == end) ? 0 : (-90 - angel(mapView,start.getLocation(),point.getLocation())))
                    .rotate((index == 0 || index == end) ? 0 : - angel(mapView,start,point))
                    .icon((index == 0 || index == end) ? bitmapStartEnd : bitmap);
            index ++;
            pointsOverlay.add(mapView.getMap().addOverlay(option));
            start = point;
            //添加文字
            OverlayOptions textOptions = new TextOptions()
                    .text(point.getUpdateTime()) //文字内容
                    .bgColor(0x000000) //背景色
                    .visible(true)
                    .fontSize(24) //字号
                    .fontColor(0xFF000000) //文字颜色
                    .position(point.getLocation());
//            if visible(false)
            pointsOverlay.add(mapView.getMap().addOverlay(textOptions));
        }
//设置折线的属性
        ArrayList<LatLng> latLngPoint = new ArrayList<>();
        XLog.dReport("angel debug real ..." + latLngPoint);
        for(UpdateItemBean bean : points){
            latLngPoint.add(bean.getLocation());
        }
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(5)
                .color(0xAA1A65C1)
                .points(latLngPoint);
//在地图上绘制折线
//mPloyline 折线对象
        Overlay polyline = mapView.getMap().addOverlay(polylineOptions);
        return new PolylineData(pointsOverlay, polyline);
    }

    //三角形测试
    private float angel(MapView mapView,UpdateItemBean from, UpdateItemBean to){
        //直角边1（虚构），经度距离，横向
        LatLng latLng = new LatLng(from.getLocation().latitude,to.getLocation().longitude);
        double lng = DistanceUtil.getDistance(latLng,from.getLocation());
        //直角边2（虚构），纬度距离，纵向
        double lat = DistanceUtil.getDistance(latLng,to.getLocation());
        //计算角度tan
        float angel = (float) (Math.atan2(lng,lat) * 180 / Math.PI);
        //from on right top
        if(from.getLocation().latitude > to.getLocation().latitude && from.getLocation().longitude > to.getLocation().longitude){
            angel += 180;
        //from on left bottom
        }else if(from.getLocation().latitude < to.getLocation().latitude && from.getLocation().longitude > to.getLocation().longitude){
            angel = 180 - angel;
        //from on right bottom
        }else if(from.getLocation().latitude < to.getLocation().latitude && from.getLocation().longitude < to.getLocation().longitude){
            //no need to do anything;
        //from on left bottom
        }else if(from.getLocation().latitude > to.getLocation().latitude && from.getLocation().longitude < to.getLocation().longitude){
            angel = -angel;
        }





        //if debug
        ArrayList<LatLng> debug = new ArrayList<>();
        debug.add(from.getLocation());
        debug.add(latLng);
        debug.add(to.getLocation());
        debug.add(from.getLocation());
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(5)
                .color(0xAAFF0000)
                .points(debug);
        mapView.getMap().addOverlay(polylineOptions);

        XLog.dReport("angel ww == " + from + " || " + to + "," + angel);
        return angel;
    }

    //三角形测试
    private float angel(MapView mapView,LatLng from, LatLng to){
        //斜边长度
//        double bevel = DistanceUtil.getDistance(from, to);
        //直角边1（虚构），经度距离，横向
//        LatLng latLng = new LatLng(to.latitude,from.longitude);
        LatLng latLng = new LatLng(from.latitude,to.longitude);
        double leg1 = DistanceUtil.getDistance(latLng,from);
        //直角边2（虚构），纬度距离，纵向
        double leg2 = DistanceUtil.getDistance(latLng,to);
        //计算角度tan
//        float angel = (float) Math.atan2(leg2,leg1);
        float angel = (float) (Math.atan2(leg2,leg1) * 180 / Math.PI);
//        float angel = (float) (Math.atan2(leg1,leg2) * 180 / Math.PI);
        XLog.dReport("angel == " + angel);
        ArrayList<LatLng> debug = new ArrayList<>();
        debug.add(from);
        debug.add(latLng);
        debug.add(to);
        debug.add(from);
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(5)
                .color(0xAAFF0000)
                .points(debug);
        mapView.getMap().addOverlay(polylineOptions);
        return angel;
    }
}
