package com.sate7.wlj.developerreader.sate7gems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.sate7.wlj.developerreader.sate7gems.databinding.ActivityDeviceManagerBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.RetrofitServerImp;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;
import com.sate7.wlj.developerreader.sate7gems.util.Constants;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.DeviceManageAdapter;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.EquipmentListViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

public class DeviceManagerActivity extends AppCompatActivity implements OnLoadMoreListener, OnRefreshLoadMoreListener, Observer<EquipmentListViewModel.EquipmentListResult>, DeviceManageAdapter.OnItemLongClickListener {
    private ActivityDeviceManagerBinding binding;
    private EquipmentListViewModel equipmentListViewModel;
    private DeviceManageAdapter deviceManageAdapter;
    private int currentPage = 1;
    private boolean hasMore;
    private AlertDialog modifyDialog;
    private EditText modifyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_manager);
        initToolBar();
        initRefresh();
        loadData();
        initDialog();
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarDarkFont(true).statusBarColor(R.color.bottom_bg).init();
    }

    private void initDialog() {
        modifyDialog = new AlertDialog.Builder(this).
                setTitle(R.string.modify).
                setPositiveButton(R.string.ok, null).
                setNegativeButton(R.string.cancel, null).
                create();
        modifyEditText = new EditText(this);
        modifyDialog.setContentView(modifyEditText);

    }

    private void initToolBar() {
        setSupportActionBar(binding.toolBarContainer.findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.manage_equipment);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRefresh() {
        binding.refreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        equipmentListViewModel = ViewModelProviders.of(this).get(EquipmentListViewModel.class);
        equipmentListViewModel.listAllEquipment(currentPage);
        equipmentListViewModel.observeDeviceListResult(this, this);
        deviceManageAdapter = new DeviceManageAdapter(this);
        binding.recyclerView.setAdapter(deviceManageAdapter);
    }

    @Override
    public void onChanged(EquipmentListViewModel.EquipmentListResult equipmentListResult) {
        hasMore = equipmentListResult.isHasMore();
        XLog.dReport("onChanged ... " + binding.refreshLayout.getState() + "," + equipmentListResult.getDevices().size() + "," + hasMore);
        deviceManageAdapter.update(equipmentListResult.getDevices(), true);
        if (binding.refreshLayout.getState() == RefreshState.Loading) {
            binding.refreshLayout.finishLoadMore(100);
        }

        deviceManageAdapter.setOnItemLongClickListener(this);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        XLog.dReport("onLoadMore ...");
        if (hasMore) {
            currentPage++;
            equipmentListViewModel.listAllEquipment(currentPage);
        } else {
            binding.refreshLayout.setNoMoreData(true);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        XLog.dReport("onRefresh ...");
        binding.refreshLayout.finishRefresh(200);
    }

    @Override
    public boolean onItemLongClicked(EquipmentListBean.DataBean.Device device, String iemi, int id, String msg) {
        String hintError = "";
        int type = 1;
        switch (id) {
            default:
            case R.id.manageTag:
                XLog.dReport("onItemLongClicked tag ... " + iemi + "," + msg);
                modifyDialog.setTitle(getResources().getString(R.string.modify_tag) + ":" + msg);
                modifyEditText.setText("");
                modifyEditText.setHint(R.string.modify_tag_hint);
                hintError = getResources().getString(R.string.modify_tag_hint_error);
                type = 1;
                break;
            case R.id.manageNumber:
                XLog.dReport("onItemLongClicked number ... " + iemi + "," + msg);
                modifyDialog.setTitle(getResources().getString(R.string.modify_number) + ":" + msg);
                modifyEditText.setText("");
                modifyEditText.setHint(R.string.modify_number_hint);
                modifyEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                hintError = getResources().getString(R.string.modify_number_hint_error);
                type = 2;
                break;
            case R.id.manageFrq:
                XLog.dReport("onItemLongClicked frq ... " + iemi + "," + msg);
                modifyDialog.setTitle(getResources().getString(R.string.modify_frq) + ":" + msg);
                modifyEditText.setText("");
                modifyEditText.setHint(R.string.modify_frq_hint);
                modifyEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                hintError = getResources().getString(R.string.modify_frq_hint_error);
                type = 3;
                break;
        }
        modifyEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        modifyDialog.setView(modifyEditText);
        modifyDialog.show();
        int finalType = type;
        String finalHintError = hintError;
        modifyDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLog.dReport("onClick ... " + modifyEditText.getText().toString() + "," + finalType);
                if (TextUtils.isEmpty(modifyEditText.getText().toString())) {
                    //count not empty
                } else if (msg.equals(modifyEditText.getText().toString())) {
                    modifyEditText.setError(finalHintError);
                } else {
                    //modify
                    switch (finalType) {
                        case 1:
                            RetrofitServerImp.getInstance().updateDeviceTag(device, modifyEditText.getText().toString().trim(), new Server.DeviceUpdateCallBack() {
                                @Override
                                public void onDeviceUpdateSuccess(String msg) {
                                    XLog.dReport("tag onDeviceUpdateSuccess ... " + msg);
                                    ToastUtils.showLong(R.string.modify_success);
                                    device.setTag(modifyEditText.getText().toString().trim());
                                    deviceManageAdapter.updateItem(device);
                                }

                                @Override
                                public void onDeviceUpdateFailed(String msg) {
                                    XLog.dReport("tag onDeviceUpdateFailed ... " + msg);
                                }
                            });
                            break;
                        case 2:
                            RetrofitServerImp.getInstance().updateDevicePhone(device, modifyEditText.getText().toString().trim(), new Server.DeviceUpdateCallBack() {
                                @Override
                                public void onDeviceUpdateSuccess(String msg) {
                                    XLog.dReport("phone onDeviceUpdateSuccess ... " + msg);
                                    ToastUtils.showLong(R.string.modify_success);
                                    device.setBindNumber(modifyEditText.getText().toString().trim());
                                    deviceManageAdapter.updateItem(device);
                                }

                                @Override
                                public void onDeviceUpdateFailed(String msg) {
                                    XLog.dReport("phone onDeviceUpdateFailed ... " + msg);
                                }
                            });
                            break;
                        case 3:
                            RetrofitServerImp.getInstance().updateDeviceFrq(device, modifyEditText.getText().toString().trim(), new Server.DeviceUpdateCallBack() {
                                @Override
                                public void onDeviceUpdateSuccess(String msg) {
                                    XLog.dReport("frq onDeviceUpdateSuccess ... " + msg);
                                    ToastUtils.showLong(R.string.modify_success);
                                }

                                @Override
                                public void onDeviceUpdateFailed(String msg) {
                                    XLog.dReport("frq onDeviceUpdateFailed ... " + msg);
                                }
                            });
                            break;
                    }
                    modifyDialog.dismiss();
                }
            }
        });
        return true;
    }
}
