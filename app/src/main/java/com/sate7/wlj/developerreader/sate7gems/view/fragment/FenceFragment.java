package com.sate7.wlj.developerreader.sate7gems.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.sate7.wlj.developerreader.sate7gems.CreateFenceActivity;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.FragmentFenceBinding;
import com.sate7.wlj.developerreader.sate7gems.net.bean.FenceListBean;
import com.sate7.wlj.developerreader.sate7gems.view.MyItemDecoration;
import com.sate7.wlj.developerreader.sate7gems.view.adapter.FenceAdapter;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.FenceViewModel;

import java.util.ArrayList;

public class FenceFragment extends BaseFragment implements View.OnClickListener {

    private FenceViewModel fenceViewModel;
    private FragmentFenceBinding binding;
    private FenceAdapter fenceAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fenceViewModel = ViewModelProviders.of(this).get(FenceViewModel.class);
        fenceAdapter = new FenceAdapter();
    }

    @Override
    public void startLoadData() {
        super.startLoadData();
        fenceViewModel.startQueryFenceList();
        fenceViewModel.getFences().observe(this, new Observer<ArrayList<FenceListBean.DataBean.FenceBean>>() {
            @Override
            public void onChanged(ArrayList<FenceListBean.DataBean.FenceBean> fenceBeanArrayList) {
                fenceAdapter.update(fenceBeanArrayList);
            }
        });
    }

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.create:
//                fenceViewModel.createFence();
//                break;
//            case R.id.query:
//                fenceViewModel.startQueryFenceList();
//                break;
            case R.id.createFence:
                CreateFenceActivity.start(getActivity());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
