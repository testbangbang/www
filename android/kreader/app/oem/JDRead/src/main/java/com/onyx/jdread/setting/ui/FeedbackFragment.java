package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentFeedbackBinding;
import com.onyx.jdread.main.event.TitleBarRightTitleEvent;
import com.onyx.jdread.setting.event.BackToHelpFragmentEvent;
import com.onyx.jdread.setting.model.FeedbackModel;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.util.Utils;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showInputKeyboard();
    }

    private void showInputKeyboard() {
        feedbackBinding.feedbackDescEdit.requestFocus();
        InputMethodUtils.showForcedInputKeyboard(getContext(), feedbackBinding.feedbackDescEdit);
    }

    private void loadData() {
        FeedbackModel feedbackModel = new FeedbackModel();
        feedbackBinding.feedbackTitle.setTitleModel(feedbackModel.titleBarModel);
        feedbackBinding.setFeedbackModel(feedbackModel);
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
    public void onBackToHelpFragmentEvent(BackToHelpFragmentEvent event) {
        InputMethodUtils.hideInputKeyboard(getContext());
        viewEventCallBack.viewBack();
    }

    @Subscribe
    public void onTitleBarRightTitleEvent(TitleBarRightTitleEvent event) {
        InputMethodUtils.hideInputKeyboard(getContext());
        viewEventCallBack.gotoView(FeedbackRecordFragment.class.getName());
    }
}
