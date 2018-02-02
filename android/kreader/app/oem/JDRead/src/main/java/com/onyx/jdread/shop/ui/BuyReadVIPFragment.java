package com.onyx.jdread.shop.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.BuyReadVipBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.adapter.BuyReadVipAdapter;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.model.BuyReadVipModel;
import com.onyx.jdread.shop.model.ShopDataBundle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2018/1/10.
 */

public class BuyReadVIPFragment extends BaseFragment {
    private BuyReadVipBinding binding;
    private BuyReadVipAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_buy_read_vip, container, false);
        initView();
        initLibrary();
        initData();
        return binding.getRoot();
    }

    private void initLibrary() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initLibrary();
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    private void initView() {
        initRecycleView();
        binding.setViewModel(getBuyReadVipModel());
        getBuyReadVipModel().getTitleBarViewModel().leftText = ResManager.getString(R.string.buy_read_vip);
    }

    private void initRecycleView() {
        PageRecyclerView buyVipRecycler = binding.buyVipRecycler;
        buyVipRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        adapter = new BuyReadVipAdapter();
        buyVipRecycler.setAdapter(adapter);
    }

    private void initData() {

    }

    private BuyReadVipModel getBuyReadVipModel() {
        return getShopDataBundle().getBuyReadVipModel();
    }

    private EventBus getEventBus() {
        return getShopDataBundle().getEventBus();
    }

    public ShopDataBundle getShopDataBundle() {
        return ShopDataBundle.getInstance();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }
}
