package com.sate7.wlj.developerreader.sate7gems.util;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.sate7.wlj.developerreader.sate7gems.R;

public class BindAdapter {
    private static final String TAG = "BindAdapter";

    @BindingAdapter({"resId", "value"})
    public static void setFormattedText(TextView textView, int resId, String value) {
        XLog.d(TAG, "setFormattedText ...");
        if (resId == 0) return;
//        textView.setText(String.format(format, textView.getResources().getString(resId)));
        textView.setText(textView.getResources().getString(resId, value));
    }
}
