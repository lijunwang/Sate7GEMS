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
import androidx.recyclerview.widget.DividerItemDecoration;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.FragmentWarningBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.WarningInfoBean;
import com.sate7.wlj.developerreader.sate7gems.util.Constants;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.MyItemDecoration;
import com.sate7.wlj.developerreader.sate7gems.view.StateRecyclerView;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.EquipmentAdapter;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.WarningInfoAdapter;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.EquipmentListViewModel;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.WarningViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WarningFragment extends BaseFragment implements View.OnClickListener {
    private FragmentWarningBinding binding;
    private WarningViewModel warningViewModel;
    private WarningInfoAdapter warningInfoAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        warningViewModel = ViewModelProviders.of(this).get(WarningViewModel.class);
        warningInfoAdapter = new WarningInfoAdapter(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_warning, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.warningInfoRV.setAdapter(warningInfoAdapter);
        binding.warningInfoRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        warningViewModel.getWarningInfoList().observe(this, new Observer<List<WarningInfoBean.DataBean.MessagesBean>>() {
            @Override
            public void onChanged(List<WarningInfoBean.DataBean.MessagesBean> messagesBeans) {
                XLog.dReport("warning info ... " + messagesBeans.size());
                warningInfoAdapter.update((ArrayList<WarningInfoBean.DataBean.MessagesBean>) messagesBeans, true);
            }
        });

        //if not selected devices
        warningViewModel.fetchHomePageWarnings();
        warningViewModel.getHomePageWarnings().observe(this, new Observer<List<WarningInfoBean.DataBean.MessagesBean>>() {
            @Override
            public void onChanged(List<WarningInfoBean.DataBean.MessagesBean> messagesBeans) {
                XLog.dReport("HomePage warning info ... " + messagesBeans.size());
                warningInfoAdapter.showHome((ArrayList<WarningInfoBean.DataBean.MessagesBean>) messagesBeans);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    //上次有哪些设备
    private ArrayList<EquipmentListBean.DataBean.Device> lastSelectedDevices = new ArrayList<>();

    public void onDeviceSelected(ArrayList<EquipmentListBean.DataBean.Device> selectedDevices) {
        HashSet<String> added = added(selectedDevices, lastSelectedDevices);
        HashSet<String> reduced = reduced(selectedDevices, lastSelectedDevices);
        XLog.dReport("Warning onDrawerClosed diff added " + added);
        XLog.dReport("Warning onDrawerClosed diff reduce " + reduced);
        lastSelectedDevices = selectedDevices;
        if (added.isEmpty() && reduced.isEmpty()) {
            return;
        }
        for (String imei : added) {
            warningViewModel.fetchWarningInfo(imei);
        }
        if (!reduced.isEmpty()) {
            warningInfoAdapter.removeWarnings(reduced);
        }

        if(!selectedDevices.isEmpty()){
            warningInfoAdapter.cleanHome();
        }else{
            warningViewModel.fetchHomePageWarnings();
        }
    }


}
