package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentScreenSaversBinding;
import com.onyx.jdread.library.event.HideAllDialogEvent;
import com.onyx.jdread.library.event.LoadingDialogEvent;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.setting.action.LoadScreenSaversPicAction;
import com.onyx.jdread.setting.action.SaveBootPicAction;
import com.onyx.jdread.setting.adapter.ScreenSaversAdapter;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.event.CheckPicToScreenSaversEvent;
import com.onyx.jdread.setting.model.ScreenSaversModel;
import com.onyx.jdread.setting.model.SettingBundle;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by hehai on 18-1-1.
 */

public class ScreensaversFragment extends BaseFragment {

    private FragmentScreenSaversBinding binding;
    private ScreenSaversAdapter screenSaversAdapter;
    private ScreenSaversModel screenSaversModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initData(inflater, container);
        initRecyclerView();
        loadData();
        return binding.getRoot();
    }

    private void loadData() {
        final LoadScreenSaversPicAction action = new LoadScreenSaversPicAction(screenSaversModel);
        action.execute(SettingBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateView();
            }
        });
    }

    private void updateView() {
        screenSaversAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        binding.screenSaverRecycler.setLayoutManager(new DisableScrollGridManager(getContext().getApplicationContext()));
        screenSaversAdapter = new ScreenSaversAdapter();
        binding.screenSaverRecycler.setAdapter(screenSaversAdapter);
    }

    private void initData(LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = FragmentScreenSaversBinding.inflate(inflater, container, false);
        screenSaversModel = new ScreenSaversModel();
        binding.screenSaverTitle.setTitleModel(screenSaversModel.titleBarModel);
        binding.setModel(screenSaversModel);
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
    public void onCheckPicToScreenSaversEvent(CheckPicToScreenSaversEvent event) {
        SaveBootPicAction saveBootPicAction = new SaveBootPicAction(event.getPath());
        saveBootPicAction.execute(SettingBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                loadData();
            }
        });
    }

    @Subscribe
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        showLoadingDialog(String.format(getString(R.string.loading_format), getString(R.string.being_set_up)));
    }

    @Subscribe
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        hideLoadingDialog();
    }
}
