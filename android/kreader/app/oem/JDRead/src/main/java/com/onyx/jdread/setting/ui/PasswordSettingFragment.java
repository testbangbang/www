package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.databinding.FragmentPasswordSettingsBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.model.PswSettingModel;
import com.onyx.jdread.setting.model.SettingBundle;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hehai on 17-12-28.
 */

public class PasswordSettingFragment extends BaseFragment {

    private FragmentPasswordSettingsBinding passwordSettingBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initBinding(inflater, container);
        return passwordSettingBinding.getRoot();
    }

    private void initBinding(LayoutInflater inflater, @Nullable ViewGroup container) {
        passwordSettingBinding = FragmentPasswordSettingsBinding.inflate(inflater, container, false);
        PswSettingModel pswSettingModel = new PswSettingModel();
        passwordSettingBinding.passwordSettingsTitle.setTitleModel(pswSettingModel.titleBarModel);
        passwordSettingBinding.setPswSettingModel(pswSettingModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        SettingBundle.getInstance().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        SettingBundle.getInstance().getEventBus().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToDeviceConfigFragment(BackToDeviceConfigFragment event) {
        viewEventCallBack.viewBack();
    }
}
