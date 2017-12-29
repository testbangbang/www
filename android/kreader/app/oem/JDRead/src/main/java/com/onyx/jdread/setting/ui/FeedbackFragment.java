package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentFeedbackBinding;
import com.onyx.jdread.setting.event.BackToHelpFragmentEvent;
import com.onyx.jdread.setting.model.FeedbackModel;
import com.onyx.jdread.setting.model.SettingBundle;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hehai on 17-12-28.
 */

public class FeedbackFragment extends BaseFragment {

    private FragmentFeedbackBinding feedbackBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        feedbackBinding = FragmentFeedbackBinding.inflate(inflater, container, false);
        loadData();
        return feedbackBinding.getRoot();
    }

    private void loadData() {
        FeedbackModel feedbackModel = new FeedbackModel();
        feedbackBinding.feedbackTitle.setTitleModel(feedbackModel.titleBarModel);
        feedbackBinding.setFeedbackModel(feedbackModel);
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
    public void onBackToHelpFragmentEvent(BackToHelpFragmentEvent event) {
        viewEventCallBack.gotoView(HelpFragment.class.getName());
    }
}
