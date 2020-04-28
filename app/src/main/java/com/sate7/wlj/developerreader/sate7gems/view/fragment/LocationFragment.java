package com.sate7.wlj.developerreader.sate7gems.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.zhouwei.library.CustomPopWindow;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.HorizontalAttachPopupView;
import com.sate7.wlj.developerreader.sate7gems.MainActivity;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.FragmentLocationBinding;
import com.sate7.wlj.developerreader.sate7gems.databinding.MarkerInfoBinding;
import com.sate7.wlj.developerreader.sate7gems.location.MarkerAction;
import com.sate7.wlj.developerreader.sate7gems.map.PolylineData;
import com.sate7.wlj.developerreader.sate7gems.net.bean.LogInfoByDateBean;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.RetrofitServerImp;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;
import com.sate7.wlj.developerreader.sate7gems.util.Constants;
import com.sate7.wlj.developerreader.sate7gems.view.xpop.MorePop;
import com.sate7.wlj.developerreader.sate7gems.view.xpop.StartEndDateDialog;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.DetailViewMode;
import com.sate7.wlj.developerreader.sate7gems.map.BaiduMapHelper;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LocationFragment extends BaseFragment implements View.OnClickListener, OnMarkerClickListener {
    private FragmentLocationBinding binding;
    private DetailViewMode detailViewMode;
    private MarkerAction markerAction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailViewMode = ViewModelProviders.of(this).get(DetailViewMode.class);
        markerAction = new MarkerAction(getContext());
//        checkAndShowSelectedDevice();

        startQueryAndShow();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return binding.mapView.onTouchEvent(event);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false);
        binding.mapView.getMap().setOnMarkerClickListener(this);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        XLog.dReport("onViewCreated ...");
        binding.more.setOnClickListener(this);
        detailViewMode.getLatestLocation().observe(this, new Observer<EquipmentListBean.DataBean.Device>() {
            @Override
            public void onChanged(EquipmentListBean.DataBean.Device device) {
                XLog.dReport("Location show ... " + device + "," + device);
                if (device.getLocation() != null) {
                    mLatestLocation = device.getLocation();
                    BaiduMapHelper.getInstance().addMarkerPoint(binding.mapView, device.getLocation(), device);
                    handler.removeMessages(MSG_CENTER_TO);
                    handler.sendMessageDelayed(handler.obtainMessage(MSG_CENTER_TO, device), 500);
                } else {
                    ToastUtils.showLong(getResources().getString(R.string.location_empty, TextUtils.isEmpty(device.getTag()) ? device.getImei() : device.getTag()));
                }
            }
        });
    }

    public MapView getMapView(MotionEvent event) {
        return binding.mapView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
        XLog.dReport("Location Fragment onDestroy ... ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top:
                handler.removeMessages(MSG_TOGGLE);
                handler.sendEmptyMessageDelayed(MSG_TOGGLE, 300);
                break;
            case R.id.marker_upload:
                markerAction.start(currentDevice, MarkerAction.ACTION.UPLOAD);
                break;
            case R.id.marker_dial:
                markerAction.start(currentDevice, MarkerAction.ACTION.DIAL);
                break;
            case R.id.marker_sms:
                markerAction.start(currentDevice, MarkerAction.ACTION.SMS);
                break;
            case R.id.marker_track:
                startQueryAndShow();
                break;
            case R.id.info_close:
                XLog.dReport("info_close ... " + infoWindow);
                if (infoWindow != null) {
                    infoWindow.dissmiss();
                }
                break;
            case R.id.morePopCloseTrack:
                if (morePop != null && morePop.isShow()) {
                    morePop.dismiss();
                }
                if (!mPlyLineList.isEmpty()) {
                    for (PolylineData polylineData : mPlyLineList) {
                        polylineData.remove();
                    }
                    mPlyLineList.clear();
                }
                break;
            case R.id.morePopOpenDrawer:
                if (morePop != null && morePop.isShow()) {
                    morePop.dismiss();
                }
                openDrawLeft();
                break;
            case R.id.more:
                if (!mPlyLineList.isEmpty()) {
                    if (morePop == null) {
                        morePop = new XPopup.Builder(getContext()).atView(binding.more).asCustom(new MorePop(getContext(), this));
                    }
                    morePop.show();
                } else {
                    openDrawLeft();
                }
                break;
        }
    }

    private void openDrawLeft() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.openDrawer();
    }


    private EquipmentListBean.DataBean.Device currentDevice = null;
    private LatLng mLatestLocation = null;

    @Override
    public boolean onMarkerClick(Marker marker) {
        XLog.dReport("onMarkerClick ... " + "," + marker.getAnchorX() + "," + marker.getAnchorY());
        if (marker.getExtraInfo() != null) {
            EquipmentListBean.DataBean.Device device = marker.getExtraInfo().getParcelable("device");
            currentDevice = device;
            XLog.dReport("onMarkerClick device ... " + device + "," + device.getLocation());
            goTo(device);
            return true;
        }
        return false;
    }

    private final int MSG_SHOW_INFO = 0x10;
    private final int MSG_TOGGLE = 0x11;
    private final int MSG_CENTER_TO = 0x12;
    private final int MSG_FLUSH = 0x13;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            XLog.dReport("handleMessage ... " + msg.what);
            switch (msg.what) {
                case MSG_SHOW_INFO:
                    mapMove((int) (ScreenUtils.getScreenWidth() * 0.5 * 0.75), 0);
                    EquipmentListBean.DataBean.Device device = (EquipmentListBean.DataBean.Device) msg.obj;
                    showDetail(device);
                    break;
                case MSG_TOGGLE:
                    break;
                case MSG_CENTER_TO:
                    EquipmentListBean.DataBean.Device device1 = (EquipmentListBean.DataBean.Device) msg.obj;
                    centerTo(device1);
                    break;
                case MSG_FLUSH:
                    break;
            }
        }
    };

    private CustomPopWindow infoWindow;
    private boolean infoWindowShowing = false;
    private EquipmentListBean.DataBean.Device clickDevice;

    private void showDetail(EquipmentListBean.DataBean.Device device) {
        if (infoWindow == null || !clickDevice.getImei().equals(device.getImei())) {
            clickDevice = device;
            MarkerInfoBinding markerInfoBinding = MarkerInfoBinding.inflate(getLayoutInflater(), binding.mapView, false);
            markerInfoBinding.setDevice(device);
            View customView = markerInfoBinding.getRoot();
            markerInfoBinding.infoClose.setOnClickListener(this);
            markerInfoBinding.bottoms.markerUpload.setOnClickListener(this);
            markerInfoBinding.bottoms.markerDial.setOnClickListener(this);
            markerInfoBinding.bottoms.markerSms.setOnClickListener(this);
            markerInfoBinding.bottoms.markerTrack.setOnClickListener(this);
            infoWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                    .size(600, 1000)
                    .setView(customView)//显示的布局
                    .setOutsideTouchable(false)
                    .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            move2Center();
                            infoWindowShowing = false;
                        }
                    })
                    .setTouchable(true)
                    .enableOutsideTouchableDissmiss(false)
                    .create();//创建PopupWindow
        }
        int marginLeft = (int) (getResources().getDrawable(R.drawable.location).getIntrinsicWidth() + ScreenUtils.getScreenWidth() * 0.5 * 0.25);
        infoWindow.showAtLocation(binding.mapView, Gravity.CENTER_VERTICAL | Gravity.LEFT, marginLeft, 0);
        infoWindowShowing = true;
    }

    private void goTo(EquipmentListBean.DataBean.Device device) {
        float max = binding.mapView.getMap().getMaxZoomLevel();
        XLog.dReport("goto Max == " + max);
        binding.mapView.getMap().animateMapStatus(MapStatusUpdateFactory.zoomTo(max - 4));
        binding.mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(device.getLocation()));
        handler.removeMessages(MSG_SHOW_INFO);
        handler.sendMessageDelayed(handler.obtainMessage(MSG_SHOW_INFO, device), 500);
    }

    private void move2Center() {
        XLog.dReport("move2Center ... " + (currentDevice != null));
        if (currentDevice != null) {
            binding.mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(currentDevice.getLocation()));
        }
    }

    private void mapMove(int distanceX, int distanceY) {
        binding.mapView.getMap().animateMapStatus(MapStatusUpdateFactory.scrollBy(distanceX, distanceY));
    }

    private void centerTo(EquipmentListBean.DataBean.Device device) {
        XLog.dReport("centerTo ... " + device + "," + infoWindowShowing);
        if (device != null && !infoWindowShowing) {
            binding.mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(device.getLocation()));
        }
    }


    private StartEndDateDialog startEndDateDialog;
    private ArrayList<PolylineData> mPlyLineList = new ArrayList<>();
    //    private PolylineData mPolylineData;
    private BasePopupView morePop;

    private void startQueryAndShow() {
        if (startEndDateDialog == null) {
            startEndDateDialog = new StartEndDateDialog(getContext(), new StartEndDateDialog.StartEndTimeSelectedListener() {
                @Override
                public void onDateSelected(String start, String end) {
                    XLog.dReport("setStartEnd ... " + start + "," + end);
                    RetrofitServerImp.getInstance().queryLocationByDate(1, currentDevice.getImei(), start, end, new Server.LocationsListQueryCallBack() {
                        @Override
                        public void onLocationsListQuerySuccess(LogInfoByDateBean logInfoByDateBean) {
                            List<List<Double>> locations = logInfoByDateBean.getData().getLocation();
                            List<String> message = logInfoByDateBean.getData().getMessages();
                            XLog.dReport("onLocationsListQuerySuccess locations ... " + message);
                            if (locations == null && locations.isEmpty()) {
                                ToastUtils.showLong(R.string.track_empty);
                                binding.mapView.getMap().clear();
                                BaiduMapHelper.getInstance().addMarkerPoint(binding.mapView, mLatestLocation, currentDevice);
                                return;
                            }
                            XLog.dReport("onLocationsListQuerySuccess ... " + locations.size());
                            ArrayList<LatLng> points = new ArrayList<>();
                            for (List<Double> l : locations) {
                                points.add(new LatLng(l.get(1), l.get(0)));
                            }
                            PolylineData polylineData = BaiduMapHelper.getInstance().drawLines(binding.mapView, points);
                            mPlyLineList.add(polylineData);
                            if (infoWindow != null) {
                                infoWindow.dissmiss();
                            }
                        }

                        @Override
                        public void onLocationsListQueryFailed(String msg) {

                        }
                    });
                }
            });
        }
        startEndDateDialog.show();
    }

    private void checkAndShowSelectedDevice() {
        HashSet<String> imeiSet = (HashSet<String>) SPUtils.getInstance().getStringSet(Constants.SELECT_LOCATION_IMEIS);
        XLog.dReport("checkAndShowSelectedDevice ... " + imeiSet);
        for (String imei : imeiSet) {
            detailViewMode.queryLatestLocationInfo(imei);
        }
    }

    //上次有哪些设备
    private ArrayList<EquipmentListBean.DataBean.Device> lastSelectedDevices = new ArrayList<>();

    public void onDeviceSelected(ArrayList<EquipmentListBean.DataBean.Device> selectedDevices) {
        HashSet<String> added = added(selectedDevices, lastSelectedDevices);
        HashSet<String> reduced = reduced(selectedDevices, lastSelectedDevices);
        XLog.dReport("Location onDrawerClosed diff added " + added);
        XLog.dReport("Location onDrawerClosed diff reduce " + reduced);
        lastSelectedDevices = selectedDevices;
        if (added.isEmpty() && reduced.isEmpty()) {
            return;
        }
        binding.mapView.getMap().clear();
        for (EquipmentListBean.DataBean.Device device : selectedDevices) {
            detailViewMode.queryLatestLocationInfo(device.getImei());
        }
    }

}
