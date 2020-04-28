package com.sate7.wlj.developerreader.sate7gems;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.blankj.utilcode.util.SPUtils;
import com.sate7.wlj.developerreader.sate7gems.databinding.ActivityLoginBinding;
import com.sate7.wlj.developerreader.sate7gems.util.Constants;
import com.sate7.wlj.developerreader.sate7gems.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SPUtils.getInstance().getBoolean(Constants.NEED_LOGIN, true)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        String name = SPUtils.getInstance().getString(Constants.LOGIN_USER_NAME, "");
        if (!TextUtils.isEmpty(name)) {
            loginViewModel.setUserName(name);
        }
        String pwd = SPUtils.getInstance().getString(Constants.LOGIN_PWD, "");
        boolean remember = SPUtils.getInstance().getBoolean(Constants.REMEMBER, false);
        if (remember) {
            binding.loginRemember.setChecked(true);
        }
        if (remember && !TextUtils.isEmpty(pwd)) {
            loginViewModel.setPassword(pwd);
        }
//        loginViewModel.setUserName("qx_admin");
//        loginViewModel.setPassword("qx");
        binding.setLoginViewMode(loginViewModel);
        loginViewModel.getLoginResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                if (getResources().getString(R.string.login_success).equals(s)) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    SPUtils.getInstance().put(Constants.NEED_LOGIN, false);
                    SPUtils.getInstance().put(Constants.REMEMBER, binding.loginRemember.isChecked());
                }
            }
        });
    }
}
