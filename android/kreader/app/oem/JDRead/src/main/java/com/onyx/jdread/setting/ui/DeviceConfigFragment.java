package com.onyx.jdread.setting.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.DeviceConfigBinding;
import com.onyx.jdread.setting.adapter.DeviceConfigAdapter;
import com.onyx.jdread.setting.event.DeviceInformationEvent;
import com.onyx.jdread.setting.event.PasswordEvent;
import com.onyx.jdread.setting.event.ReadToolEvent;
import com.onyx.jdread.setting.event.ScreenSaverEvent;
import com.onyx.jdread.setting.event.SystemUpdateEvent;
import com.onyx.jdread.setting.model.DeviceConfigData;
import com.onyx.jdread.setting.model.DeviceConfigModel;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.SettingTitleModel;
import com.onyx.jdread.setting.utils.LocalPackageUpdateUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

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
        SettingTitleModel titleModel = SettingBundle.getInstance().getTitleModel();
        titleModel.setTitle(JDReadApplication.getInstance().getResources().getString(R.string.device_config));
        titleModel.setToggle(false);
        titleModel.setViewHistory(false);
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

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPasswordEvent(PasswordEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadToolEvent(ReadToolEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceInformationEvent(DeviceInformationEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemUpdateEvent(SystemUpdateEvent event) {
        DeviceConfigData deviceConfigData = event.getDeviceConfigData();
        // TODO: 2017/12/25  
    }
}
