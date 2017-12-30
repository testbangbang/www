package com.onyx.jdread.personal.ui;

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
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.PersonalExperienceBinding;
import com.onyx.jdread.personal.adapter.PersonalExperienceAdapter;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.model.SettingTitleModel;

/**
 * Created by li on 2017/12/29.
 */

public class PersonalExperienceFragment extends BaseFragment {
    private PersonalExperienceBinding binding;
    private PersonalExperienceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (PersonalExperienceBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_personal_experience, container, false);
        initView();
        initData();
        return binding.getRoot();
    }

    private void initData() {
        SettingTitleModel titleModel = PersonalDataBundle.getInstance().getTitleModel();

    }

    private void initView() {
        binding.experienceRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration decoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        decoration.setBlankCount(8);
        binding.experienceRecycler.addItemDecoration(decoration);
        adapter = new PersonalExperienceAdapter();
        binding.experienceRecycler.setAdapter(adapter);
    }
}
