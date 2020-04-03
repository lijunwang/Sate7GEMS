package com.sate7.wlj.developerreader.sate7gems.view.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.FragmentWarningBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.WarningInfoBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.MyItemDecoration;
import com.sate7.wlj.developerreader.sate7gems.view.StateRecyclerView;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.EquipmentAdapter;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.WarningInfoAdapter;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.EquipmentListViewModel;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.WarningViewModel;

import java.util.ArrayList;
import java.util.List;

public class WarningFragment extends BaseFragment implements View.OnClickListener, StateRecyclerView.StateListener {
    private FragmentWarningBinding binding;
    private WarningViewModel warningViewModel;
    private WarningInfoAdapter warningInfoAdapter;
    private EquipmentListViewModel equipmentListViewModel;
    private EquipmentAdapter equipmentAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        warningViewModel = ViewModelProviders.of(this).get(WarningViewModel.class);
        equipmentListViewModel = ViewModelProviders.of(this).get(EquipmentListViewModel.class);

        equipmentAdapter = new EquipmentAdapter(getContext(), EquipmentAdapter.TYPE.WARNING);
        warningInfoAdapter = new WarningInfoAdapter();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_warning, container, false);
        return binding.getRoot();
    }

    @Override
    public void startLoadData() {
        super.startLoadData();
//        warningViewModel.fetchWarningInfo("");
        equipmentListViewModel.listAllEquipment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.warningTop.setOnClickListener(this);
        binding.warningDeviceRV.setAdapter(equipmentAdapter);
        binding.warningInfoRV.setAdapter(warningInfoAdapter);
        binding.warningDeviceRV.setStateListener(this);
        binding.warningInfoRV.addItemDecoration(new MyItemDecoration());

        equipmentListViewModel.getAllDevice().observe(this, new Observer<ArrayList<EquipmentListBean.DataBean.Device>>() {
            @Override
            public void onChanged(ArrayList<EquipmentListBean.DataBean.Device> devices) {
//                binding.warningDeviceRV.setVisibility(View.INVISIBLE);
                equipmentAdapter.update(devices);
            }
        });

        warningViewModel.getWarningInfoList().observe(this, new Observer<List<WarningInfoBean.DataBean.MessagesBean>>() {
            @Override
            public void onChanged(List<WarningInfoBean.DataBean.MessagesBean> messagesBeans) {
                XLog.dReport("warning info ... " + messagesBeans.size());
                warningInfoAdapter.update((ArrayList<WarningInfoBean.DataBean.MessagesBean>) messagesBeans, true);
                if (warningInfoAdapter.getItemCount() > 0) {
                    binding.warningEmpty.setVisibility(View.INVISIBLE);
                } else {
                    binding.warningEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.warning_top:
//                mHandler.removeMessages(MSG_TOGGLE);
//                mHandler.sendEmptyMessageDelayed(MSG_TOGGLE, 200);
                binding.warningDeviceRV.toggle();
                break;
        }
    }

    private void startLoadWarningInfo() {
        ArrayList<EquipmentListBean.DataBean.Device> devices = equipmentAdapter.getSelectedDevices(EquipmentAdapter.TYPE.WARNING);
        XLog.dReport("warning startLoadWarningInfo ..." + devices);
        if(devices.isEmpty()){
            binding.warningEmpty.setVisibility(View.VISIBLE);
            binding.warningEmpty.setText(R.string.warning_no_device);
        }
        warningInfoAdapter.update(new ArrayList<>(), false);
        for (EquipmentListBean.DataBean.Device device : devices) {
            warningViewModel.fetchWarningInfo(device.getImei());
        }
    }

    private boolean isOpened = false;
    private int deviceListHeight = 0;
    private final int MSG_TOGGLE = 0x10;
    private final int MSG_LOAD_WARNING = 0x11;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_TOGGLE:
//                    toggle();
//                    binding.warningDeviceRV.toggle();
                    break;
                case MSG_LOAD_WARNING:
                    equipmentAdapter.onToggled(EquipmentAdapter.TYPE.WARNING);
                    startLoadWarningInfo();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mHandler.sendEmptyMessageDelayed(MSG_LOAD_WARNING, 1000);
    }

    private void toggle() {
        ValueAnimator animator = null;
        XLog.dReport("warning toggle ... " + isOpened + "," + deviceListHeight);
        if (isOpened) {//to close
            deviceListHeight = binding.warningDeviceRV.getHeight();
            animator = ValueAnimator.ofInt(deviceListHeight, 1);//nani 0 is bug
            isOpened = false;
            mHandler.removeMessages(MSG_LOAD_WARNING);
            mHandler.sendEmptyMessageDelayed(MSG_LOAD_WARNING, 500);
        } else {//to open
            animator = ValueAnimator.ofInt(0, deviceListHeight);
            isOpened = true;
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                binding.warningDeviceRV.getLayoutParams().height = height;
                binding.warningDeviceRV.requestLayout();
                if (binding.warningDeviceRV.getVisibility() != View.VISIBLE) {
                    binding.warningDeviceRV.setVisibility(View.VISIBLE);
                }
            }
        });
        animator.start();
    }


    @Override
    public void onOpened() {
        XLog.dReport("warning onOpened ...");
        ObjectAnimator rotation = ObjectAnimator.ofFloat(binding.warningToggle, "rotationX", 0, 180);
        rotation.start();
    }

    @Override
    public void onClosed() {
        XLog.dReport("warning onClosed ...");
        ObjectAnimator rotation = ObjectAnimator.ofFloat(binding.warningToggle, "rotationX", 180, 0);
        rotation.start();
        mHandler.removeMessages(MSG_LOAD_WARNING);
        mHandler.sendEmptyMessageDelayed(MSG_LOAD_WARNING, 100);
    }
}
