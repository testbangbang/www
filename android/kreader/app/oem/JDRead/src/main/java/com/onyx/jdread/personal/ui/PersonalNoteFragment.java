package com.onyx.jdread.personal.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.PersonalNoteBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.adapter.PersonalNoteAdapter;
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
        OnyxPageDividerItemDecoration decoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
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
        binding.personalNoteExportFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.setExport(true);
                personalNoteAdapter.showBox(true);
            }
        });

        binding.personalNoteSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO: 2018/1/3
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

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
