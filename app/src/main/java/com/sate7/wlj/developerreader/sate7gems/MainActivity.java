package com.sate7.wlj.developerreader.sate7gems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.sate7.wlj.developerreader.sate7gems.databinding.ActivityMainBinding;
import com.sate7.wlj.developerreader.sate7gems.net.OkhttpTest;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.RetrofitTest;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.ServerImp;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;
import com.sate7.wlj.developerreader.sate7gems.view.fragment.FenceFragment;
import com.sate7.wlj.developerreader.sate7gems.view.fragment.LocationFragment;
import com.sate7.wlj.developerreader.sate7gems.view.fragment.MySelfFragment;
import com.sate7.wlj.developerreader.sate7gems.view.TabView;
import com.sate7.wlj.developerreader.sate7gems.view.fragment.WarningFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<TabView> tabViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initVies();

        testLogin();
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
        fragments.add(new LocationFragment());
        fragments.add(new WarningFragment());
        fragments.add(new FenceFragment());
        fragments.add(new MySelfFragment());
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
//                retrofitTest.testDetail();
//                retrofitTest.testByDate();
//                retrofitTest.testCreateFence();
//                okhttpTest.testLogin();
//                okhttpTest.testQueryDevices();
                okhttpTest.testQueryWarningInfo();
                break;
        }
    }

    private RetrofitTest retrofitTest = new RetrofitTest();
    private OkhttpTest okhttpTest = new OkhttpTest();

    private void updateCurrentTab(int index) {
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


    private void testLogin() {
        ServerImp.getInstance().login("qx_admin", "qx", new Server.LoginCallBack() {
            @Override
            public void onLoginSuccess(String token) {
                Sate7EGMSApplication.setToken(token);
//                ServerImp.getInstance().queryAllDevices(1,null);
            }

            @Override
            public void onLoginFailed(String reason) {

            }
        });
    }

}
