package com.onyx.jdread.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.model.LaboratoryModel;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentLaboratoryBinding;
import com.onyx.jdread.setting.model.SettingBundle;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    @Override
    public void onResume() {
        super.onResume();
        SettingBundle.getInstance().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        SettingBundle.getInstance().getEventBus().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
