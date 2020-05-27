package com.sate7.wlj.developerreader.sate7gems.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.ActivityTestBinding;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.RetrofitServerImp;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.DeviceManageAdapter;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.EquipmentListViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.File;
import java.io.FileOutputStream;


public class TestActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshLoadMoreListener, Observer<EquipmentListViewModel.EquipmentListResult> {
    private final static String TAG = "TestActivity";
    private ActivityTestBinding binding;
    private EquipmentListViewModel equipmentListViewModel;
    private DeviceManageAdapter deviceManageAdapter;
    private TopLayoutManager layoutManager;
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
        deviceManageAdapter = new DeviceManageAdapter(this);
        binding.recyclerView.setAdapter(deviceManageAdapter);
        layoutManager = new TopLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
//        binding.recyclerView.addItemDecoration(new MyItemDecoration());
//        binding.recyclerView.addItemDecoration(new StickyItemDecoration());
        binding.recyclerView.addItemDecoration(new MyDrawItemDecoration());
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
//                judge();
                RetrofitServerImp.getInstance().getHomeArticle();
                break;
            default:
                break;
        }
    }

    private void judge() {
        File file = new File("/storage/emulated/0");
        boolean exists = file.exists();
        boolean isFile = file.isFile();
        boolean isDirectory = file.isDirectory();
        boolean canRead = file.canRead();
        boolean canWrite = file.canWrite();
        boolean canExecute = file.canExecute();
        XLog.d(TAG,"judge destination ... " + exists + "," + isFile + "," + isDirectory + "," + canRead + "," + canWrite + "," + canExecute);
        File from = new File("/system/media/");
        exists = from.exists();
        isDirectory = from.isDirectory();
        canWrite = from.canWrite();
        canRead = from.canRead();
        canExecute = from.canExecute();
        XLog.d(TAG,"judge source ... " + exists + "," + isFile + "," + isDirectory + "," + canRead + "," + canWrite + "," + canExecute);
        CopyMapService.startCopy(this);
    }

    private void createPrivateFile()//向内部存储写文件
    {
        String filename = "myfile.txt";
        String string = "Hello world!";
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
            Toast.makeText(TestActivity.this,"文件位置    在"+getFilesDir().getAbsolutePath(),Toast.LENGTH_LONG).show();
        } catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(TestActivity.this,"出错误了哦",Toast.LENGTH_SHORT).show();
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
            deviceManageAdapter.update(equipmentListResult.getDevices(), true);
            binding.refreshLayout.finishLoadMore(true);
        } else {
            deviceManageAdapter.update(equipmentListResult.getDevices(), true);
        }

    }

    private static class MyItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int childAdapterPosition = parent.getChildAdapterPosition(view);
            int childLayoutPosition = parent.getChildLayoutPosition(view);
            log("getItemOffsets ... " + outRect.left + "," + outRect.top + "," + outRect.right + "," + outRect.bottom);
            log("getItemOffsets childAdapterPosition = " + childAdapterPosition + ",childLayoutPosition = " + childLayoutPosition);
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
