package com.sate7.wlj.developerreader.sate7gems.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.DeviceInfoItemBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.util.Constants;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EquipmentAdapter extends RecyclerView.Adapter {
    public enum TYPE {
        LOCATION,
        WARNING
    }

    private TYPE type;
    private ArrayList<EquipmentListBean.DataBean.Device> devices = new ArrayList<>();
    private HashSet<String> selectedDeviceImei = new HashSet<>();
    private Context context;
    private boolean allChecked = false;

    public EquipmentAdapter(Context context) {
        this.context = context;
    }

    public EquipmentAdapter(Context context, TYPE type) {
        this.context = context;
        this.type = type;
        selectedDeviceImei = (HashSet<String>) SPUtils.getInstance().getStringSet(type == TYPE.WARNING ? Constants.SELECT_WARNING_IMEIS : Constants.SELECT_LOCATION_IMEIS);
        XLog.dReport("selectedDeviceImei ... " + type + "," + selectedDeviceImei);
    }

    public EquipmentAdapter(Context context, ArrayList<EquipmentListBean.DataBean.Device> devices) {
        this.devices = devices;
        this.context = context;
    }

    public ArrayList<EquipmentListBean.DataBean.Device> getSelectedDevices() {
        ArrayList<EquipmentListBean.DataBean.Device> selectedDevices = new ArrayList<>();
        for (EquipmentListBean.DataBean.Device device : devices) {
            if (device.isChecked()) {
                XLog.dReport("checked debug selected " + device.getTag());
                selectedDevices.add(device);
            }
        }
        return selectedDevices;
    }

    public ArrayList<EquipmentListBean.DataBean.Device> getSelectedDevices(TYPE type) {
        Set<String> selectedSet = SPUtils.getInstance().getStringSet(type == TYPE.WARNING ? Constants.SELECT_WARNING_IMEIS : Constants.SELECT_LOCATION_IMEIS);
        ArrayList<EquipmentListBean.DataBean.Device> selectedDevices = new ArrayList<>();
        XLog.dReport("checked debug getSelectedDevices " + type + "," + selectedDevices.size());
        for (EquipmentListBean.DataBean.Device device : devices) {
            if (device.isChecked() && selectedSet.contains(device.getImei())) {
                XLog.dReport("checked debug TYPE selected " + type + "," + device.getTag());
                selectedDevices.add(device);
            }
        }
        return selectedDevices;
    }

    public void onToggled(TYPE type) {
        /*Set<String> selectImei = new HashSet<>();
        ArrayList<EquipmentListBean.DataBean.Device> devices = getSelectedDevices();
        selectedDeviceImei.clear();
        for (EquipmentListBean.DataBean.Device device : devices) {
            selectImei.add(device.getImei());
            selectedDeviceImei.add(device.getImei());
        }
        switch (type) {
            case WARNING:
                SPUtils.getInstance().put(Constants.SELECT_WARNING_IMEIS, selectImei);
                break;
            case LOCATION:
                SPUtils.getInstance().put(Constants.SELECT_LOCATION_IMEIS, selectImei);
                break;
        }
        XLog.dReport("selectedDeviceImei onToggled ... " + type + "," + selectImei);*/
    }

    public void update(ArrayList<EquipmentListBean.DataBean.Device> devices) {
        this.devices = devices;
//        for (EquipmentListBean.DataBean.Device device : devices) {
//            XLog.dReport("checked debug update 22 " + device.getTag() + "," + device.isChecked());
//            if (device.isChecked()) {
//                XLog.dReport("checked debug update " + device.getTag() + "," + device.getImei());
//            }
//        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DeviceInfoItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.device_info_item, parent, false);
        return new EquipmentHolder(binding);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private final int MSG_UPDATE = 0x10;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_UPDATE:
                    notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EquipmentHolder equipmentHolder = (EquipmentHolder) holder;
        equipmentHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (position == 0) {
                    allChecked = isChecked;
                    for (EquipmentListBean.DataBean.Device device : devices) {
                        device.setChecked(allChecked);
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 500);
                } else {
                    int realPosition = position - 1;
                    EquipmentListBean.DataBean.Device device = devices.get(realPosition);
                    device.setChecked(isChecked);
                    if(isChecked && !selectedDeviceImei.contains(device.getImei())){
                        selectedDeviceImei.add(device.getImei());
                    }

                    if(!isChecked && selectedDeviceImei.contains(device.getImei())){
                        //to remove
                        selectedDeviceImei.remove(device.getImei());
                    }

                }
            }
        });

        if (position == 0) {
            equipmentHolder.checkBox.setChecked(allChecked);
            equipmentHolder.imei.setText(context.getResources().getString(R.string.equipment_iemi));
            equipmentHolder.tag.setText(context.getResources().getString(R.string.equipment_tag));
            equipmentHolder.number.setText(context.getResources().getString(R.string.equipment_number));
        } else {
            int realPosition = position - 1;
            EquipmentListBean.DataBean.Device device = devices.get(realPosition);
//            equipmentHolder.checkBox.setChecked(device.isChecked());
            if (type != null) {
//                XLog.dReport("selectedDeviceImei bind check ... " + type + "," + device.getTag() + "," + selectedDeviceImei + "," + device.isChecked() + "," + selectedDeviceImei.contains(device.getImei()));
                equipmentHolder.checkBox.setChecked((selectedDeviceImei.contains(device.getImei())));
//                equipmentHolder.checkBox.setChecked((selectedDeviceImei.contains(device.getImei())) & device.isChecked());
            } else {
                equipmentHolder.checkBox.setChecked(device.isChecked());
            }
            equipmentHolder.imei.setText(device.getImei());
            equipmentHolder.tag.setText(device.getTag());
            equipmentHolder.number.setText("");
        }

    }

    @Override
    public int getItemCount() {
        XLog.dReport("getItemCount ... " + type + "," + selectedDeviceImei);
        return devices.size() + 1;
    }

    private static class EquipmentHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView imei;
        private TextView tag;
        private TextView number;
        private ViewGroup container;

        public EquipmentHolder(@NonNull DeviceInfoItemBinding binding) {
            super(binding.getRoot());
            container = (ViewGroup) binding.getRoot();
            checkBox = binding.deviceChecked;
            imei = binding.deviceImei;
            tag = binding.deviceTag;
            number = binding.deviceLinenumber;
        }
    }
}
