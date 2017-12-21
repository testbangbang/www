package com.onyx.jdread.setting.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.SettingBinding;
import com.onyx.jdread.setting.adapter.SettingAdapter;
import com.onyx.jdread.setting.event.FeedbackEvent;
import com.onyx.jdread.setting.event.IntensityEvent;
import com.onyx.jdread.setting.event.LaboratoryEvent;
import com.onyx.jdread.setting.event.RefreshEvent;
import com.onyx.jdread.setting.event.ScreenEvent;
import com.onyx.jdread.setting.event.WireLessEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.SettingDataModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class SettingFragment extends BaseFragment {
    private SettingBinding binding;
    private SettingAdapter settingAdapter;
    private SettingDataModel settingDataModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (SettingBinding)DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        initView();
        initData();
        return binding.getRoot();
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

    private void initData() {
        settingDataModel = SettingBundle.getInstance().getSettingDataModel();
        if (settingAdapter != null) {
            settingAdapter.setEvent(settingDataModel.getItemEvent());
            settingAdapter.setData(settingDataModel.getItemData());
        }
    }

    private void initView() {
        binding.settingRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration dividerItemDecoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.settingRecycler.addItemDecoration(dividerItemDecoration);
        settingAdapter = new SettingAdapter(SettingBundle.getInstance().getEventBus());
        binding.settingRecycler.setAdapter(settingAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWireLessEvent(WireLessEvent event) {
        getViewEventCallBack().gotoView(WifiFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIntensityEvent(IntensityEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScreenEvent(ScreenEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLaboratoryEvent(LaboratoryEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFeedbackEvent(FeedbackEvent event) {

    }
}
