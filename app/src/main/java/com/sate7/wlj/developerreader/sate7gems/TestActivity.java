package com.sate7.wlj.developerreader.sate7gems;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.sate7.wlj.developerreader.sate7gems.databinding.ActivityTestBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.StateRecyclerView;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.EquipmentAdapter;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.EquipmentListViewModel;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.FenceViewModel;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.WarningViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

public class TestActivity extends AppCompatActivity implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {
    private final String TAG = "TestActivity";
    private ActivityTestBinding binding;
    private FenceViewModel fenceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        initRefreshLayout();
        initViews();

        fenceViewModel = ViewModelProviders.of(this).get(FenceViewModel.class);
        fenceViewModel.getFences().observe(this, new Observer<ArrayList<FenceListBean.DataBean.FenceBean>>() {
            @Override
            public void onChanged(ArrayList<FenceListBean.DataBean.FenceBean> fenceBeans) {
                Log.d(TAG, "fenceOnChanged ... " + fenceBeans.size());
            }
        });
    }

    private void initViews() {
    }

    private void initRefreshLayout() {
        // 为BGARefreshLayout 设置代理
        binding.refreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        // 设置下拉刷新和上拉加载更多的风格
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(this, true);
        stickinessRefreshViewHolder.setStickinessColor(R.color.colorPrimary);
        stickinessRefreshViewHolder.setRotateImage(R.drawable.bga_refresh_stickiness);
        binding.refreshLayout.setRefreshViewHolder(stickinessRefreshViewHolder);
        // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项  -------------START
        // 设置正在加载更多时不显示加载更多控件
        binding.refreshLayout.setIsShowLoadingMoreView(true);
        // 设置正在加载更多时的文本
        stickinessRefreshViewHolder.setLoadingMoreText("loading ...");
        // 设置整个加载更多控件的背景颜色资源 id
        /*refreshViewHolder.setLoadMoreBackgroundColorRes(loadMoreBackgroundColorRes);
        // 设置整个加载更多控件的背景 drawable 资源 id
        refreshViewHolder.setLoadMoreBackgroundDrawableRes(loadMoreBackgroundDrawableRes);
        // 设置下拉刷新控件的背景颜色资源 id
        refreshViewHolder.setRefreshViewBackgroundColorRes(refreshViewBackgroundColorRes);
        // 设置下拉刷新控件的背景 drawable 资源 id
        refreshViewHolder.setRefreshViewBackgroundDrawableRes(refreshViewBackgroundDrawableRes);
        // 设置自定义头部视图（也可以不用设置）     参数1：自定义头部视图（例如广告位）， 参数2：上拉加载更多是否可用
        binding.refreshLayout.setCustomHeaderView(mBanner, false);*/
        // 可选配置  -------------END
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        Log.d(TAG, "onBGARefreshLayoutBeginRefreshing ...");
        fenceViewModel.startQueryFenceList();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        Log.d(TAG, "onBGARefreshLayoutBeginLoadingMore ...");
        return true;
    }
}
