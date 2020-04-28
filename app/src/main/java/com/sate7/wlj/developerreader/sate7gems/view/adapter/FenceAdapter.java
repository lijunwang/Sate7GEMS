package com.sate7.wlj.developerreader.sate7gems.view.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
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
import com.lxj.xpopup.XPopup;
import com.sate7.wlj.developerreader.sate7gems.CreateFenceActivity;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.Sate7EGMSApplication;
import com.sate7.wlj.developerreader.sate7gems.databinding.FenceDeviceItemBinding;
import com.sate7.wlj.developerreader.sate7gems.databinding.FenceInfoItemBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.DeviceDetailInfoBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.RetrofitServerImp;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;
import com.sate7.wlj.developerreader.sate7gems.util.EndListener;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.fragment.FenceFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FenceAdapter extends RecyclerView.Adapter {

    private ArrayList fenceList = new ArrayList();
    private final boolean foldStyle = false;
    private FenceFragment context;
    private final boolean filterOutOfDate = true;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public FenceAdapter(FenceFragment context) {
        this.context = context;
    }

    //TODO
    public void update(ArrayList<FenceListBean.DataBean.FenceBean> fenceBeanArrayList) {
//        int size = fenceList.size();
        if (fenceList.isEmpty()) {
            if (filterOutOfDate) {
                for (FenceListBean.DataBean.FenceBean fenceBean : fenceBeanArrayList) {
                    if (effective(fenceBean)) {
                        fenceList.add(fenceBean);
                    }
                }
            } else {
                fenceList.addAll(fenceBeanArrayList);
            }

            notifyDataSetChanged();
            return;
        }
        notifyDataSetChanged();
    }

    private boolean effective(FenceListBean.DataBean.FenceBean fenceBean) {
        try {
            String endTime = fenceBean.getEndDate();
            Date end = simpleDateFormat.parse(endTime);
            if (end.before(Calendar.getInstance().getTime())) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void refreshTop(ArrayList<FenceListBean.DataBean.FenceBean> fenceBeanArrayList) {
        //step one,find new;
        //step two,insert;
    }

    public void appendOne(FenceListBean.DataBean.FenceBean fenceBean) {
        if (filterOutOfDate && effective(fenceBean)) {
            fenceList.add(fenceBean);
        } else {
            fenceList.add(fenceBean);
        }
        notifyDataSetChanged();
    }

    public void append(ArrayList<FenceListBean.DataBean.FenceBean> fenceBeanArrayList) {
        if (filterOutOfDate) {
            for (FenceListBean.DataBean.FenceBean fenceBean : fenceBeanArrayList) {
                if (effective(fenceBean)) {
                    fenceList.add(fenceBean);
                }
            }
        } else {
            fenceList.addAll(fenceBeanArrayList);
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

    private int positionSave = -1;

    private void expandDevices(ArrayList<String> imeiList, int position) {
        XLog.dReport("expandDevices position == " + position + ",childs ==" + imeiList.size() + "," + fenceList.size());
        positionSave = position;
        ArrayList<EquipmentListBean.DataBean.Device> childDevice = new ArrayList<>();
        for (String imei : imeiList) {
            RetrofitServerImp.getInstance().queryDetailInfo(imei, new Server.DetailInfoQueryCallBack() {
                @Override
                public void onDetailQuerySuccess(DeviceDetailInfoBean device) {
                    EquipmentListBean.DataBean.Device basic = device.getData().getBasic();
                    XLog.dReport("expandDevices onDetailQuerySuccess ... " + basic);
                    childDevice.add(basic);
                    positionSave++;
                    fenceList.add(positionSave, basic);
                    notifyItemRangeInserted(positionSave + 1, 1);
                }

                @Override
                public void onDetailQueryFailed(String msg) {
                    XLog.dReport("onDetailQueryFailed ... " + msg);
                }
            });
        }
    }

    private void closeUpDevice(ArrayList imeiList, int position) {
        int count = imeiList.size();
        XLog.dReport("closeUpDevice position == " + position + ",childs ==" + count + "," + fenceList.size());
        for (int i = count; i > 0; i--) {
            fenceList.remove(position + i);
        }
        notifyDataSetChanged();
    }

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

                if (!foldStyle) {
                    //showDialog;
                    context.showDevicesInFence(fenceBean.getName(), (ArrayList<String>) fenceBean.getImeis());
                    return;
                }
                if (fenceBean.isOpened()) {
                    fenceBean.setIsOpened(false);
                    closeUpDevice((ArrayList) imeiList, position);
                    ObjectAnimator rotation = ObjectAnimator.ofFloat(fenceHolder.fenceToggleImageView, "rotationX", 180, 0);
                    rotation.addListener(new EndListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }
                    });
                } else {
                    fenceBean.setIsOpened(true);
                    expandDevices((ArrayList<String>) imeiList, position);
                    ObjectAnimator rotation = ObjectAnimator.ofFloat(fenceHolder.fenceToggleImageView, "rotationX", 0, 180);
                    rotation.addListener(new EndListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }
                    });
                }

            }

        });

        fenceHolder.fenceInfoItemBinding.fenceCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FenceListBean.DataBean.FenceBean fenceBean = (FenceListBean.DataBean.FenceBean) fenceList.get(position);
                XLog.dReport("before create fence start: " + fenceBean.getGeofenceData());
                context.startCreateFenceAct((ArrayList<Double>) fenceBean.getGeofenceData(), fenceBean);
            }
        });
        fenceHolder.fenceInfoItemBinding.getRoot().setBackgroundColor(context.getResources().getColor(position % 2 == 0 ? R.color.item_gray : R.color.colorWhite));
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
