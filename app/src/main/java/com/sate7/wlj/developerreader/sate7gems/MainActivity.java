package com.sate7.wlj.developerreader.sate7gems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.Utils;
import com.gyf.immersionbar.ImmersionBar;
import com.sate7.wlj.developerreader.sate7gems.databinding.ActivityMainBinding;
import com.sate7.wlj.developerreader.sate7gems.net.OkhttpTest;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.RetrofitServerImp;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.RetrofitTest;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.EquipmentAdapter;
import com.sate7.wlj.developerreader.sate7gems.view.fragment.FenceFragment;
import com.sate7.wlj.developerreader.sate7gems.view.fragment.LocationFragment;
import com.sate7.wlj.developerreader.sate7gems.view.fragment.MySelfFragment;
import com.sate7.wlj.developerreader.sate7gems.view.TabView;
import com.sate7.wlj.developerreader.sate7gems.view.fragment.WarningFragment;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.EquipmentListViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, DrawerLayout.DrawerListener, Observer<EquipmentListViewModel.EquipmentListResult>, OnRefreshLoadMoreListener {
    private final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<TabView> tabViews = new ArrayList<>();
    private LocationFragment locationFragment;
    private WarningFragment warningFragment;
    private FenceFragment fenceFragment;
    private MySelfFragment mySelfFragment;
    private ActionBarDrawerToggle toggle;
    private EquipmentAdapter equipmentAdapter;
    private EquipmentListViewModel equipmentListViewModel;
    private boolean hasMore = false;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setToolBar();
        initVies();
        initData();
        initRefresh();
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarDarkFont(true).statusBarColor(R.color.bottom_bg).init();
    }

    private void initRefresh() {
        binding.drawerLeft.refreshLayout.setOnRefreshLoadMoreListener(this);
    }

    private void initData() {
        equipmentAdapter = new EquipmentAdapter(this);
        binding.drawerLeft.recyclerView.setAdapter(equipmentAdapter);
        binding.drawerLeft.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        equipmentListViewModel = ViewModelProviders.of(this).get(EquipmentListViewModel.class);
        equipmentListViewModel.listAllEquipment(1);
        equipmentListViewModel.observeDeviceListResult(this, this);
    }

    private void initVies() {
        binding.bottom.tabLocation.setOnClickListener(this);
        binding.bottom.tabWarning.setOnClickListener(this);
        binding.bottom.tabFence.setOnClickListener(this);
        binding.bottom.tabMy.setOnClickListener(this);
        tabViews.add(binding.bottom.tabLocation);
        tabViews.add(binding.bottom.tabWarning);
        tabViews.add(binding.bottom.tabFence);
        tabViews.add(binding.bottom.tabMy);
        viewPager = binding.viewPager;

        updateCurrentTab(0);//default location selected;
        locationFragment = new LocationFragment();
        warningFragment = new WarningFragment();
        fenceFragment = new FenceFragment();
        mySelfFragment = new MySelfFragment();
        fragments.add(locationFragment);
        fragments.add(warningFragment);
        fragments.add(fenceFragment);
        fragments.add(mySelfFragment);
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                XLog.dReport("destroyItem ... " + position);
            }
        });
    }

    private void setToolBar() {
        setSupportActionBar(binding.toolBar);
        toggle = new ActionBarDrawerToggle(
                this, binding.drawer, binding.toolBar, 0, 0);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();
        binding.drawer.addDrawerListener(this);
        setTitle(R.string.bottom_location);
    }

    private void updateTitle(int index) {
        switch (index) {
            case 0:
                binding.toolBar.setTitle(R.string.bottom_location);
                toggle.setDrawerIndicatorEnabled(true);
                break;
            case 1:
                binding.toolBar.setTitle(R.string.bottom_warning);
                toggle.setDrawerIndicatorEnabled(true);
                break;
            case 2:
                binding.toolBar.setTitle(R.string.bottom_fence);
                toggle.setDrawerIndicatorEnabled(false);
                break;
            case 3:
                binding.toolBar.setTitle(R.string.bottom_my);
                toggle.setDrawerIndicatorEnabled(false);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        XLog.dReport("onClick ... ");
        switch (v.getId()) {
            case R.id.tabLocation:
                updateCurrentTab(0);
                viewPager.setCurrentItem(0, true);
                break;
            case R.id.tabWarning:
                updateCurrentTab(1);
                viewPager.setCurrentItem(1, true);
                break;
            case R.id.tabFence:
                updateCurrentTab(2);
                viewPager.setCurrentItem(2, true);
                break;
            case R.id.tabMy:
                updateCurrentTab(3);
                viewPager.setCurrentItem(3, true);
                break;
        }
    }

    private void updateCurrentTab(int index) {
        updateTitle(index);
        for (int i = 0; i < tabViews.size(); i++) {
            if (index == i) {
                tabViews.get(i).setXPercentage(1);
            } else {
                tabViews.get(i).setXPercentage(0);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        updateCurrentTab(position);
        XLog.dReport("onPageScrolled ... " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
//        XLog.dReport("onDrawerSlide ...");
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        XLog.dReport("onDrawerOpened ..." + equipmentAdapter.getSelectedDevices());
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        XLog.dReport("onDrawerClosed ..." + equipmentAdapter.getSelectedDevices());
        if (viewPager.getCurrentItem() == 0) {
            locationFragment.onDeviceSelected(equipmentAdapter.getSelectedDevices());
        } else if (viewPager.getCurrentItem() == 1) {
            warningFragment.onDeviceSelected(equipmentAdapter.getSelectedDevices());
        }
    }

    public ArrayList<EquipmentListBean.DataBean.Device> getDevicesHasLoaded() {
        return equipmentAdapter.getDevices();
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        XLog.dReport("onDrawerStateChanged ..." + newState);
    }

    //加载设备接口回调
    @Override
    public void onChanged(EquipmentListViewModel.EquipmentListResult equipmentListResult) {
        hasMore = equipmentListResult.isHasMore();
        XLog.dReport("MainActivity onChanged ... " + equipmentListResult.getDevices().size() + "," + hasMore + "," + binding.drawerLeft.refreshLayout.getState());
        if (binding.drawerLeft.refreshLayout.getState() == RefreshState.Loading) {
            equipmentAdapter.append(equipmentListResult.getDevices());
            binding.drawerLeft.refreshLayout.finishLoadMore();
        } else {//TODO update refresh
            equipmentAdapter.update(equipmentListResult.getDevices());
        }
    }

    public void openDrawer() {
        binding.drawer.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(Gravity.LEFT)) {
            binding.drawer.closeDrawer(Gravity.LEFT);
            return;
        }
        //Go Home do not close;
        boolean back = moveTaskToBack(true);
        XLog.dReport("moveTaskToBack ..." + back);
    }

    //加载更多回调
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        XLog.dReport("onLoadMore ... " + hasMore);
        if (hasMore) {
            currentPage++;
            equipmentListViewModel.listAllEquipment(currentPage);
        } else {
            binding.drawerLeft.refreshLayout.finishLoadMore(200);
            binding.drawerLeft.footer.setNoMoreData(true);
        }
    }

    //上拉加载回调
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        XLog.dReport("onRefresh ... ");
        binding.drawerLeft.refreshLayout.finishRefresh(500);
    }
}
