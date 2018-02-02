package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.onyx.jdread.databinding.FragmentBrightnessBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.model.BrightnessModel;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by hehai on 18-1-5.
 */

public class BrightnessFragment extends BaseFragment {

    private FragmentBrightnessBinding brightnessBinding;
    private BrightnessModel brightnessModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initData(inflater, container);
        initEvent();
        return brightnessBinding.getRoot();
    }

    private void initData(LayoutInflater inflater, @Nullable ViewGroup container) {
        brightnessBinding = FragmentBrightnessBinding.inflate(inflater, container, false);
        brightnessModel = new BrightnessModel();
        brightnessBinding.setBrightnessModel(brightnessModel);
    }

    private void initEvent() {
        brightnessBinding.ratingbarLightSettings.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    brightnessModel.setBrightness(ratingBar.getProgress());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.ensureRegister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Subscribe
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
