package com.onyx.jdread.setting.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FeedbackRecordBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.adapter.FeedbackRecordAdapter;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2018/1/18.
 */

public class FeedbackRecordFragment extends BaseFragment {
    private FeedbackRecordBinding binding;
    private FeedbackRecordAdapter feedbackRecordAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (FeedbackRecordBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_feedback_record, container, false);
        initView();
        initData();
        return binding.getRoot();
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

    private void initView() {
        binding.feedbackRecordRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider dividerItemDecoration = new DashLineItemDivider();
        binding.feedbackRecordRecycler.addItemDecoration(dividerItemDecoration);
        feedbackRecordAdapter = new FeedbackRecordAdapter();
        binding.feedbackRecordRecycler.setAdapter(feedbackRecordAdapter);
    }

    private void initData() {
        TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
        titleBarModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.feedback_history));
        titleBarModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.feedbackRecordTitle.setTitleModel(titleBarModel);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
