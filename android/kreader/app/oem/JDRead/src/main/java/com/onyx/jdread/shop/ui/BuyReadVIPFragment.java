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
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.action.UserInfoAction;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfo;
import com.onyx.jdread.personal.dialog.TopUpDialog;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.action.GetOrderInfoAction;
import com.onyx.jdread.shop.action.GetVipGoodListAction;
import com.onyx.jdread.shop.adapter.BuyReadVipAdapter;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetOrderInfoResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetVipGoodsListResultBean;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.event.PayByCashSuccessEvent;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.VipButtonClickEvent;
import com.onyx.jdread.shop.event.VipGoodItemClickEvent;
import com.onyx.jdread.shop.model.BuyReadVipModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.utils.ViewHelper;
import com.onyx.jdread.shop.view.BookInfoDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2018/1/10.
 */

public class BuyReadVIPFragment extends BaseFragment {
    private BuyReadVipBinding binding;
    private BuyReadVipAdapter adapter;

    private BookInfoDialog vipNoticeDialog;
    private TopUpDialog topUpDialog;
    private boolean isVipBuying = false;

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
        checkWifi(getBuyReadVipModel().getTitleBarViewModel().leftText);
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
        if (checkWifiDisconnected()) {
            return;
        }
        showVipNoticeDialog();
    }

    private void showVipNoticeDialog() {
        if (ViewHelper.dialogIsShowing(vipNoticeDialog)) {
            return;
        }
        vipNoticeDialog = ViewHelper.showNoticeDialog(getActivity(),
                ResManager.getString(R.string.read_vip_instructions),
                ResManager.getUriOfRawName("joyread_notice.html"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewHelper.dismissDialog(vipNoticeDialog);
                        vipNoticeDialog = null;
                    }
                });
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayByCashSuccessEvent(PayByCashSuccessEvent event) {
        UserInfoAction userInfoAction = new UserInfoAction();
        userInfoAction.execute(PersonalDataBundle.getInstance(), new RxCallback<UserInfoAction>() {
            @Override
            public void onNext(UserInfoAction userInfoAction) {
                updateVipStatus(userInfoAction.getUserInfoData().data);
            }
        });
    }

    private void updateVipStatus(UserInfo info) {
        if (info != null) {
            getUserInfo().vip_remain_days = info.vip_remain_days;
            getBuyReadVipModel().getVipUserInfoViewModel().vipStatus.set(String.format(
                    ResManager.getString(R.string.vip_read_days), info.vip_remain_days));
        }
    }

    private UserInfo getUserInfo() {
        return PersonalDataBundle.getInstance().getUserInfo();
    }

    private int getUserVipRemainDays() {
        if (getUserInfo() == null) {
            return 0;
        }
        return getUserInfo().vip_remain_days;
    }

    private boolean checkVipGoodCanBuy(GetVipGoodsListResultBean.DataBean dataBean) {
        if (dataBean == null) {
            ToastUtil.showToast(R.string.read_vip_can_not_buy);
            return false;
        }
        if (!dataBean.can_buy) {
            String message;
            int maxYears = ResManager.getInteger(R.integer.read_vip_max_year);
            int vipRemainDays = getUserVipRemainDays();
            if (vipRemainDays / 365 >= maxYears) {
                message = String.format(ResManager.getString(R.string.read_vip_surpass_yeas), maxYears);
            } else {
                message = ResManager.getString(R.string.read_vip_can_not_buy);
            }
            ToastUtil.showToast(message);
            return false;
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVipGoodItemClickEvent(VipGoodItemClickEvent event) {
        if (checkWifiDisconnected()) {
            return;
        }
        if (!checkVipGoodCanBuy(event.dataBean)) {
            return;
        }
        if (ViewHelper.dialogIsShowing(topUpDialog) || isVipBuying()) {
            return;
        }
        getOrderInfo(new String[]{String.valueOf(event.dataBean.sku_id)});
    }

    private void getOrderInfo(String[] ids) {
        if (ids != null) {
            setVipBuying(true);
            GetOrderInfoAction action = new GetOrderInfoAction(ids);
            action.execute(getShopDataBundle(), new RxCallback<GetOrderInfoAction>() {
                @Override
                public void onNext(GetOrderInfoAction getOrderInfoAction) {
                    showTopUpDialog(getOrderInfoAction.getDataBean());
                }

                @Override
                public void onError(Throwable throwable) {
                    ToastUtil.showToast(R.string.network_exception);
                }

                @Override
                public void onFinally() {
                    setVipBuying(false);
                }
            });
        }
    }

    private void showTopUpDialog(GetOrderInfoResultBean.DataBean orderData) {
        if (orderData != null && !ViewHelper.dialogIsShowing(topUpDialog)) {
            topUpDialog = new TopUpDialog();
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.PAY_DIALOG_TYPE, Constants.PAY_DIALOG_TYPE_PAY_ORDER);
            bundle.putSerializable(Constants.ORDER_INFO, orderData);
            bundle.putBoolean(Constants.PAY_BY_CASH, true);
            bundle.putString(Constants.ORDER_FORM_VIP,Constants.OPEN_VIP);
            topUpDialog.setArguments(bundle);
            topUpDialog.show(getActivity().getFragmentManager(), "");
        }
    }

    private void setVipBuying(boolean buying) {
        isVipBuying = buying;
    }

    private boolean isVipBuying() {
        return isVipBuying;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideAllDialog();
    }

    private void hideAllDialog() {
        hideLoadingDialog();
        ViewHelper.dismissDialog(vipNoticeDialog);
        ViewHelper.dismissDialog(topUpDialog);
    }
}
