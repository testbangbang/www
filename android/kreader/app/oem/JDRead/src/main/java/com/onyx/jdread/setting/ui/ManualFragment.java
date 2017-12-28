package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentManualBinding;
import com.onyx.jdread.model.TitleBarModel;

/**
 * Created by hehai on 17-12-28.
 */

public class ManualFragment extends BaseFragment {

    private FragmentManualBinding manualBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        manualBinding = FragmentManualBinding.inflate(inflater, container, false);
        loadData();
        return manualBinding.getRoot();
    }

    private void loadData() {
        TitleBarModel titleBarModel = new TitleBarModel();
        titleBarModel.title.set(getString(R.string.manual));
        manualBinding.manualTitle.setTitleModel(titleBarModel);
    }
}
