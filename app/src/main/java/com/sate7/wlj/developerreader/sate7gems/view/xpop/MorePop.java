package com.sate7.wlj.developerreader.sate7gems.view.xpop;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.lxj.xpopup.core.HorizontalAttachPopupView;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.MoreExpendBinding;

public class MorePop extends HorizontalAttachPopupView {
    private MoreExpendBinding binding;
    private OnClickListener clickListener;
    private String item1;
    private String item2;

    public MorePop(@NonNull Context context, OnClickListener clickListener) {
        super(context);
        this.clickListener = clickListener;
    }

    public MorePop(String item1, String item2, @NonNull Context context, OnClickListener clickListener) {
        super(context);
        this.clickListener = clickListener;
        this.item1 = item1;
        this.item2 = item2;
    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.more_expend;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        if (!TextUtils.isEmpty(item1)) {
            TextView one = findViewById(R.id.morePopOpenDrawer);
            one.setText(item1);
        }
        if (!TextUtils.isEmpty(item2)) {
            TextView two = findViewById(R.id.morePopCloseTrack);
            two.setText(item2);
        }
        findViewById(R.id.morePopOpenDrawer).setOnClickListener(clickListener);
        findViewById(R.id.morePopCloseTrack).setOnClickListener(clickListener);
    }
}
