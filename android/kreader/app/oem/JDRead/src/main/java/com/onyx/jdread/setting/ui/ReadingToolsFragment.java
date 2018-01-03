package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentReadingToolsBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.setting.adapter.DeviceInfoAdapter;
import com.onyx.jdread.setting.event.AssociatedEmailToolsEvent;
import com.onyx.jdread.setting.event.AssociatedNotesToolsEvent;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.event.DictionaryToolsEvent;
import com.onyx.jdread.setting.event.TranslationToolsEvent;
import com.onyx.jdread.setting.model.ReadingToolsModel;
import com.onyx.jdread.setting.model.SettingBundle;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by hehai on 18-1-2.
 */

public class ReadingToolsFragment extends BaseFragment {

    private FragmentReadingToolsBinding binding;
    private ReadingToolsModel readingToolsModel;
    private DeviceInfoAdapter deviceInfoAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initBinding(inflater, container);
        initRecycler();
        return binding.getRoot();
    }

    private void initRecycler() {
        OnyxPageDividerItemDecoration dividerItemDecoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.readingToolsRecycler.addItemDecoration(dividerItemDecoration);
        deviceInfoAdapter = new DeviceInfoAdapter();
        deviceInfoAdapter.setRowAndCol(getResources().getInteger(R.integer.reading_tools_row), getResources().getInteger(R.integer.reading_tools_col));
        binding.readingToolsRecycler.setAdapter(deviceInfoAdapter);
    }

    private void initBinding(LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = FragmentReadingToolsBinding.inflate(inflater, container, false);
        readingToolsModel = new ReadingToolsModel(SettingBundle.getInstance().getEventBus());
        binding.readingToolsTitle.setTitleModel(readingToolsModel.titleBarModel);
        binding.setReadingToolsModel(readingToolsModel);
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
    public void onAssociatedEmailToolsEvent(AssociatedEmailToolsEvent event) {

    }

    @Subscribe
    public void onAssociatedNotesToolsEvent(AssociatedNotesToolsEvent event) {

    }

    @Subscribe
    public void onTranslationToolsEvent(TranslationToolsEvent event) {

    }

    @Subscribe
    public void onDictionaryToolsEvent(DictionaryToolsEvent event) {

    }
}
