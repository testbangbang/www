package com.onyx.jdread.setting.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.databinding.LockScreenBinding;
import com.onyx.jdread.setting.adapter.LockScreenAdapter;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.SettingLockScreenModel;
import com.onyx.jdread.setting.model.SettingTitleModel;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by li on 2017/12/21.
 */

public class LockScreenFragment extends BaseFragment {
    private LockScreenBinding binding;
    private LockScreenAdapter lockScreenAdapter;
    private SettingLockScreenModel settingLockScreenModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (LockScreenBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_lock_screen, container, false);
        initView();
        initData();
        initListener();
        return binding.getRoot();
    }

    private void initListener() {
        if (lockScreenAdapter != null) {
            lockScreenAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    settingLockScreenModel.setCurrentTime(lockScreenAdapter.getCurrentScreenTimeout());
                }
            });
        }
    }

    private void initView() {
        binding.lockScreenRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DashLineItemDivider dividerItemDecoration = new DashLineItemDivider();
        binding.lockScreenRecycler.addItemDecoration(dividerItemDecoration);
        lockScreenAdapter = new LockScreenAdapter();
        binding.lockScreenRecycler.setAdapter(lockScreenAdapter);
    }

    private void initData() {
        SettingTitleModel titleModel = SettingBundle.getInstance().getTitleModel();
        titleModel.setTitle(JDReadApplication.getInstance().getResources().getString(R.string.interest_rates_screen_time));
        titleModel.setToggle(false);
        titleModel.setViewHistory(false);
        binding.lockScreenTitleBar.setTitleModel(titleModel);

        settingLockScreenModel = SettingBundle.getInstance().getSettingLockScreenModel();
        if (lockScreenAdapter != null) {
            lockScreenAdapter.setTimes(settingLockScreenModel.getLockScreenTimes(), settingLockScreenModel.getLockScreenValues(), settingLockScreenModel.getCurrentTime());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Subscribe
    public void onBackToSettingFragment(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }
}
