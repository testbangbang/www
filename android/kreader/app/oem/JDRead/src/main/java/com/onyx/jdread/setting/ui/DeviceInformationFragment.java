package com.onyx.jdread.setting.ui;

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
import com.onyx.jdread.setting.adapter.DeviceInfoAdapter;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.event.CopyrightNoticeEvent;
import com.onyx.jdread.setting.event.ResetDeviceEvent;
import com.onyx.jdread.setting.model.DeviceInformationModel;
import com.onyx.jdread.setting.model.SettingBundle;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by hehai on 18-1-2.
 */

public class DeviceInformationFragment extends BaseFragment {

    private FragmentDeviceInformationBinding binding;
    private DeviceInformationModel informationModel;
    private DeviceInfoAdapter deviceInfoAdapter;

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
        SettingBundle.getInstance().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        SettingBundle.getInstance().getEventBus().unregister(this);
    }

    @Subscribe
    public void onBackToDeviceConfigFragment(BackToDeviceConfigFragment event) {
        viewEventCallBack.viewBack();
    }

    @Subscribe
    public void onCopyrightNoticeEvent(CopyrightNoticeEvent event) {

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
                // TODO: 18-1-8 reset
            }
        });

        dialog.show();
    }
}
