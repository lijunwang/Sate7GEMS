package com.sate7.wlj.developerreader.sate7gems.view.xpop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ToastUtils;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.TrackShowDateBinding;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StartEndDateDialog implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    public interface StartEndTimeSelectedListener {
        void onDateSelected(String start, String end);
    }

    private Context context;
    private StartEndTimeSelectedListener listener;
    private TrackShowDateBinding binding;
    private AlertDialog alertDialog;
    private Calendar calendarStart;
    private Calendar calendarEnd;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DatePicker.OnDateChangedListener dateChangedListenerEnd = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            XLog.dReport("onDateChanged start... " + year + "," + monthOfYear + "," + dayOfMonth);
            calendarEnd.set(year, monthOfYear, dayOfMonth);
            binding.trackEnd.setText(context.getResources().getString(R.string.track_end_value, simpleDateFormat.format(calendarEnd.getTime())));
        }
    };
    private DatePicker.OnDateChangedListener dateChangedListenerStart = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            XLog.dReport("onDateChanged end ... " + year + "," + monthOfYear + "," + dayOfMonth);
            calendarStart.set(year, monthOfYear, dayOfMonth);
            binding.trackStart.setText(context.getResources().getString(R.string.track_start_value, simpleDateFormat.format(calendarStart.getTime())));
        }
    };

    public StartEndDateDialog(Context context, StartEndTimeSelectedListener listener) {
        this.context = context;
        this.listener = listener;
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.track_show_date, null, false);
        binding.trackStart.setOnCheckedChangeListener(this);
        binding.trackEnd.setOnCheckedChangeListener(this);
        calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.MONTH, -1);
        calendarEnd = Calendar.getInstance();
        alertDialog = new AlertDialog.Builder(context).
//                setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).
//                setPositiveButton(R.string.ok, null).
        setView(binding.getRoot()).
                        create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        binding.trackStart.setText(context.getResources().getString(R.string.track_start_value, simpleDateFormat.format(calendarStart.getTime())));
        binding.trackEnd.setText(context.getResources().getString(R.string.track_end_value, simpleDateFormat.format(calendarEnd.getTime())));
        binding.trackTimePick.init(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH), dateChangedListenerStart);
    }

    private boolean checkOk() {
        if (calendarEnd.getTimeInMillis() > calendarStart.getTimeInMillis()) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                if (checkOk()) {
                    listener.onDateSelected(simpleDateFormat.format(calendarStart.getTime()), simpleDateFormat.format(calendarEnd.getTime()));
                    alertDialog.dismiss();
                } else {
                    ToastUtils.showShort(R.string.track_date_error);
                }
                break;
            case R.id.cancel:
                alertDialog.dismiss();
                break;
        }
    }

    public void show() {
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
        binding.ok.setOnClickListener(this);
        binding.cancel.setOnClickListener(this);
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (checkOk()) {
//                    listener.onDateSelected(simpleDateFormat.format(calendarStart.getTime()), simpleDateFormat.format(calendarEnd.getTime()));
//                    alertDialog.dismiss();
//                } else {
//                    ToastUtils.showShort(R.string.track_date_error);
//                }
//                return;
//            }
//        });
//        ViewGroup viewGroup = (ViewGroup) alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).getParent();
//        viewGroup.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
//        debugLayout(binding.getRoot());
//        changeHeaderColor();
    }

    private void changeHeaderColor() {
        ViewGroup viewGroup = (ViewGroup) binding.trackTimePick.getChildAt(0);
        ViewGroup viewGroup2 = (ViewGroup) binding.trackTimePick.getChildAt(1);
        ViewGroup child = (ViewGroup) viewGroup.getChildAt(0);
        Log.d("debugLayout", "changeHeaderColor :" + viewGroup.getWidth());
        Log.d("debugLayout", "changeHeaderColor child :" + child.getWidth());
        Log.d("debugLayout", "changeHeaderColor viewGroup2 :" + child.getWidth());

        ViewGroup.LayoutParams params = child.getLayoutParams();
        params.width = 1500;
        child.setLayoutParams(params);
        child.requestLayout();
//        child.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
    }

    private void debugLayout(View view) {
        if (view instanceof ViewGroup) {
            int count = ((ViewGroup) view).getChildCount();
            Log.d("debugLayout", "Layout == " + view.getClass().getSimpleName() + " contains " + count + " children ");
            for (int i = 0; i < count; i++) {
                View child = ((ViewGroup) view).getChildAt(i);
                if (child instanceof ViewGroup) {
                    debugLayout(child);
                } else {
                    Log.d("debugLayout", "child[ " + i + "] ==" + child.getClass().getSimpleName());
                    if (child instanceof AppCompatTextView) {
                        Log.d("debugLayout", "text 22BB == " + ((TextView) child).getText());
                    }
                }
            }
        } else {
            Log.d("debugLayout", "child name == " + view.getClass().getSimpleName());
            if (view instanceof TextView) {
                Log.d("debugLayout", "text 33 aa == " + ((TextView) view).getText());
            }
        }
    }

    public void dismiss() {
        alertDialog.dismiss();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.trackStart:
                if (isChecked) {
                    binding.trackTimePick.init(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH), dateChangedListenerStart);
                }
                break;
            case R.id.trackEnd:
                if (isChecked) {
                    binding.trackTimePick.init(calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH), dateChangedListenerEnd);
                }
                break;
            default:
                break;
        }
    }
}
