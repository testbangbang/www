package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PersonalNoteBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.adapter.PersonalNoteAdapter;
import com.onyx.jdread.personal.dialog.ExportDialog;
import com.onyx.jdread.personal.event.ExportToEmailEvent;
import com.onyx.jdread.personal.event.ExportToImpressionEvent;
import com.onyx.jdread.personal.event.ExportToNativeEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2018/1/3.
 */

public class PersonalNoteFragment extends BaseFragment {
    private PersonalNoteBinding binding;
    private PersonalNoteAdapter personalNoteAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (PersonalNoteBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_personal_note, container, false);
        initView();
        initData();
        initListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        PersonalDataBundle.getInstance().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        PersonalDataBundle.getInstance().getEventBus().unregister(this);
    }

    private void initView() {
        binding.personalNoteRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider decoration = new DashLineItemDivider();
        binding.personalNoteRecycler.addItemDecoration(decoration);
        personalNoteAdapter = new PersonalNoteAdapter();
        binding.personalNoteRecycler.setAdapter(personalNoteAdapter);
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.personal_notes));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.personalNoteTitle.setTitleModel(titleModel);
    }

    private void initListener() {
        binding.personalNoteCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalNoteAdapter.showBox(true);
            }
        });

        binding.personalNoteExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExportDialog();
            }
        });
    }

    private void showExportDialog() {
        ExportDialog dialog = new ExportDialog();
        dialog.show(getActivity().getFragmentManager(), "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportToNativeEvent(ExportToNativeEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportToEmailEvent(ExportToEmailEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportToImpressionEvent(ExportToImpressionEvent event) {
    }
}
