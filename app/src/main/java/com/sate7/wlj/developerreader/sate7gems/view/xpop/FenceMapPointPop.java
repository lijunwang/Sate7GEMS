package com.sate7.wlj.developerreader.sate7gems.view.xpop;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.ObjectUtils;
import com.lxj.xpopup.core.BottomPopupView;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.map.BaiduMapHelper;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.ArrayList;

public class FenceMapPointPop extends BottomPopupView implements BaiduMap.OnMapClickListener {
    private int popupHeight;
    private MapView mapView;
    private ArrayList<LatLng> pointList = new ArrayList<>();
    private Overlay linesOverlay;

    public FenceMapPointPop(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.fence_create_map;
    }


    public void setPopupHeight(int popupHeight) {
        this.popupHeight = popupHeight;
    }

    @Override
    protected int getPopupHeight() {
        return popupHeight;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mapView = findViewById(R.id.fenceMapView);
        XLog.dReport("mapView onCreate ...");
        mapView.getMap().setOnMapClickListener(this);
    }

    public void onPause() {
        XLog.dReport("mapView onPause ..." + ObjectUtils.isEmpty(mapView));
        if (ObjectUtils.isEmpty(mapView)) {
            return;
        }
        mapView.onPause();
    }

    public void onDismiss() {
        XLog.dReport("mapView onDismiss ...");
    }

    public void clean() {
        XLog.dReport("mapView onDismiss ...");
        if(mapView == null){
            return;
        }
        mapView.getMap().clear();
    }

    public void onResume() {
        XLog.dReport("mapView onResume ..." + ObjectUtils.isEmpty(mapView));
        if (ObjectUtils.isEmpty(mapView)) {
            return;
        }
        mapView.onResume();
    }

    public void onDestroy() {
        XLog.dReport("mapView onDestroy ..." + ObjectUtils.isEmpty(mapView));
        if (ObjectUtils.isEmpty(mapView)) {
            return;
        }
        mapView.onDestroy();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        XLog.dReport("onMapClick ... " + latLng);
        addPoint(latLng);
        /*if (linesOverlay != null) {
            linesOverlay.remove();
        }
        pointList.add(latLng);
        if (pointList.size() >= 2) {
            linesOverlay = BaiduMapHelper.getInstance().addLines(mapView, pointList);
        }*/
    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {
        XLog.dReport("onMapPoiClick ... " + mapPoi.getName() + "," + mapPoi.getPosition());
//        BaiduMapHelper.getInstance().addPoint(mapView, mapPoi.getPosition());
        addPoint(mapPoi.getPosition());
    }

    private void addPoint(LatLng latLng){
        onMapClickListener.onMapClicked(mapView,latLng);
//        BaiduMapHelper.getInstance().addPoint(mapView, latLng);
    }
    private OnMapClickListener onMapClickListener;
    public void setOnMapClickedListener(OnMapClickListener listener){
        onMapClickListener = listener;
    }
    public interface OnMapClickListener{
        void onMapClicked(MapView mapView,LatLng latLng);
    }
}
