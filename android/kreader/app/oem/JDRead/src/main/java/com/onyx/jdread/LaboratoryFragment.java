package com.onyx.jdread;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentLaboratoryBinding;

/**
 * Created by hehai on 17-12-27.
 */

public class LaboratoryFragment extends BaseFragment {

    private FragmentLaboratoryBinding laboratoryBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        laboratoryBinding = FragmentLaboratoryBinding.inflate(inflater, container, false);
        initData();
        return laboratoryBinding.getRoot();
    }

    private void initData() {
        LaboratoryModel laboratoryModel = new LaboratoryModel();
        laboratoryBinding.laboratoryTitle.setTitleModel(laboratoryModel.titleBarModel);
        laboratoryBinding.setLaboratoryModel(laboratoryModel);
    }
}
