package com.sate7.wlj.developerreader.sate7gems.view.xpop;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.core.BottomPopupView;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.Sate7EGMSApplication;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.EquipmentAdapter;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.EquipmentListViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;

public class DeviceAddPop extends BottomPopupView implements OnRefreshLoadMoreListener {
    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;
    private EquipmentAdapter equipmentAdapter;
    private EquipmentListViewModel equipmentListViewModel;
    private FragmentActivity activity;

    public DeviceAddPop(@NonNull Context context) {
        super(context);
        if (context instanceof Activity) {
            activity = (FragmentActivity) context;
            equipmentListViewModel = ViewModelProviders.of(activity).get(EquipmentListViewModel.class);
            equipmentListViewModel.listAllEquipment(1);
        }
    }

    public void showSelect(ArrayList<EquipmentListBean.DataBean.Device> devices){
        XLog.dReport("AddPop showSelected ... " + devices);
        equipmentAdapter.showSelected(devices);
    }

    protected int getImplLayoutId() {
        return R.layout.fence_create_add_device;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        recyclerView = findViewById(R.id.fenceCreateRecyclerView);
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshLoadMoreListener(this);
        equipmentAdapter = new EquipmentAdapter(getContext());

        recyclerView.setAdapter(equipmentAdapter);
        equipmentListViewModel.observeDeviceListResult(activity, new Observer<EquipmentListViewModel.EquipmentListResult>() {
            @Override
            public void onChanged(EquipmentListViewModel.EquipmentListResult equipmentListResult) {
                hasMore = equipmentListResult.isHasMore();
                if (equipmentAdapter.getDevices().isEmpty()) {
                    equipmentAdapter.update(equipmentListResult.getDevices());
                } else if (refreshLayout.getState() == RefreshState.Loading) {
                    refreshLayout.finishLoadMore();
                    equipmentAdapter.append(equipmentListResult.getDevices());
                }
            }
        });
    }

    public ArrayList<EquipmentListBean.DataBean.Device> getSelectedDevice() {
        return equipmentAdapter.getSelectedDevices();
    }

    private int currentPageNumber = 1;
    private boolean hasMore = false;

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (hasMore) {
            currentPageNumber++;
            equipmentListViewModel.listAllEquipment(currentPageNumber);
        } else {
            refreshLayout.finishLoadMore();
            refreshLayout.setNoMoreData(true);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh(1000);
    }
}
