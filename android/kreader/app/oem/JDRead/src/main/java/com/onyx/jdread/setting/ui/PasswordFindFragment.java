package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.databinding.FragmentPasswordFindBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.setting.event.BackToDeviceConfigEvent;
import com.onyx.jdread.setting.model.PswFindModel;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by suicheng on 2018/2/8.
 */
public class PasswordFindFragment extends BaseFragment {

    private FragmentPasswordFindBinding passwordFindBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initBinding(inflater, container);
        return passwordFindBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    private void initBinding(LayoutInflater inflater, @Nullable ViewGroup container) {
        passwordFindBinding = FragmentPasswordFindBinding.inflate(inflater, container, false);
        PswFindModel pswFindModel = new PswFindModel(SettingBundle.getInstance().getEventBus());
        passwordFindBinding.passwordFindTitle.setTitleModel(pswFindModel.titleBarModel);
        passwordFindBinding.setPswFindModel(pswFindModel);
    }

    private void loadData() {
        passwordFindBinding.getPswFindModel().onRefreshClick();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToDeviceConfigEvent(BackToDeviceConfigEvent event) {
        viewEventCallBack.viewBack();
    }
}
