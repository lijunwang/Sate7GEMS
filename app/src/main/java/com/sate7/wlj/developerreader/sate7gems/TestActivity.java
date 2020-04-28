package com.sate7.wlj.developerreader.sate7gems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sate7.wlj.developerreader.sate7gems.databinding.ActivityTestBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.StateRecyclerView;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.DeviceManageAdapter;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.EquipmentAdapter;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.FenceAdapter;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.EquipmentListViewModel;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.FenceViewModel;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.WarningViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

public class TestActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshLoadMoreListener, Observer<EquipmentListViewModel.EquipmentListResult> {
    private final static String TAG = "TestActivity";
    private ActivityTestBinding binding;
    private EquipmentListViewModel equipmentListViewModel;
    private DeviceManageAdapter equipmentAdapter;
    private int currentPageNumber = 1;
    private boolean hasMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        initRefreshLayout();
        initViews();
    }

    private void initViews() {
        equipmentAdapter = new DeviceManageAdapter(this);
        binding.recyclerView.setAdapter(equipmentAdapter);
        binding.recyclerView.addItemDecoration(new MyItemDecoration());

        equipmentListViewModel = ViewModelProviders.of(this).get(EquipmentListViewModel.class);
        equipmentListViewModel.observeDeviceListResult(this, this);
        equipmentListViewModel.listAllEquipment(currentPageNumber);

        binding.floatButton.setOnClickListener(this);

    }

    private void initRefreshLayout() {
        binding.refreshLayout.setOnRefreshLoadMoreListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatButton:
                BottomSheetDialog sheetDialog = new BottomSheetDialog(this);
                sheetDialog.setContentView(R.layout.track_show_date);
                sheetDialog.setTitle("BottomSheetDialog");
                sheetDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (hasMore) {
            currentPageNumber++;
            equipmentListViewModel.listAllEquipment(currentPageNumber);
        } else {
            binding.refreshLayout.setNoMoreData(true);
            binding.refreshLayout.finishLoadMore();
        }

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        binding.refreshLayout.finishRefresh(100);
    }

    @Override
    public void onChanged(EquipmentListViewModel.EquipmentListResult equipmentListResult) {
        hasMore = equipmentListResult.isHasMore();
        log("Test Activity onChanged ... " + binding.refreshLayout.getState() + "," + equipmentListResult.getDevices().size() + "," + hasMore);
        if (binding.refreshLayout.getState() == RefreshState.Loading) {
            equipmentAdapter.update(equipmentListResult.getDevices(), true);
            binding.refreshLayout.finishLoadMore(true);
        } else {
            equipmentAdapter.update(equipmentListResult.getDevices(), true);
        }

    }

    private static class MyItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int childAdapterPosition = parent.getChildAdapterPosition(view);
            int childLayoutPosition = parent.getChildLayoutPosition(view);
            log("getItemOffsets ... " + outRect.left + "," + outRect.top + "," + outRect.right + "," + outRect.bottom);
            log("getItemOffsets childAdapterPosition =  ... " + childAdapterPosition + ",childLayoutPosition = " + childLayoutPosition);
        }

        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.onDraw(c, parent, state);
            log("onDraw ..." + state);
        }

        @Override
        public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            log("onDrawOver ..." + state);
        }
    }

    private static void log(String msg) {
        Log.d(TAG, "" + msg);
    }
}
