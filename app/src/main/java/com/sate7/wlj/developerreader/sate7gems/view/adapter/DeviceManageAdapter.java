package com.sate7.wlj.developerreader.sate7gems.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.core.PositionPopupView;
import com.lxj.xpopup.enums.PopupPosition;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.DeviceManageItemBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;

import java.util.ArrayList;

public class DeviceManageAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<EquipmentListBean.DataBean.Device> devices = new ArrayList<>();

    public DeviceManageAdapter(Context context) {
        this.context = context;
    }

    public void update(ArrayList<EquipmentListBean.DataBean.Device> deviceArrayList, boolean append) {
        if (append) {
            devices.addAll(deviceArrayList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceManageHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.device_manage_item, parent, false));
    }

    public void updateItem(EquipmentListBean.DataBean.Device device) {
        for (EquipmentListBean.DataBean.Device d : devices) {
            if (device.getImei().equals(d.getImei())) {
                d.setTag(device.getTag());
                d.setBindNumber(device.getBindNumber());
//                TODO set frequency
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DeviceManageHolder deviceManageHolder = (DeviceManageHolder) holder;
        if (position == 0) {
            deviceManageHolder.binding.manageTag.setText(R.string.manage_tag);
            deviceManageHolder.binding.manageImei.setText(R.string.manage_imei);
            deviceManageHolder.binding.manageNumber.setText(R.string.manage_number);
            deviceManageHolder.binding.manageFrq.setText(R.string.manage_frq);
        } else {
            int realPosition = position - 1;
            EquipmentListBean.DataBean.Device device = devices.get(realPosition);
            deviceManageHolder.binding.manageTag.setText(device.getTag());
            deviceManageHolder.binding.manageImei.setText(device.getImei());
            deviceManageHolder.binding.manageNumber.setText(device.getBindNumber());
            deviceManageHolder.binding.manageFrq.setText("unknown");
            if (itemLongClickListener != null) {
                deviceManageHolder.binding.manageTag.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return itemLongClickListener.onItemLongClicked(device, device.getImei(), R.id.manageImei, device.getTag());
                    }
                });
                deviceManageHolder.binding.manageNumber.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return itemLongClickListener.onItemLongClicked(device, device.getImei(), R.id.manageNumber, device.getBindNumber());
                    }
                });

                deviceManageHolder.binding.manageImei.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new XPopup.Builder(context).atView(deviceManageHolder.binding.manageImei).
                                offsetY(ScreenUtils.getScreenHeight() / 2).offsetX(ScreenUtils.getScreenWidth() / 4).asCustom(new PositionPopupView(context) {
                            @Override
                            protected int getImplLayoutId() {
                                return R.layout.big_imei;
                            }

                            @Override
                            protected void onCreate() {
                                super.onCreate();
                                TextView textView = findViewById(R.id.imei);
                                textView.setText(device.getImei());
                            }
                        }).show();
                        return false;
                    }
                });
                deviceManageHolder.binding.manageFrq.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //TODO server
                        ToastUtils.showLong(R.string.nonsupport);
                        return true;
//                        return itemLongClickListener.onItemLongClicked(device,device.getImei(), R.id.manageFrq, "unknown");
                    }
                });
            }
        }
        deviceManageHolder.binding.getRoot().setBackgroundColor(context.getResources().getColor(position % 2 == 0 ? R.color.item_gray : R.color.colorWhite));
    }

    private OnItemLongClickListener itemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.itemLongClickListener = longClickListener;
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClicked(EquipmentListBean.DataBean.Device device, String iemi, int id, String msg);
    }

    @Override
    public int getItemCount() {
        return devices.size() + 1;
    }

    private static class DeviceManageHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private DeviceManageItemBinding binding;

        public DeviceManageHolder(@NonNull DeviceManageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
