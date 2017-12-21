package com.onyx.jdread.setting.ui;

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
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.RefreshBinding;
import com.onyx.jdread.setting.adapter.RefreshAdapter;
import com.onyx.jdread.setting.event.BackToSettingFragmentEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.model.SettingRefreshModel;
import com.onyx.jdread.setting.model.SettingTitleModel;
import com.onyx.jdread.setting.utils.ScreenUtils;

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
        SettingBundle.getInstance().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        SettingBundle.getInstance().getEventBus().unregister(this);
    }

    private void initData() {
        SettingTitleModel titleModel = SettingBundle.getInstance().getTitleModel();
        titleModel.setToggle(false);
        titleModel.setTitle(JDReadApplication.getInstance().getResources().getString(R.string.page_refresh));
        binding.refreshTitleBar.setTitleModel(titleModel);

        settingRefreshModel = SettingBundle.getInstance().getSettingRefreshModel();
        String currentRefreshPage = settingRefreshModel.getCurrentRefreshPage();
        settingRefreshModel.setCurrentPageRefreshPage(currentRefreshPage);
        refreshAdapter.setCurrentPage(currentRefreshPage);
        refreshAdapter.setData(settingRefreshModel.getRefreshPages());
    }

    private void initView() {
        binding.refreshRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        OnyxPageDividerItemDecoration dividerItemDecoration = new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL);
        binding.refreshRecycler.addItemDecoration(dividerItemDecoration);
        refreshAdapter = new RefreshAdapter();
        binding.refreshRecycler.setAdapter(refreshAdapter);
    }

    private void initListener() {
        binding.refreshToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ScreenUtils.toggleA2Mode(JDReadApplication.getInstance());
                binding.setRecyclerEnable(isChecked);
                if (refreshAdapter != null) {
                    refreshAdapter.setA2Mode(isChecked);
                }
            }
        });

        refreshAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                settingRefreshModel.setCurrentPageRefreshPage(settingRefreshModel.getRefreshPages()[position]);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToSettingFragmentEvent(BackToSettingFragmentEvent event) {
        getViewEventCallBack().gotoView(SettingFragment.class.getName());
    }
}
