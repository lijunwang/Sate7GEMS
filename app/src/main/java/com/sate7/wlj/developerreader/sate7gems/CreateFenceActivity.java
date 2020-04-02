package com.sate7.wlj.developerreader.sate7gems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.internal.FlowLayout;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.interfaces.XPopupCallback;
import com.sate7.wlj.developerreader.sate7gems.databinding.ActivityCreateFenceBinding;
import com.sate7.wlj.developerreader.sate7gems.map.BaiduMapHelper;
import com.sate7.wlj.developerreader.sate7gems.net.Sate7GEMSServer;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.xpop.DatePickPop;
import com.sate7.wlj.developerreader.sate7gems.view.xpop.DeviceAddPop;
import com.sate7.wlj.developerreader.sate7gems.view.xpop.FenceMapPointPop;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.FenceViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class CreateFenceActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityCreateFenceBinding binding;
    private DatePickPop datePickPopStart;
    private DatePickPop datePickPopEnd;
    private DeviceAddPop deviceAddPop;
    private XPopupCallback devicePopCallback = new XPopupCallback() {
        @Override
        public void onCreated() {

        }

        @Override
        public void beforeShow() {

        }

        @Override
        public void onShow() {

        }

        @Override
        public void onDismiss() {
            ArrayList<EquipmentListBean.DataBean.Device> devices = deviceAddPop.getSelectedDevice();
            binding.fenceCreateFlex.removeAllViews();
            for (EquipmentListBean.DataBean.Device device : devices) {
                TextView info = new TextView(CreateFenceActivity.this);
                info.setTextSize(8);
                info.setBackgroundResource(R.drawable.device_bg);
                info.setGravity(Gravity.CENTER);
                info.setText(device.getImei() + (TextUtils.isEmpty(device.getTag()) ? "" : "\n" + device.getTag()));
                binding.fenceCreateFlex.addView(info);
            }

        }

        @Override
        public boolean onBackPressed() {
            return false;
        }
    };
    private FenceMapPointPop mapPop;
    private ArrayList<LatLng> mapClickedPoint = new ArrayList<>();
    private BasePopupView baseMapPop;
    private int scrollY = -1;
    private int mapTopY = -1;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private final String format = "yyyy-MM-dd";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    private ArrayList<LatLng> fencePoints = new ArrayList<>();
    private FenceViewModel fenceViewModel;
    private final String MAOHAO = "：";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_fence);
        initViews();
        initToolBar();
        fenceViewModel = ViewModelProviders.of(this).get(FenceViewModel.class);
        fenceViewModel.getCreateResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                XLog.dReport("create result ... " + s);
                if (s.trim().contains("SUCCESS")) {
                    ToastUtils.showShort(R.string.create_success);
                    finish();
                } else {
                    ToastUtils.showShort(R.string.create_failed);
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapPop.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapPop.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapPop.onDestroy();
    }

    private void initToolBar() {
        setSupportActionBar(binding.toolBarContainer.findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.create_fence);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void initViews() {
        binding.createFenceStartTv.setOnClickListener(this);
        binding.createFenceEndTv.setOnClickListener(this);
        binding.fenceAddDevice.setOnClickListener(this);
        binding.fenceAddDeviceImg.setOnClickListener(this);
        binding.fencePointsMap.setOnClickListener(this);
        binding.fencePointsClean.setOnClickListener(this);
        datePickPopStart = new DatePickPop(this, startListener);
        datePickPopEnd = new DatePickPop(this, endListener);
        deviceAddPop = new DeviceAddPop(this);
        mapPop = new FenceMapPointPop(this);
        mapPop.setOnMapClickedListener(new FenceMapPointPop.OnMapClickListener() {
            @Override
            public void onMapClicked(MapView mapView, LatLng latLng) {
                int size = mapClickedPoint.size();
                XLog.dReport("activity onMapClicked ... " + size);
                if (size <= 4) {
                    mapClickedPoint.add(latLng);
                    size = mapClickedPoint.size();
                    for (int i = 0; i < size; i++) {
                        switch (i) {
                            case 0:
                                binding.createFenceP1Lat.setText("" + mapClickedPoint.get(i).latitude);
                                binding.createFenceP1Lng.setText("" + mapClickedPoint.get(i).latitude);
                                break;
                            case 1:
                                binding.createFenceP2Lat.setText("" + mapClickedPoint.get(i).latitude);
                                binding.createFenceP2Lng.setText("" + mapClickedPoint.get(i).latitude);
                                break;
                            case 2:
                                binding.createFenceP3Lat.setText("" + mapClickedPoint.get(i).latitude);
                                binding.createFenceP3Lng.setText("" + mapClickedPoint.get(i).latitude);
                                break;
                            case 3:
                                binding.createFenceP4Lat.setText("" + mapClickedPoint.get(i).latitude);
                                binding.createFenceP4Lng.setText("" + mapClickedPoint.get(i).latitude);
                                break;
                        }
                    }
                    BaiduMapHelper.getInstance().addPoint(mapView, latLng);
                }

            }
        });
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, CreateFenceActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_fence, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_save:
                if (check()) {
                    fenceViewModel.createFence(fenceName, startTime, endTime, fencePoints, selectDeviceImeis);
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createFenceStartTv:
                new XPopup.Builder(this).asCustom(datePickPopStart).show();
                break;
            case R.id.createFenceEndTv:
                new XPopup.Builder(this).asCustom(datePickPopEnd).show();
                break;
            case R.id.fenceAddDevice:
            case R.id.fenceAddDeviceImg:
                new XPopup.Builder(this).setPopupCallback(devicePopCallback).asCustom(deviceAddPop).show();
                break;
            case R.id.fencePointsClean:
                mapPop.clean();
                mapClickedPoint.clear();
                baseMapPop.show();
                binding.srcollView.smoothScrollTo(0, mapTopY);
                binding.createFenceP1Lat.setText("");
                binding.createFenceP1Lng.setText("");
                binding.createFenceP2Lat.setText("");
                binding.createFenceP2Lng.setText("");
                binding.createFenceP3Lat.setText("");
                binding.createFenceP3Lng.setText("");
                binding.createFenceP4Lat.setText("");
                binding.createFenceP4Lng.setText("");
                break;
            case R.id.fencePointsMap:
                if (baseMapPop == null) {
                    scrollY = (int) binding.createFencePoints.getY();
                    mapTopY = (int) binding.createFencePoints.getY();
                    binding.srcollView.smoothScrollBy(0, scrollY);
                    mapPop.setPopupHeight((int) (ScreenUtils.getScreenHeight() - binding.createFencePoints.getHeight()) - BarUtils.getStatusBarHeight() - BarUtils.getActionBarHeight());
                    baseMapPop = new XPopup.Builder(this).setPopupCallback(new XPopupCallback() {
                        @Override
                        public void onCreated() {
                            XLog.dReport("XPopupCallback onCreated");
                        }

                        @Override
                        public void beforeShow() {
                            XLog.dReport("XPopupCallback beforeShow ww");
                            mapPop.onResume();
                        }

                        @Override
                        public void onShow() {
                            XLog.dReport("XPopupCallback onShow");
                        }

                        @Override
                        public void onDismiss() {
                            XLog.dReport("XPopupCallback onDismiss 22");
                            mapPop.onPause();
                        }

                        @Override
                        public boolean onBackPressed() {
                            return false;
                        }
                    }).asCustom(mapPop).show();
                    binding.fencePointsClean.setVisibility(View.VISIBLE);
                } else {
                    baseMapPop.show();
                    binding.srcollView.smoothScrollTo(0, mapTopY);
                }
                break;
            default:
                break;
        }
    }

    private DatePicker.OnDateChangedListener startListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            XLog.dReport("startListener onDateChanged ... " + year + "," + monthOfYear + "," + dayOfMonth);
//            binding.createFenceStartTv.setText(getResources().getString(R.string.start_time) + ":" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, monthOfYear);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            binding.createFenceStartTv.setText(getResources().getString(R.string.start_time) + MAOHAO + simpleDateFormat.format(startCalendar.getTime()));
        }
    };
    private DatePicker.OnDateChangedListener endListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            XLog.dReport("endListener onDateChanged ... " + year + "," + monthOfYear + "," + dayOfMonth);
//            binding.createFenceEndTv.setText(getResources().getString(R.string.end_time) + ":" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, monthOfYear);
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            binding.createFenceEndTv.setText(getResources().getString(R.string.end_time) + "：" + simpleDateFormat.format(endCalendar.getTime()));
        }
    };

    private String fenceName;
    private String startTime;
    private String endTime;
    private ArrayList<String> selectDeviceImeis = new ArrayList<>();

    private boolean check() {
        XLog.dReport("check ....");
        fenceName = binding.createFenceET.getText().toString().trim();
        if (TextUtils.isEmpty(fenceName)) {
            binding.createFenceET.setError(getResources().getString(R.string.fence_name_empty));
            return false;
        }
        startTime = binding.createFenceStartTv.getText().toString().trim();
        endTime = binding.createFenceEndTv.getText().toString().trim();
        if (TextUtils.isEmpty(startTime) ||
                TextUtils.isEmpty(endTime)) {
            ToastUtils.showShort(R.string.fence_start_end_empty);
            return false;
        } else {
            startTime = startTime.split(MAOHAO)[1];
            endTime = endTime.split(MAOHAO)[1];
        }
        if (endCalendar.getTime().getTime() <= System.currentTimeMillis()) {
            ToastUtils.showShort(R.string.fence_end_too_late);
            return false;
        }
        ArrayList<EquipmentListBean.DataBean.Device> devices = deviceAddPop.getSelectedDevice();
        if (devices.size() == 0) {
            ToastUtils.showShort(R.string.fence_add_device);
            return false;
        } else {
            selectDeviceImeis.clear();
            for (EquipmentListBean.DataBean.Device device : devices) {
                selectDeviceImeis.add(device.getImei());
            }
        }
        LatLng p1 = null, p2 = null, p3 = null, p4 = null;
        fencePoints.clear();
        try {
            p1 = new LatLng(Double.parseDouble(binding.createFenceP1Lat.getText().toString().trim()), Double.parseDouble(binding.createFenceP1Lng.getText().toString().trim()));
            fencePoints.add(p1);
        } catch (Exception e) {
            XLog.dReport("check exception ... " + e.getMessage());
        }

        try {
            p2 = new LatLng(Double.parseDouble(binding.createFenceP2Lat.getText().toString().trim()), Double.parseDouble(binding.createFenceP2Lng.getText().toString().trim()));
            fencePoints.add(p2);
        } catch (Exception e) {
            XLog.dReport("check exception ... " + e.getMessage());
        }

        try {
            p3 = new LatLng(Double.parseDouble(binding.createFenceP3Lat.getText().toString().trim()), Double.parseDouble(binding.createFenceP3Lng.getText().toString().trim()));
            fencePoints.add(p3);
        } catch (Exception e) {
            XLog.dReport("check exception ... " + e.getMessage());
        }

        try {
            p4 = new LatLng(Double.parseDouble(binding.createFenceP4Lat.getText().toString().trim()), Double.parseDouble(binding.createFenceP4Lng.getText().toString().trim()));
            fencePoints.add(p4);
        } catch (Exception e) {
            XLog.dReport("check exception ... " + e.getMessage());
        }

        if (fencePoints.size() < 3) {
            ToastUtils.showShort(R.string.fence_point_pair);
            return false;
        }
        return true;
    }
}
