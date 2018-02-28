package com.onyx.jdread.setting.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentDeviceInformationBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.library.view.LibraryDeleteDialog;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.manager.ManagerActivityUtils;
import com.onyx.jdread.setting.adapter.DeviceInfoAdapter;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.event.CopyrightNoticeEvent;
import com.onyx.jdread.setting.event.DeviceModelEvent;
import com.onyx.jdread.setting.event.ResetDeviceEvent;
import com.onyx.jdread.setting.model.DeviceInformationModel;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.util.TimeUtils;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by hehai on 18-1-2.
 */

public class DeviceInformationFragment extends BaseFragment {

    private FragmentDeviceInformationBinding binding;
    private DeviceInformationModel informationModel;
    private DeviceInfoAdapter deviceInfoAdapter;
    private long lastPressTime;
    private long resetPressCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initBinding(inflater, container);
        initRecycler();
        return binding.getRoot();
    }

    private void initRecycler() {
        DashLineItemDivider dividerItemDecoration = new DashLineItemDivider();
        binding.deviceInfoRecycler.addItemDecoration(dividerItemDecoration);
        deviceInfoAdapter = new DeviceInfoAdapter();
        binding.deviceInfoRecycler.setAdapter(deviceInfoAdapter);
    }

    private void initBinding(LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = FragmentDeviceInformationBinding.inflate(inflater, container, false);
        informationModel = new DeviceInformationModel(SettingBundle.getInstance().getEventBus());
        binding.deviceInfoTitle.setTitleModel(informationModel.titleBarModel);
        binding.setDeviceInfoModel(informationModel);
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
    public void onBackToDeviceConfigFragment(BackToDeviceConfigFragment event) {
        viewEventCallBack.viewBack();
    }

    @Subscribe
    public void onCopyrightNoticeEvent(CopyrightNoticeEvent event) {

    }

    @Subscribe
    public void onDeviceModelEvent(DeviceModelEvent event) {
        openSystemSetting();
    }

    @Subscribe
    public void onResetDeviceEvent(ResetDeviceEvent event) {
        LibraryDeleteDialog.DialogModel model = new LibraryDeleteDialog.DialogModel();
        model.message.set(getString(R.string.device_reset_prompt));
        LibraryDeleteDialog.Builder builder = new LibraryDeleteDialog.Builder(JDReadApplication.getInstance(), model);
        final LibraryDeleteDialog dialog = builder.create();
        model.setNegativeClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                dialog.dismiss();
            }
        });

        model.setPositiveClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                dialog.dismiss();
                ManagerActivityUtils.reset(JDReadApplication.getInstance());
            }
        });

        dialog.show();
    }

    private void openSystemSetting() {
        if (TimeUtils.getCurrentTimeInLong() > lastPressTime + Constants.RESET_PRESS_TIMEOUT) {
            lastPressTime = TimeUtils.getCurrentTimeInLong();
            resetPressCount = 0;
        }
        resetPressCount++;
        if (resetPressCount >= Constants.SYSTEM_SETTING_PRESS_COUNT) {
            resetPressCount = 0;
            lastPressTime = 0;
            startSystemSettingActivity(getActivity());
        }
    }

    public void startSystemSettingActivity(Context context) {
        Intent mIntent = new Intent();
        ComponentName comp = new ComponentName("com.android.settings",
                "com.android.settings.Settings");
        mIntent.setComponent(comp);
        mIntent.setAction("android.intent.action.VIEW");
        context.startActivity(mIntent);
    }
}
