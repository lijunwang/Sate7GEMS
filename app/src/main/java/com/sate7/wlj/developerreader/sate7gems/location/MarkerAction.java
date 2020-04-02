package com.sate7.wlj.developerreader.sate7gems.location;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;

import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.net.Sate7GEMSServer;
import com.sate7.wlj.developerreader.sate7gems.net.bean.EquipmentListBean;
import com.sate7.wlj.developerreader.sate7gems.util.Constants;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

public class MarkerAction {
    public enum ACTION {
        UPLOAD,
        DIAL,
        SMS,
        TRACK
    }

    private Context context;
    private AlertDialog askBindNumberDialog;

    public MarkerAction(Context context) {
        this.context = context;
        EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        askBindNumberDialog = new AlertDialog.Builder(context).
                setTitle(R.string.bind_title).
                setMessage(R.string.bind_message).
                setView(editText).
                setPositiveButton(R.string.bind_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String number = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(number)) {
                            ToastUtils.showShort(R.string.bind_empty);
                        } else {
                            Sate7GEMSServer.getInstance().bindNumber(currentDevice, editText.getText().toString().trim(), new Sate7GEMSServer.BindCallback() {
                                @Override
                                public void onBindResult(String msg) {
                                    XLog.dReport("bind result ... " + msg);
                                    if (msg.contains("0")) {
                                        ToastUtils.showShort(R.string.bind_success);
                                    } else {
                                        ToastUtils.showShort(context.getResources().getString(R.string.bind_failed, msg));
                                    }
                                }
                            });
                        }
                    }
                }).
                setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).
                create();


    }

    private EquipmentListBean.DataBean.Device currentDevice;

    public void start(EquipmentListBean.DataBean.Device device, ACTION action) {
        if (device == null) {
            return;
        }
        currentDevice = device;
        switch (action) {
            case SMS:
                PhoneUtils.sendSms(device.getBindNumber(), "");
                break;
            case DIAL:
                if (device.getType().equals(Constants.DEVICE_DTYPE_M2M)) {
                    ToastUtils.showShort(R.string.cannot_call);
                    return;
                } else if (TextUtils.isEmpty(device.getBindNumber())) {
                    if (!askBindNumberDialog.isShowing()) {
                        askBindNumberDialog.show();
                    }
                } else {
                    PhoneUtils.dial(device.getBindNumber());
                }
                break;
            case TRACK:
                break;
            case UPLOAD:
                if (!device.getType().equals(Constants.DEVICE_DTYPE_M2M)) {
                    ToastUtils.showShort(R.string.nonsupport);
                } else if (TextUtils.isEmpty(device.getBindNumber())) {
                    askBindNumberDialog.show();
                } else {
                    PhoneUtils.sendSms(device.getBindNumber(), "send");
                }
                break;
            default:
                break;
        }
    }

    private boolean checkCondition(EquipmentListBean.DataBean.Device device) {
        if (TextUtils.isEmpty(device.getBindNumber())) {
            return false;
        }
        return false;
    }
}
