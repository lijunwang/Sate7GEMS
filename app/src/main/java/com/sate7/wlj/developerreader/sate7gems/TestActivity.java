package com.sate7.wlj.developerreader.sate7gems;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.StateRecyclerView;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.EquipmentAdapter;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.EquipmentListViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class TestActivity extends AppCompatActivity implements View.OnClickListener, StateRecyclerView.StateListener {

    private StateRecyclerView recyclerView;
    private EquipmentListViewModel equipmentListViewModel;
    private EquipmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.testBt).setOnClickListener(this);
        recyclerView = findViewById(R.id.testRV);

        equipmentListViewModel = ViewModelProviders.of(this).get(EquipmentListViewModel.class);
        adapter = new EquipmentAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setStateListener(this);
        equipmentListViewModel.listAllEquipment();

        equipmentListViewModel.getAllDevice().observe(this, new Observer<ArrayList<EquipmentListBean.DataBean.Device>>() {
            @Override
            public void onChanged(ArrayList<EquipmentListBean.DataBean.Device> devices) {
                adapter.update(devices);
            }
        });
    }

    private boolean isOpened = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.testBt:
                recyclerView.toggle();
                break;
        }
    }

    @Override
    public void onOpened() {
        XLog.dReport("RV state onOpened ...");
    }

    @Override
    public void onClosed() {
        XLog.dReport("RV state onClosed ...");
    }
}
