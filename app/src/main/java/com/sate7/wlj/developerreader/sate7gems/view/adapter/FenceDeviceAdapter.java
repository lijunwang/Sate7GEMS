package com.sate7.wlj.developerreader.sate7gems.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.FenceDeviceItemPopBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;

import java.util.ArrayList;

public class FenceDeviceAdapter extends RecyclerView.Adapter {
    private ArrayList<EquipmentListBean.DataBean.Device> devices = new ArrayList<>();
    private Context context;

    public FenceDeviceAdapter(Context context) {
        this.context = context;
    }

    public void update(ArrayList<EquipmentListBean.DataBean.Device> devices, boolean append) {
        if (!append) {
            devices.clear();
        }
        this.devices.addAll(devices);
        notifyDataSetChanged();
    }

    public void clean() {
        this.devices.clear();
    }

    public void append(EquipmentListBean.DataBean.Device device) {
        this.devices.add(device);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fence_device_item_pop, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DeviceViewHolder deviceViewHolder = (DeviceViewHolder) holder;
        deviceViewHolder.binding.setDevice(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }


    private static class DeviceViewHolder extends RecyclerView.ViewHolder {
        private FenceDeviceItemPopBinding binding;

        public DeviceViewHolder(@NonNull FenceDeviceItemPopBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
