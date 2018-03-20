package com.onyx.jdread.setting.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.OnyxPageDividerItemDecoration;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.databinding.RefreshBinding;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.setting.adapter.RefreshAdapter;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.event.SpeedRefreshChangeEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.SettingRefreshModel;
import com.onyx.jdread.setting.model.SettingTitleModel;
import com.onyx.jdread.util.ScreenUtils;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2017/12/21.
 */

public class RefreshFragment extends BaseFragment {
    private RefreshBinding binding;
    private RefreshAdapter refreshAdapter;
    private SettingRefreshModel settingRefreshModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (RefreshBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_refresh, container, false);
        initView();
        initData();
        initListener();
        return binding.getRoot();
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

    private void initData() {
        SettingTitleModel titleModel = SettingBundle.getInstance().getTitleModel();
        titleModel.setToggle(false);
        titleModel.setViewHistory(false);
        titleModel.setTitle(ResManager.getString(R.string.page_refresh));
        binding.refreshTitleBar.setTitleModel(titleModel);

        settingRefreshModel = SettingBundle.getInstance().getSettingRefreshModel();
        settingRefreshModel.setSpeedRefresh(JDPreferenceManager.getBooleanValue(R.string.speed_refresh_key, false));
        int currentRefreshPage = settingRefreshModel.getCurrentRefreshPage();
        settingRefreshModel.setCurrentPageRefreshPage(currentRefreshPage);
        refreshAdapter.setCurrentPage(currentRefreshPage);
        refreshAdapter.setData(settingRefreshModel.getRefreshPages(), settingRefreshModel.getRefreshValues());
        binding.refreshCheckBox.setChecked(settingRefreshModel.isSpeedRefresh());
    }

    private void initView() {
        binding.refreshRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration dividerItemDecoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.refreshRecycler.addItemDecoration(dividerItemDecoration);
        refreshAdapter = new RefreshAdapter();
        binding.refreshRecycler.setAdapter(refreshAdapter);
    }

    private void initListener() {
        binding.refreshCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JDPreferenceManager.setBooleanValue(R.string.speed_refresh_key, isChecked);
                settingRefreshModel.setSpeedRefresh(isChecked);
                ToastUtil.showToast(isChecked ? ResManager.getString(R.string.speed_refresh_is_opened) : ResManager.getString(R.string.speed_refresh_is_closed));
            }
        });

        refreshAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                settingRefreshModel.setCurrentPageRefreshPage(settingRefreshModel.getRefreshValues()[position]);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        viewEventCallBack.viewBack();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeedRefreshChangeEvent(SpeedRefreshChangeEvent event) {
        binding.refreshCheckBox.setChecked(JDPreferenceManager.getBooleanValue(R.string.speed_refresh_key, false));
    }
}
