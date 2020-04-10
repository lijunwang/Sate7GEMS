package com.sate7.wlj.developerreader.sate7gems.view.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.zhouwei.library.CustomPopWindow;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.FragmentLocationBinding;
import com.sate7.wlj.developerreader.sate7gems.databinding.MarkerInfoBinding;
import com.sate7.wlj.developerreader.sate7gems.location.MarkerAction;
import com.sate7.wlj.developerreader.sate7gems.net.Sate7GEMSServer;
import com.sate7.wlj.developerreader.sate7gems.net.bean.DeviceDetailInfoBean;
import com.sate7.wlj.developerreader.sate7gems.view.MyItemDecoration;
import com.sate7.wlj.developerreader.sate7gems.view.StateRecyclerView;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.EquipmentAdapter;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.DetailViewMode;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.EquipmentListViewModel;
import com.sate7.wlj.developerreader.sate7gems.map.BaiduMapHelper;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.ArrayList;
import java.util.List;

public class LocationFragment extends BaseFragment implements View.OnClickListener, BaiduMap.OnMarkerClickListener, StateRecyclerView.StateListener {
    private FragmentLocationBinding binding;
    private EquipmentListViewModel equipmentListViewModel;
    private EquipmentAdapter adapter;
    private DetailViewMode detailViewMode;
    private MarkerAction markerAction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        equipmentListViewModel = ViewModelProviders.of(this).get(EquipmentListViewModel.class);
        detailViewMode = ViewModelProviders.of(this).get(DetailViewMode.class);
        adapter = new EquipmentAdapter(getContext(), EquipmentAdapter.TYPE.LOCATION);
        markerAction = new MarkerAction(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false);
        return binding.getRoot();
    }

    @Override
    public void startLoadData() {
        super.startLoadData();
        equipmentListViewModel.listAllEquipment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.top.setOnClickListener(this);
        binding.mapView.getMap().setOnMarkerClickListener(this);
        binding.recyclerView.addItemDecoration(new MyItemDecoration());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setStateListener(this);
        equipmentListViewModel.getAllDevice().observe(this, new Observer<ArrayList<EquipmentListBean.DataBean.Device>>() {
            @Override
            public void onChanged(ArrayList<EquipmentListBean.DataBean.Device> devices) {
                XLog.dReport("EquipmentList onChanged ... " + devices.size());
                adapter.update(devices);
            }
        });
        XLog.dReport("onViewCreated ...");
        detailViewMode.getLocationData().observe(this, new Observer<DeviceDetailInfoBean>() {
            @Override
            public void onChanged(DeviceDetailInfoBean deviceDetailInfoBean) {
                EquipmentListBean.DataBean.Device device = deviceDetailInfoBean.getData().getBasic();
                List<List<Double>> locationList = deviceDetailInfoBean.getData().getLocation();
                XLog.dReport("Location show ... " + locationList.size() + "," + device);
                if (locationList.size() >= 1) {
                    List<Double> location = locationList.get(0);
                    LatLng point = new LatLng(location.get(1), location.get(0));
                    device.setLocation(point);
                    BaiduMapHelper.getInstance().addMarkerPoint(binding.mapView, point, device);
                    handler.removeMessages(MSG_CENTER_TO);
                    handler.sendMessageDelayed(handler.obtainMessage(MSG_CENTER_TO, device), 500);
                } else {
                    ToastUtils.showLong(getResources().getString(R.string.location_empty, TextUtils.isEmpty(device.getTag()) ? device.getImei() : device.getTag()));
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
        handler.sendEmptyMessageDelayed(MSG_FLUSH, 1000);
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
                Sate7GEMSServer.getInstance().getLocationInfoByDate(currentDevice.getImei(), new Sate7GEMSServer.LocationsCallback() {
                    @Override
                    public void onLocationsGet(ArrayList<LatLng> points) {
                        BaiduMapHelper.getInstance().drawLines(binding.mapView, points);
                    }
                });
                break;
            case R.id.info_close:
                XLog.dReport("info_close ... " + infoWindow);
                if (infoWindow != null) {
                    infoWindow.dissmiss();
                }
                break;
        }
    }

    private void onToggled() {
        adapter.onToggled(EquipmentAdapter.TYPE.LOCATION);
        ArrayList<EquipmentListBean.DataBean.Device> devices = adapter.getSelectedDevices(EquipmentAdapter.TYPE.LOCATION);
        BaiduMapHelper.getInstance().cleanAll(binding.mapView);
        XLog.dReport("Location onToggled ..." + devices);
        for (EquipmentListBean.DataBean.Device device : devices) {
            detailViewMode.getLastLocationInfo(device.getImei());
        }
    }

    private int saveHeight = 0;


    private void toggle() {
        ValueAnimator valueAnimator = null;
        XLog.dReport("toggle ... " + binding.recyclerView.getHeight() + "," + saveHeight);
        if (binding.recyclerView.getHeight() > saveHeight && binding.recyclerView.getVisibility() == View.INVISIBLE) {
            saveHeight = binding.recyclerView.getHeight();
            valueAnimator = ValueAnimator.ofInt(0, saveHeight);
            binding.recyclerView.setVisibility(View.VISIBLE);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(binding.toggle, "rotationX", 0, 180);
            rotation.start();
        } else if (binding.recyclerView.getHeight() == 0) {
            XLog.dReport("toggle ... AAA");
            valueAnimator = ValueAnimator.ofInt(1, saveHeight);
            binding.recyclerView.setVisibility(View.VISIBLE);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(binding.toggle, "rotationX", 0, 180);
            rotation.start();
        } else {
            XLog.dReport("toggle ... BBB");
            saveHeight = binding.recyclerView.getHeight();
            valueAnimator = ValueAnimator.ofInt(binding.recyclerView.getHeight(), 0);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(binding.toggle, "rotationX", 180, 0);
            rotation.start();
            onToggled();
        }
        ValueAnimator finalValueAnimator = valueAnimator;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取当前的height值

                int h = (Integer) finalValueAnimator.getAnimatedValue();
                XLog.dReport("onAnimationUpdate nani ..." + h + "," + adapter.getItemCount());
                //动态更新view的高度
                binding.recyclerView.getLayoutParams().height = h;
                binding.recyclerView.requestLayout();
            }
        });
        valueAnimator.start();
    }

    private EquipmentListBean.DataBean.Device currentDevice = null;

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
//                    toggle();
                    binding.recyclerView.toggle();
                    break;
                case MSG_CENTER_TO:
                    EquipmentListBean.DataBean.Device device1 = (EquipmentListBean.DataBean.Device) msg.obj;
                    centerTo(device1);
                    break;
                case MSG_FLUSH:
                    onToggled();
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
            MarkerInfoBinding markerInfoBinding = MarkerInfoBinding.inflate(getLayoutInflater(),binding.mapView,false);
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
//        binding.mapView.getMap().animateMapStatus(MapStatusUpdateFactory.scrollBy(350, 0));
    }

    private void centerTo(EquipmentListBean.DataBean.Device device) {
        XLog.dReport("centerTo ... " + device + "," + infoWindowShowing);
        if (device != null && !infoWindowShowing) {
            binding.mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(device.getLocation()));
        }
    }

    @Override
    public void onOpened() {
        XLog.dReport("onOpened ...");
        ObjectAnimator rotation = ObjectAnimator.ofFloat(binding.toggle, "rotationX", 0, 180);
        rotation.start();
    }

    @Override
    public void onClosed() {
        XLog.dReport("onClosed ...");
        ObjectAnimator rotation = ObjectAnimator.ofFloat(binding.toggle, "rotationX", 180, 0);
        rotation.start();
        onToggled();
    }
}
