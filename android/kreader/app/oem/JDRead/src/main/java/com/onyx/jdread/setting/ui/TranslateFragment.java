package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.databinding.FragmentTranslateBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.TranslateModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hehai on 18-1-15.
 */

public class TranslateFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentTranslateBinding binding = FragmentTranslateBinding.inflate(inflater, container, false);
        TranslateModel translateModel = new TranslateModel();
        binding.titleBar.setTitleModel(translateModel.titleBarModel);
        binding.setModel(translateModel);
        binding.translateResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!SettingBundle.getInstance().getEventBus().isRegistered(this)) {
            SettingBundle.getInstance().getEventBus().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SettingBundle.getInstance().getEventBus().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTranslateFragment(TranslateFragment event) {
        viewEventCallBack.viewBack();
    }
}
