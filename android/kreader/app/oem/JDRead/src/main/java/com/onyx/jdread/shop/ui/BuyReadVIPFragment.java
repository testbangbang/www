package com.onyx.jdread.shop.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.BuyReadVipBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.action.GetVipGoodListAction;
import com.onyx.jdread.shop.adapter.BuyReadVipAdapter;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.VipButtonClickEvent;
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
        getBuyReadVipModel().setVipUserInfoViewModel(getShopDataBundle().getVipUserInfoViewModel());
    }

    private void initRecycleView() {
        DashLineItemDivider itemDecoration = new DashLineItemDivider();
        PageRecyclerView buyVipRecycleView = binding.buyVipRecycleView;
        buyVipRecycleView.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        adapter = new BuyReadVipAdapter(getEventBus());
        buyVipRecycleView.setAdapter(adapter);
        buyVipRecycleView.addItemDecoration(itemDecoration);
    }

    private void initData() {
        GetVipGoodListAction vipGoodListAction = new GetVipGoodListAction();
        vipGoodListAction.execute(getShopDataBundle(), new RxCallback<GetVipGoodListAction>() {
            @Override
            public void onNext(GetVipGoodListAction action) {

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVipButtonClickEvent(VipButtonClickEvent event) {
        if (checkWfiDisConnected()) {
            return;
        }
        //TODO pay order
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        if (isAdded()) {
            showLoadingDialog(getString(event.getResId()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        hideLoadingDialog();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideLoadingDialog();
    }
}
