package com.sate7.wlj.developerreader.sate7gems.view.adapter;

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

public class WarningInfoAdapter extends RecyclerView.Adapter {
    private WarningInfoItemBinding binding;
    private ArrayList<WarningInfoBean.DataBean.MessagesBean> warningMessages = new ArrayList<>();

    public void update(ArrayList<WarningInfoBean.DataBean.MessagesBean> messagesBeans,boolean append) {
        if(append){
            warningMessages.addAll(messagesBeans);
        }else{
            this.warningMessages = messagesBeans;
        }
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
    }

    @Override
    public int getItemCount() {
        return warningMessages.size();
    }

    private static class WarningHolder extends RecyclerView.ViewHolder {
        private TextView deviceName;
        private TextView message;
        private TextView time;

        public WarningHolder(@NonNull WarningInfoItemBinding binding) {
            super(binding.getRoot());
            deviceName = binding.warningDevice;
            message = binding.warningMessage;
            time = binding.warningTime;
        }
    }
}
