package com.sate7.wlj.developerreader.sate7gems.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.WarningInfoItemBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.WarningInfoBean;

import java.util.ArrayList;
import java.util.HashSet;

public class WarningInfoAdapter extends RecyclerView.Adapter {
    private WarningInfoItemBinding binding;
    private ArrayList<WarningInfoBean.DataBean.MessagesBean> homePageWarnings = new ArrayList<>();
    private ArrayList<WarningInfoBean.DataBean.MessagesBean> warningMessages = new ArrayList<>();
    private Context context;

    public WarningInfoAdapter(Context context) {
        this.context = context;
    }


    public void cleanHome(){
        warningMessages.removeAll(homePageWarnings);
        homePageWarnings.clear();
    }

    public void showHome(ArrayList<WarningInfoBean.DataBean.MessagesBean> warningInfoBeans){
        if(homePageWarnings.isEmpty()){
            homePageWarnings.addAll(warningInfoBeans);
            warningMessages.addAll(homePageWarnings);
            notifyDataSetChanged();
        }
    }

    public void update(ArrayList<WarningInfoBean.DataBean.MessagesBean> messagesBeans, boolean append) {
        if (append) {
            warningMessages.addAll(messagesBeans);
        } else {
            this.warningMessages = messagesBeans;
        }
        notifyDataSetChanged();
    }

    public void removeWarnings(HashSet<String> imeis) {
        ArrayList<WarningInfoBean.DataBean.MessagesBean> toRemove = new ArrayList<>();
        for (String imei : imeis) {
            for (WarningInfoBean.DataBean.MessagesBean messagesBean : warningMessages) {
                if (messagesBean.getDevice().contains(imei)) {
                    toRemove.add(messagesBean);
                }
            }
        }
        warningMessages.removeAll(toRemove);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.warning_info_item, parent, false);
        return new WarningHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WarningHolder warningHolder = (WarningHolder) holder;
        warningHolder.deviceName.setText(warningMessages.get(position).getDevice());
        warningHolder.message.setText(warningMessages.get(position).getMessage());
        warningHolder.time.setText(warningMessages.get(position).getDateTime());
        warningHolder.binding.getRoot().setBackgroundColor(context.getResources().getColor(position % 2 == 0 ? R.color.item_gray : R.color.colorWhite));
    }

    @Override
    public int getItemCount() {
        return warningMessages.size();
    }

    private static class WarningHolder extends RecyclerView.ViewHolder {
        private TextView deviceName;
        private TextView message;
        private TextView time;
        private WarningInfoItemBinding binding;

        public WarningHolder(@NonNull WarningInfoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            deviceName = binding.warningDevice;
            message = binding.warningMessage;
            time = binding.warningTime;
        }
    }
}
