package com.sate7.wlj.developerreader.sate7gems.view.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.sate7.wlj.developerreader.sate7gems.CreateFenceActivity;
import com.sate7.wlj.developerreader.sate7gems.MainActivity;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.FenceDevicesBinding;
import com.sate7.wlj.developerreader.sate7gems.databinding.FragmentFenceBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.util.Constants;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.MyItemDecoration;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.FenceAdapter;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.FenceDeviceAdapter;
import com.sate7.wlj.developerreader.sate7gems.view.xpop.MorePop;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.DetailViewMode;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.FenceViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;

import static com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server.PageSizeForFence;

public class FenceFragment extends BaseFragment implements View.OnClickListener, OnRefreshLoadMoreListener {

    private FenceViewModel fenceViewModel;
    private FragmentFenceBinding binding;
    private FenceAdapter fenceAdapter;
    private DetailViewMode detailViewMode;
    private FenceDeviceAdapter fenceDeviceAdapter;
    private AlertDialog fenceDeviceDialog;
    private FenceDevicesBinding fenceDevicesBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fenceViewModel = ViewModelProviders.of(this).get(FenceViewModel.class);
        detailViewMode = ViewModelProviders.of(this).get(DetailViewMode.class);
        fenceAdapter = new FenceAdapter(this);
    }

    private boolean hasMore = true;
    private int currentPageNumber = 1;
    private int lastPageCount = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fence, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.createFence.setOnClickListener(this);
        binding.fenceRecyclerView.setAdapter(fenceAdapter);
        binding.fenceRecyclerView.addItemDecoration(new MyItemDecoration());
        binding.refreshLayout.setOnRefreshLoadMoreListener(this);
        fenceViewModel.startQueryFenceList(currentPageNumber);
        fenceViewModel.getFences().observe(this, new Observer<FenceViewModel.FenceListData>() {
            @Override
            public void onChanged(FenceViewModel.FenceListData fenceListData) {
                hasMore = fenceListData.isHasMore();
                XLog.dReport("create fence onChanged ..." + hasMore + "," + binding.refreshLayout.getState() + "," + fenceListData.getFenceBeans().size());
                if (binding.refreshLayout.getState() == RefreshState.Loading) {
                    fenceAdapter.append(fenceListData.getFenceBeans());
                    binding.refreshLayout.finishLoadMore(200);
                    if (!hasMore) {
                        lastPageCount = fenceListData.getFenceBeans().size();
                    }
                } else if (binding.refreshLayout.getState() == RefreshState.ReleaseToLoad) {//创建成功后手动刷新
                    XLog.dReport("create fence activity ok ...");
                    fenceAdapter.appendOne(fenceListData.getFenceBeans().get(fenceListData.getFenceBeans().size() - 1));
                } else {
                    binding.refreshLayout.finishRefresh(200);
                    fenceAdapter.update(fenceListData.getFenceBeans());
                }
            }
        });

        detailViewMode.getLatestLocation().observe(this, new Observer<EquipmentListBean.DataBean.Device>() {
            @Override
            public void onChanged(EquipmentListBean.DataBean.Device device) {
                fenceDeviceAdapter.append(device);
            }
        });
    }

    private BasePopupView createFencePop;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.morePopOpenDrawer:
                XLog.dReport("morePopOpenDrawer ... ");
                createFencePop.dismiss();
                startCreateFenceAct(Constants.MONITOR_DATA);
                break;
            case R.id.morePopCloseTrack:
                XLog.dReport("morePopCloseTrack ... ");
                createFencePop.dismiss();
                startCreateFenceAct(Constants.MONITOR_GEOFENCE);
                break;

            case R.id.createFence:
                /*if (createFencePop == null) {
                    createFencePop = new XPopup.Builder(getContext()).
                            offsetX(-20).
                            atView(binding.createFence).
                            asCustom(new MorePop(getResources().getString(R.string.monitor_data),
                                    getResources().getString(R.string.monitor_fence),
                                    getContext(), this));
                }
                createFencePop.show();*/
                startCreateFenceAct(Constants.MONITOR_GEOFENCE);
                break;
        }
    }

    public static final int REQUEST_CREATE = 10000;

    private void startCreateFenceAct(int type) {
        Intent intent = new Intent(getContext(), CreateFenceActivity.class);
        intent.putExtra("type", type);
        startActivityForResult(intent, REQUEST_CREATE);
    }

    public void startCreateFenceAct(ArrayList<Double> geoList, FenceListBean.DataBean.FenceBean fenceBean) {
        XLog.dReport("create fence start ... " + fenceBean);
        Intent intent = new Intent(getContext(), CreateFenceActivity.class);
        intent.putExtra("FenceBean", fenceBean);
        if (geoList != null && geoList.size() > 0) {
            double[] geo = new double[geoList.size()];
            for (int i = 0; i < geoList.size(); i++) {
                geo[i] = geoList.get(i);
            }
            intent.putExtra("geo", geo);
        }
        startActivityForResult(intent, REQUEST_CREATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE && resultCode == Activity.RESULT_OK) {
            XLog.dReport("create fence onActivityResult ... " + hasMore + "," + currentPageNumber);
            //create success;
            hasMore = true;
            binding.footer.setNoMoreData(false);
            if (lastPageCount >= PageSizeForFence) {
                currentPageNumber++;
            }
            fenceViewModel.startQueryFenceList(currentPageNumber);
            binding.refreshLayout.autoLoadMore();
        }
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        XLog.dRefresh("FenceFragment onBGARefreshLayoutBeginLoadingMore ..." + hasMore + "," + currentPageNumber);
        if (hasMore) {
            currentPageNumber++;
            fenceViewModel.startQueryFenceList(currentPageNumber);
        } else {
            binding.footer.setNoMoreData(true);
            binding.refreshLayout.finishLoadMore(100);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        fenceViewModel.startQueryFenceList(1);
    }

    public void showDevicesInFence(String fenceName, ArrayList<String> imeis) {
        MainActivity mainActivity = (MainActivity) getActivity();
        ArrayList<EquipmentListBean.DataBean.Device> loadedDevice = mainActivity.getDevicesHasLoaded();
        ArrayList<EquipmentListBean.DataBean.Device> showDevice = new ArrayList<>();
        for (String imei : imeis) {
            for (EquipmentListBean.DataBean.Device device : loadedDevice) {
                if (imei.equals(device.getImei())) {
                    showDevice.add(device);
                    break;
                }
            }
            //沒有加载的设备，需要去查询
            detailViewMode.queryLatestLocationInfo(imei);
        }

        if (fenceDeviceDialog == null) {
            fenceDevicesBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fence_devices, null, false);
            fenceDeviceAdapter = new FenceDeviceAdapter(getContext());
            fenceDevicesBinding.rv.setAdapter(fenceDeviceAdapter);
            fenceDevicesBinding.rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            fenceDevicesBinding.fence.setText(fenceName);
            fenceDeviceDialog = new AlertDialog.Builder(getContext()).
                    setView(fenceDevicesBinding.getRoot()).create();
        }

        fenceDevicesBinding.fence.setText(fenceName);
        fenceDeviceDialog.show();
        fenceDeviceAdapter.clean();
        fenceDeviceAdapter.update(showDevice, false);
        XLog.dReport("showDevicesInFence iemi... " + imeis);
        XLog.dReport("showDevicesInFence device ... " + showDevice);
    }
}
