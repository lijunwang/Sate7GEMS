package com.sate7.wlj.developerreader.sate7gems.view.fragment;

import android.database.DatabaseUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceFragmentCompat;

import com.blankj.utilcode.util.SPUtils;
import com.sate7.wlj.developerreader.sate7gems.R;
import com.sate7.wlj.developerreader.sate7gems.databinding.FragmentMyselfBinding;
import com.sate7.wlj.developerreader.sate7gems.util.Constants;
import com.sate7.wlj.developerreader.sate7gems.util.XLog;

public class MySelfFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    /*@Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preferences);
    }*/

    private FragmentMyselfBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_myself, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.settingNotfSwitch.setOnCheckedChangeListener(this);
        binding.settingNotfSwitch.setChecked(allowNotification());
        binding.settingExit.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        XLog.dReport("onCheckedChanged ... " + isChecked);
        SPUtils.getInstance().put(Constants.SETTINGS_NOTIFICATION, true);
    }

    private boolean allowNotification() {
        return SPUtils.getInstance().getBoolean(Constants.SETTINGS_NOTIFICATION, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_exit:
                XLog.dReport("exit ... ");
                SPUtils.getInstance().put(Constants.NEED_LOGIN,true);
                getActivity().finish();
                break;
        }
    }
}
