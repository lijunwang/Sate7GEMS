package com.sate7.wlj.developerreader.sate7gems.view.xpop;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.xpopup.core.BottomPopupView;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.Sate7EGMSApplication;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.EquipmentAdapter;

import java.util.ArrayList;

public class DeviceAddPop extends BottomPopupView {
    private RecyclerView recyclerView;
    private EquipmentAdapter equipmentAdapter;

    public DeviceAddPop(@NonNull Context context) {
        super(context);
    }

    protected int getImplLayoutId() {
        return R.layout.fence_create_add_device;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        recyclerView = findViewById(R.id.fenceCreateRecyclerView);
        equipmentAdapter = new EquipmentAdapter(getContext());
        equipmentAdapter.update(Sate7EGMSApplication.getEquipmentsList());
        recyclerView.setAdapter(equipmentAdapter);
    }

    public ArrayList<EquipmentListBean.DataBean.Device> getSelectedDevice() {
        return equipmentAdapter.getSelectedDevices();
    }
}
