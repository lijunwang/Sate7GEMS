package com.sate7.wlj.developerreader.sate7gems.view.xpop;

import android.content.Context;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.BottomPopupView;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.util.Calendar;

public class DatePickPop extends BottomPopupView implements View.OnClickListener {
    private DatePicker datePicker;
    private DatePicker.OnDateChangedListener listener;

    public DatePickPop(@NonNull Context context, DatePicker.OnDateChangedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.fence_create_date_picker;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        Calendar calendar = Calendar.getInstance();
        datePicker = findViewById(R.id.datePicker);
        XLog.dReport("date month = " + calendar.get(Calendar.MONTH));
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), listener);
    }


    @Override
    public void onClick(View v) {

    }

}
