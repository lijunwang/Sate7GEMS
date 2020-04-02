package com.sate7.wlj.developerreader.sate7gems.view.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.Sate7EGMSApplication;
import com.sate7.wlj.developerreader.sate7gems.databinding.FenceDeviceItemBinding;
import com.sate7.wlj.developerreader.sate7gems.databinding.FenceInfoItemBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.util.EndListener;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.ArrayList;
import java.util.List;

public class FenceAdapter extends RecyclerView.Adapter {

    private ArrayList fenceList = new ArrayList();

    public void update(ArrayList<FenceListBean.DataBean.FenceBean> fenceBeanArrayList) {
        for (FenceListBean.DataBean.FenceBean fence : fenceBeanArrayList) {
            fenceList.add(fence);
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_FENCE:
            default:
                return new FenceHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.fence_info_item, parent, false));
            case TYPE_DEVICE:
                return new DeviceHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.fence_device_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_FENCE:
                bindFence(holder, position);
                break;
            case TYPE_DEVICE:
                bindDevice(holder, position);
                break;
        }
    }

    private void expandDevices(ArrayList<String> imeiList, int position) {
        XLog.dReport("expandDevices position == " + position + ",childs ==" + imeiList.size() + "," + fenceList.size());
        for (String imei : imeiList) {
            for (EquipmentListBean.DataBean.Device device : Sate7EGMSApplication.getEquipmentsList()) {
                if (device.getImei().equals(imei)) {
                    position++;
                    fenceList.add(position, device);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void closeUpDevice(ArrayList imeiList, int position) {
        int count = imeiList.size();
        XLog.dReport("closeUpDevice position == " + position + ",childs ==" + count + "," + fenceList.size());
        for (int i = count; i > 0; i--) {
            fenceList.remove(position + i);
        }
        notifyDataSetChanged();
    }

    private final int MSG_OPEN = 0x10;
    private final int MSG_CLOSE = 0x11;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
        }
    };

    private void bindFence(RecyclerView.ViewHolder holder, int position) {
        FenceHolder fenceHolder = (FenceHolder) holder;
        FenceListBean.DataBean.FenceBean fenceBean = (FenceListBean.DataBean.FenceBean) fenceList.get(position);
        fenceHolder.setFence(fenceBean);
        if (fenceBean.isOpened()) {
            fenceHolder.fenceToggleImageView.setRotation(180);
        } else {
            fenceHolder.fenceToggleImageView.setRotation(0);
        }
        fenceHolder.fenceToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> imeiList = fenceBean.getImeis();
                XLog.dReport("onClick ... " + fenceBean.getName() + "," + imeiList + "," + fenceBean.isOpened());
                if (imeiList.isEmpty()) {
                    ToastUtils.showShort(R.string.fence_empty);
                    return;
                }
                if (fenceBean.isOpened()) {
                    fenceBean.setIsOpened(false);
                    closeUpDevice((ArrayList) imeiList, position);
                    ObjectAnimator rotation = ObjectAnimator.ofFloat(fenceHolder.fenceToggleImageView, "rotationX", 180, 0);
//                    rotation.start();
                    rotation.addListener(new EndListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
//                            closeUpDevice((ArrayList) imeiList, position);
                        }
                    });
                } else {
                    fenceBean.setIsOpened(true);
                    expandDevices((ArrayList<String>) imeiList, position);
                    ObjectAnimator rotation = ObjectAnimator.ofFloat(fenceHolder.fenceToggleImageView, "rotationX", 0, 180);
//                    rotation.start();
                    rotation.addListener(new EndListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
//                            expandDevices((ArrayList<String>) imeiList, position);
                        }
                    });
                }

            }

        });


    }

    private void bindDevice(RecyclerView.ViewHolder holder, int position) {
        DeviceHolder deviceHolder = (DeviceHolder) holder;
        deviceHolder.setFenceDevice((EquipmentListBean.DataBean.Device) fenceList.get(position));
    }

    private final int TYPE_FENCE = 0;
    private final int TYPE_DEVICE = 1;

    @Override
    public int getItemViewType(int position) {
        if (fenceList.get(position) instanceof FenceListBean.DataBean.FenceBean) {
            return TYPE_FENCE;
        } else if (fenceList.get(position) instanceof EquipmentListBean.DataBean.Device) {
            return TYPE_DEVICE;
        } else {
            return TYPE_FENCE;
        }
    }

    @Override
    public int getItemCount() {
        return fenceList.size();
    }

    private static class FenceHolder extends RecyclerView.ViewHolder {
        private FenceInfoItemBinding fenceInfoItemBinding;
        private View fenceToggle;
        private ImageView fenceToggleImageView;

        public FenceHolder(@NonNull FenceInfoItemBinding binding) {
            super(binding.getRoot());
            fenceInfoItemBinding = binding;
            fenceToggle = binding.fenceToggle;
            fenceToggleImageView = binding.fenceToggleImage;
        }

        //Binding DATA with xml
        public void setFence(FenceListBean.DataBean.FenceBean fence) {
            fenceInfoItemBinding.setFence(fence);
        }
    }

    private static class DeviceHolder extends RecyclerView.ViewHolder {
        private FenceDeviceItemBinding fenceDeviceItemBinding;

        public DeviceHolder(@NonNull FenceDeviceItemBinding binding) {
            super(binding.getRoot());
            fenceDeviceItemBinding = binding;
        }

        public void setFenceDevice(EquipmentListBean.DataBean.Device device) {
            fenceDeviceItemBinding.setDevice(device);
        }
    }
}
