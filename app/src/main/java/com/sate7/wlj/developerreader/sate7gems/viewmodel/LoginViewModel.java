package com.sate7.wlj.developerreader.sate7gems.viewmodel;


import android.app.Application;
import android.content.res.Resources;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.net.Sate7GEMSServer;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

public class LoginViewModel extends AndroidViewModel {
    private String userName;
    private String password;
    private Resources resources;
    public LoginViewModel(@NonNull Application application) {
        super(application);
        resources = application.getResources();
    }

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
        XLog.dReport("name == " + userName + ",password == " + password);
        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)){
            mLoginResult.postValue(resources.getString(R.string.login_input_error));
            return;
        }
        Sate7GEMSServer.getInstance().login(userName, password, new Sate7GEMSServer.LoginCallBack() {
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
        });
    }
}
