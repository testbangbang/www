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
import com.onyx.jdread.databinding.GiftCenterBinding;
import com.onyx.jdread.personal.adapter.GiftCenterAdapter;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.setting.model.SettingTitleModel;

/**
 * Created by li on 2017/12/29.
 */

public class GiftCenterFragment extends BaseFragment {
    private GiftCenterBinding binding;
    private GiftCenterAdapter giftCenterAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = (GiftCenterBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_gift_center, container, false);
        initView();
        initData();
        return binding.getRoot();
    }

    private void initData() {
        SettingTitleModel titleModel = PersonalDataBundle.getInstance().getTitleModel();
        titleModel.setTitle(JDReadApplication.getInstance().getResources().getString(R.string.gift_center));
        titleModel.setViewHistory(false);
        titleModel.setToggle(false);
        binding.giftCenterTitleBar.setTitleModel(titleModel);
    }

    private void initView() {
        binding.giftCenterRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        binding.giftCenterRecycler.addItemDecoration(new OnyxPageDividerItemDecoration(JDReadApplication.getInstance(), OnyxPageDividerItemDecoration.VERTICAL));
        giftCenterAdapter = new GiftCenterAdapter();
        binding.giftCenterRecycler.setAdapter(giftCenterAdapter);
    }
}
