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
import com.onyx.jdread.databinding.PersonalTaskBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.adapter.PersonalTaskAdapter;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.PersonalTaskModel;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2018/1/3.
 */

public class PersonalTaskFragment extends BaseFragment {
    private PersonalTaskBinding binding;
    private PersonalTaskAdapter personalTaskAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (PersonalTaskBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_personal_task, container, false);
        initView();
        initData();
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
        binding.personalTaskRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider decoration = new DashLineItemDivider();
        binding.personalTaskRecycler.addItemDecoration(decoration);
        personalTaskAdapter = new PersonalTaskAdapter();
        binding.personalTaskRecycler.setAdapter(personalTaskAdapter);
    }

    private void initData() {
        TitleBarModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.title.set(JDReadApplication.getInstance().getResources().getString(R.string.personal_task));
        titleModel.backEvent.set(new BackToSettingFragmentEvent());
        binding.personalTaskTitle.setTitleModel(titleModel);

        PersonalTaskModel personalTaskModel = PersonalDataBundle.getInstance().getPersonalTaskModel();
        if (personalTaskAdapter != null) {
            personalTaskAdapter.setData(personalTaskModel.getTasks());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
