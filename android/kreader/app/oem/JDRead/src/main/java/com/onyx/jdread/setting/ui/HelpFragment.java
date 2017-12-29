package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentHelpBinding;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.event.ContactUsEvent;
import com.onyx.jdread.setting.event.FeedbackEvent;
import com.onyx.jdread.setting.event.ManualEvent;
import com.onyx.jdread.setting.model.HelpModel;
import com.onyx.jdread.setting.model.SettingBundle;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hehai on 17-12-28.
 */

public class HelpFragment extends BaseFragment {

    private FragmentHelpBinding helpBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        helpBinding = FragmentHelpBinding.inflate(inflater, container, false);
        HelpModel helpModel = new HelpModel();
        helpBinding.helpTitle.setTitleModel(helpModel.titleBarModel);
        helpBinding.setHelpModel(helpModel);
        return helpBinding.getRoot();
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
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.gotoView(SettingFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFeedbackEvent(FeedbackEvent event) {
        viewEventCallBack.gotoView(FeedbackFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onManualEvent(ManualEvent event) {
        viewEventCallBack.gotoView(ManualFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onContactUsEvent(ContactUsEvent event) {
        viewEventCallBack.gotoView(ContactUsFragment.class.getName());
    }
}
