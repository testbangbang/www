package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evernote.client.android.login.EvernoteLoginFragment;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentReadingToolsBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.manager.EvernoteManager;
import com.onyx.jdread.setting.adapter.DeviceInfoAdapter;
import com.onyx.jdread.setting.event.AssociatedEmailToolsEvent;
import com.onyx.jdread.setting.event.AssociatedNotesToolsEvent;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.event.DictionaryToolsEvent;
import com.onyx.jdread.setting.event.TranslationToolsEvent;
import com.onyx.jdread.setting.model.ReadingToolsModel;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.view.AssociatedEmailDialog;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by hehai on 18-1-2.
 */

public class ReadingToolsFragment extends BaseFragment implements EvernoteLoginFragment.ResultCallback {

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
        DashLineItemDivider dividerItemDecoration = new DashLineItemDivider();
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
        AssociatedEmailDialog.DialogModel model = new AssociatedEmailDialog.DialogModel();
        String email = PreferenceManager.getStringValue(JDReadApplication.getInstance(), R.string.email_address_key, null);
        boolean bound = StringUtils.isNotBlank(email);
        model.title.set(bound ? getString(R.string.unbind_to_email) : getString(R.string.bind_to_email));
        model.emailAddress.set(email);
        model.bound.set(bound);
        AssociatedEmailDialog.Builder builder = new AssociatedEmailDialog.Builder(JDReadApplication.getInstance(), model);
        AssociatedEmailDialog dialog = builder.create();
        dialog.show();
    }

    @Subscribe
    public void onAssociatedNotesToolsEvent(AssociatedNotesToolsEvent event) {
        EvernoteManager.getEvernoteSession(JDReadApplication.getInstance()).authenticate(getActivity());
    }

    @Subscribe
    public void onTranslationToolsEvent(TranslationToolsEvent event) {
        viewEventCallBack.gotoView(TranslateFragment.class.getName());
    }

    @Subscribe
    public void onDictionaryToolsEvent(DictionaryToolsEvent event) {

    }

    @Override
    public void onLoginFinished(boolean successful) {
        ToastUtil.showToast("login success");
    }
}
