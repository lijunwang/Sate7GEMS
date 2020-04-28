package com.sate7.wlj.developerreader.sate7gems.viewmodel;


import android.app.Application;
import android.content.res.Resources;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.Sate7EGMSApplication;
import com.sate7.wlj.developerreader.sate7gems.net.retrofit.Server;
import com.sate7.wlj.developerreader.sate7gems.util.Constants;

public class LoginViewModel extends GEMSViewModel {
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private MutableLiveData<String> mLoginResult = new MutableLiveData<>();

    public MutableLiveData<String> getLoginResult() {
        return mLoginResult;
    }


    public void login() {
        log("login ..." + userName + "," + password);
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            mLoginResult.postValue(Utils.getApp().getResources().getString(R.string.login_input_error));
            return;
        }

        server.login(userName, password, new Server.LoginCallBack() {
            @Override
            public void onLoginSuccess(String token) {
                log("login success ... " + token);
                mLoginResult.postValue(Utils.getApp().getResources().getString(R.string.login_success));
            }

            @Override
            public void onLoginFailed(String reason) {
                log("login failed ... " + reason);
                mLoginResult.postValue(Utils.getApp().getResources().getString(R.string.login_failed) + ":" + reason);
            }
        });

        /*OkHttpServerImp.getInstance().login(userName, password, new OkHttpServerImp.LoginCallBack() {
            @Override
            public void loginSuccess(String token) {
                XLog.dReport("loginSuccess ... " + token);
                mLoginResult.postValue(resources.getString(R.string.login_success));
            }

            @Override
            public void loginFailed(String msg) {
                XLog.dReport("loginFailed ... " + msg);
                mLoginResult.postValue(resources.getString(R.string.login_failed));
            }
        });*/
    }
}
