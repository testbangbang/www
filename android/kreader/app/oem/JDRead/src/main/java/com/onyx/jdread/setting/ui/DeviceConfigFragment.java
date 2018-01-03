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
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.databinding.DeviceConfigBinding;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.setting.adapter.DeviceConfigAdapter;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.event.DeviceInformationEvent;
import com.onyx.jdread.setting.event.PasswordEvent;
import com.onyx.jdread.setting.event.ReadToolEvent;
import com.onyx.jdread.setting.event.ScreenSaverEvent;
import com.onyx.jdread.setting.event.SystemUpdateEvent;
import com.onyx.jdread.setting.model.DeviceConfigData;
import com.onyx.jdread.setting.model.DeviceConfigModel;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.SettingTitleModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2017/12/22.
 */

public class DeviceConfigFragment extends BaseFragment {
    private DeviceConfigBinding binding;
    private DeviceConfigAdapter deviceConfigAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (DeviceConfigBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_device_config, container, false);
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
        TitleBarModel titleModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.device_config));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.deviceConfigTitleBar.setTitleModel(titleModel);
        DeviceConfigModel deviceConfigModel = SettingBundle.getInstance().getDeviceConfigModel();
        if (deviceConfigAdapter != null) {
            deviceConfigAdapter.setData(deviceConfigModel.getDeviceConfigDataList(), deviceConfigModel.getConfigEvents());
        }
    }

    private void initView() {
        binding.deviceConfigRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration dividerItemDecoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.deviceConfigRecycler.addItemDecoration(dividerItemDecoration);
        deviceConfigAdapter = new DeviceConfigAdapter(SettingBundle.getInstance().getEventBus());
        binding.deviceConfigRecycler.setAdapter(deviceConfigAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScreenSaverEvent(ScreenSaverEvent event) {
        viewEventCallBack.gotoView(ScreensaversFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPasswordEvent(PasswordEvent event) {
        viewEventCallBack.gotoView(PasswordSettingFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadToolEvent(ReadToolEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInformationEvent(DeviceInformationEvent event) {
        viewEventCallBack.gotoView(DeviceInformationFragment.class.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemUpdateEvent(SystemUpdateEvent event) {
        viewEventCallBack.gotoView(SystemUpdateFragment.class.getName());
    }

    @Subscribe
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event){
        viewEventCallBack.viewBack();
    }
}
