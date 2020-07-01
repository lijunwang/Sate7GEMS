package com.sate7.wlj.developerreader.sate7gems.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
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
    private Comparator<EquipmentListBean.DataBean.Device> comparator = new Comparator<EquipmentListBean.DataBean.Device>() {
        @Override
        public int compare(EquipmentListBean.DataBean.Device o1, EquipmentListBean.DataBean.Device o2) {
            return o2.getLastUpdateTime().compareTo(o1.getLastUpdateTime());
        }
    };

    public EquipmentAdapter(Context context) {
        this.context = context;
    }

    public EquipmentAdapter(Context context, TYPE type) {
        this.context = context;
        this.type = type;
        selectedDeviceImei = (HashSet<String>) SPUtils.getInstance().getStringSet(type == TYPE.WARNING ? Constants.SELECT_WARNING_IMEIS : Constants.SELECT_LOCATION_IMEIS, new HashSet<>());
        XLog.dReport("selectedDeviceImei ... " + type + "," + selectedDeviceImei);
    }

    public ArrayList<EquipmentListBean.DataBean.Device> findAndFilter(String input, ArrayList<EquipmentListBean.DataBean.Device> from){
        XLog.dReport("findAndFilter gogoww ... " + input + "," + from.size() + "," + devices.size());
        ArrayList<EquipmentListBean.DataBean.Device> copy = new ArrayList<>();
        copy.addAll(devices);
        if(TextUtils.isEmpty(input)){
            return copy;
        }
        ArrayList<EquipmentListBean.DataBean.Device> selected = new ArrayList<>();
        for(EquipmentListBean.DataBean.Device device:from){
            if(device.getImei().contains(input) || device.getTag().contains(input)){
                selected.add(device);
            }
        }
        if(!selected.isEmpty()){
            devices.clear();
            devices.addAll(selected);
            notifyDataSetChanged();
            return copy;
        }else{
            append(from);
        }
        XLog.dReport("findAndFilter ... " + input + "," + selected.size() + "," + selected);
        return copy;
    }
    public ArrayList<EquipmentListBean.DataBean.Device> getSelectedDevices() {
        ArrayList<EquipmentListBean.DataBean.Device> selectedDevices = new ArrayList<>();
        if (type != null) {
            for (EquipmentListBean.DataBean.Device device : devices) {
                if (selectedDeviceImei.contains(device.getImei())) {
                    selectedDevices.add(device);
                }
            }
        } else {
            for (EquipmentListBean.DataBean.Device device : devices) {
                if (device.isChecked()) {
                    XLog.dReport("getSelectedDevices bb " + device.getTag());
                    selectedDevices.add(device);
                }
            }
        }
        return selectedDevices;
    }

    public void showSelected(ArrayList<EquipmentListBean.DataBean.Device> checkedDevices) {
        for (EquipmentListBean.DataBean.Device device : checkedDevices) {
            for (EquipmentListBean.DataBean.Device d : devices) {
                if(device.getImei().equals(d.getImei())){
                    XLog.dReport("showSelected ... " + d);
                    d.setChecked(true);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<EquipmentListBean.DataBean.Device> getSelectedDevices(TYPE type) {
        Set<String> selectedSet = SPUtils.getInstance().getStringSet(type == TYPE.WARNING ? Constants.SELECT_WARNING_IMEIS : Constants.SELECT_LOCATION_IMEIS);
        ArrayList<EquipmentListBean.DataBean.Device> selectedDevices = new ArrayList<>();
        for (EquipmentListBean.DataBean.Device device : devices) {
            if (device.isChecked() && selectedSet.contains(device.getImei())) {
                selectedDevices.add(device);
            }
        }
        return selectedDevices;
    }

    public void onToggled(TYPE type) {
        Set<String> selectImei = new HashSet<>();
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
        XLog.dReport("selectedDeviceImei onToggled ... " + type + "," + selectImei);
    }

    private void save2SP() {
        XLog.dReport("save2SP ... " + type + "," + selectedDeviceImei);
        switch (type) {
            case WARNING:
                SPUtils.getInstance().put(Constants.SELECT_WARNING_IMEIS, selectedDeviceImei);
                break;
            case LOCATION:
                SPUtils.getInstance().put(Constants.SELECT_LOCATION_IMEIS, selectedDeviceImei);
                break;
        }
    }

    private final boolean filterUnknown = false;
    private boolean isUnknown(EquipmentListBean.DataBean.Device device){
        if(device.getImei().startsWith("123456789") || device.getImei().startsWith("86793503000")){
            return true;
        }

        return false;
    }
    public void update(ArrayList<EquipmentListBean.DataBean.Device> devices) {
//        XLog.dReport("update old ..." + this.devices);
//        XLog.dReport("update new ..." + devices);
        HashSet<EquipmentListBean.DataBean.Device> set = new HashSet<>(this.devices);//去重复
        if(filterUnknown){
            this.devices.clear();
            for(EquipmentListBean.DataBean.Device device:devices){
                if(!isUnknown(device)){
//                    this.devices.add(device);
                    set.add(device);
                }
            }
        }else{
//            this.devices = devices;
            set.addAll(devices);
        }
        this.devices.clear();
        this.devices.addAll(set);
        Collections.sort(this.devices,comparator);
        notifyDataSetChanged();
    }

    public void append(ArrayList<EquipmentListBean.DataBean.Device> devices) {
        HashSet<EquipmentListBean.DataBean.Device> set = new HashSet<>(this.devices);
        if(filterUnknown){
            for(EquipmentListBean.DataBean.Device device:devices){
                if(!isUnknown(device)){
                    set.add(device);
//                    this.devices.add(device);
                }
            }
        }else{
//            this.devices.addAll(devices);
            set.addAll(devices);
        }
        this.devices.clear();
        this.devices.addAll(set);
        Collections.sort(this.devices,comparator);
        notifyDataSetChanged();
    }

    public void clean(){
        this.devices.clear();
        notifyDataSetChanged();
    }

    public ArrayList<EquipmentListBean.DataBean.Device> getDevices() {
        return devices;
    }

    public void update(ArrayList<EquipmentListBean.DataBean.Device> devices, boolean append) {
        int position = devices.size();
        if (append) {
            this.devices.addAll(devices);
        }

        Collections.sort(this.devices,comparator);
        notifyItemInserted(position);
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
                        if (isChecked) {
                            selectedDeviceImei.add(device.getImei());
                        } else {
                            selectedDeviceImei.clear();
                        }

                    }
//                    save2SP();
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 500);
                } else {
                    int realPosition = position - 1;
                    EquipmentListBean.DataBean.Device device = devices.get(realPosition);
                    device.setChecked(isChecked);
                    XLog.dReport("selected debug .... " + device.getTag() + "," + isChecked + "," + type);
                    if (type != null) {
                        if (isChecked && !selectedDeviceImei.contains(device.getImei())) {
                            selectedDeviceImei.add(device.getImei());
                        }

                        if (!isChecked && selectedDeviceImei.contains(device.getImei())) {
                            //to remove
                            for (Iterator<String> i = selectedDeviceImei.iterator(); i.hasNext(); ) {
                                String element = i.next();
                                if (device.getImei().equals(element)) {
                                    XLog.dReport("remove ... " + element + "," + device.getTag());
                                    i.remove();
                                    save2SP();
                                }
                            }
                        }
                    }else{

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
            if (type != null) {
                XLog.dReport("selectedDeviceImei bind check ... " + type + "," + device.getTag() + "," + selectedDeviceImei + "," + device.isChecked() + "," + selectedDeviceImei.contains(device.getImei()));
                equipmentHolder.checkBox.setChecked((selectedDeviceImei.contains(device.getImei())));
            } else {
                equipmentHolder.checkBox.setChecked(device.isChecked());
            }
            equipmentHolder.imei.setText(device.getImei());
            if (TextUtils.isEmpty(device.getTag())) {
                equipmentHolder.tag.setVisibility(View.GONE);
            } else {
                equipmentHolder.tag.setText(device.getTag());
                equipmentHolder.tag.setVisibility(View.VISIBLE);
            }
        }

        //LiuQiong design
        equipmentHolder.container.setBackgroundColor(context.getResources().getColor(position % 2 == 0 ? R.color.item_gray : R.color.colorWhite));

    }

    @Override
    public int getItemCount() {
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
