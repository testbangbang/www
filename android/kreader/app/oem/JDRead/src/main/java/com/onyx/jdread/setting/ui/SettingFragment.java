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
import com.onyx.jdread.databinding.SettingBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.manager.ManagerActivityUtils;
import com.onyx.jdread.setting.adapter.SettingAdapter;
import com.onyx.jdread.setting.event.FeedbackEvent;
import com.onyx.jdread.setting.event.IntensityEvent;
import com.onyx.jdread.setting.event.LaboratoryEvent;
import com.onyx.jdread.setting.event.RefreshEvent;
import com.onyx.jdread.setting.event.ScreenEvent;
import com.onyx.jdread.setting.event.ToDeviceConfigEvent;
import com.onyx.jdread.setting.event.WireLessEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.SettingDataModel;
import com.onyx.jdread.util.TimeUtils;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class SettingFragment extends BaseFragment {
    private SettingBinding binding;
    private SettingAdapter settingAdapter;
    private SettingDataModel settingDataModel;
    private long lastPressTime;
    private long resetPressCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (SettingBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        initView();
        initData();
        initListener();
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

    private void initData() {
        settingDataModel = SettingBundle.getInstance().getSettingDataModel();
        if (settingAdapter != null) {
            settingAdapter.setEvent(settingDataModel.getItemEvent());
            settingAdapter.setData(settingDataModel.getItemData());
        }
        binding.setSettingModel(settingDataModel);
    }

    private void initView() {
        binding.settingRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration dividerItemDecoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.settingRecycler.addItemDecoration(dividerItemDecoration);
        settingAdapter = new SettingAdapter(SettingBundle.getInstance().getEventBus());
        binding.settingRecycler.setAdapter(settingAdapter);
    }

    private void initListener() {
        binding.settingStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrRemoveTest(Constants.START_PRODUCTION_TEST_PRESS_COUNT);
            }
        });

        binding.settingRemoveTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrRemoveTest(Constants.REMOVE_PRODUCTION_TEST_PRESS_COUNT);
            }
        });
    }

    private void startOrRemoveTest(int count) {
        if (TimeUtils.getCurrentTimeInLong() > lastPressTime + Constants.RESET_PRESS_TIMEOUT) {
            lastPressTime = TimeUtils.getCurrentTimeInLong();
            resetPressCount = 0;
        }
        resetPressCount++;
        if (resetPressCount >= count) {
            resetPressCount = 0;
            lastPressTime = 0;
            if (count == Constants.START_PRODUCTION_TEST_PRESS_COUNT) {
                ManagerActivityUtils.startProductionTest(getContext());
            } else {
                // TODO: 2018/1/15 invoke remove method
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWireLessEvent(WireLessEvent event) {
        viewEventCallBack.gotoView(WifiFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIntensityEvent(IntensityEvent event) {
        viewEventCallBack.gotoView(BrightnessFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        viewEventCallBack.gotoView(RefreshFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScreenEvent(ScreenEvent event) {
        viewEventCallBack.gotoView(LockScreenFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLaboratoryEvent(LaboratoryEvent event) {
        viewEventCallBack.gotoView(LaboratoryFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFeedbackEvent(FeedbackEvent event) {
        viewEventCallBack.gotoView(HelpFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToDeviceConfigEvent(ToDeviceConfigEvent event) {
        viewEventCallBack.gotoView(DeviceConfigFragment.class.getName());
    }
}
