package com.onyx.jdread.setting.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.SystemUpdateBinding;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.SettingTitleModel;

/**
 * Created by li on 2017/12/22.
 */

public class SystemUpdateFragment extends BaseFragment {
    private SystemUpdateBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (SystemUpdateBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_system_update, container, false);
        initData();
        return binding.getRoot();
    }

    private void initData() {
        SettingTitleModel titleModel = SettingBundle.getInstance().getTitleModel();
        titleModel.setTitle(JDReadApplication.getInstance().getResources().getString(R.string.system_update));
        titleModel.setViewHistory(true);
        titleModel.setToggle(false);
        binding.systemUpdateSettingBar.setTitleModel(titleModel);
    }
}
