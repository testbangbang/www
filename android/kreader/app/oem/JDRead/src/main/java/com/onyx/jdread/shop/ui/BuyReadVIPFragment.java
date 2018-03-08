package com.onyx.jdread.shop.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.BuyReadVipBinding;
import com.onyx.jdread.databinding.DialogVipNoticeBinding;
import com.onyx.jdread.library.view.DashLineItemDivider;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.action.UserInfoAction;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfo;
import com.onyx.jdread.personal.dialog.TopUpDialog;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.reader.ui.view.HTMLReaderWebView;
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
import com.onyx.jdread.shop.model.DialogBookInfoViewModel;
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
        checkWifi();
    }

    private void checkWifi() {
        if (checkWifiAndGoNetWorkErrorFragment()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.NET_ERROR_TITLE, ResManager.getString(R.string.read_vip));
            setBundle(bundle);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    private void initView() {
        initRecycleView();
        binding.setViewModel(getBuyReadVipModel());
        getBuyReadVipModel().getTitleBarViewModel().leftText = ResManager.getString(R.string.read_vip);
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
        showVipNoticeDialog();
    }

    private void showVipNoticeDialog() {
        if (ViewHelper.dialogIsShowing(vipNoticeDialog)) {
            return;
        }
        final DialogVipNoticeBinding infoBinding = DialogVipNoticeBinding.inflate(LayoutInflater.from(getActivity()), null, false);
        final DialogBookInfoViewModel infoViewModel = new DialogBookInfoViewModel();
        infoViewModel.title.set(ResManager.getString(R.string.read_vip_instructions));
        infoBinding.setViewModel(infoViewModel);
        vipNoticeDialog = new BookInfoDialog(JDReadApplication.getInstance());
        vipNoticeDialog.setView(infoBinding.getRoot());
        HTMLReaderWebView pagedWebView = infoBinding.infoWebView;
        pagedWebView.setCallParentPageFinishedMethod(false);
        pagedWebView.loadUrl(ResManager.getUriOfRawName("joyread_notice.html"));
        WebSettings settings = pagedWebView.getSettings();
        settings.setSupportZoom(false);
        settings.setTextZoom(Constants.WEB_VIEW_TEXT_ZOOM);
        pagedWebView.registerOnOnPageChangedListener(new HTMLReaderWebView.OnPageChangedListener() {
            @Override
            public void onPageChanged(int totalPage, int curPage) {
                infoViewModel.currentPage.set(curPage);
                infoViewModel.totalPage.set(totalPage);
            }
        });
        infoBinding.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHelper.dismissDialog(vipNoticeDialog);
                vipNoticeDialog = null;
            }
        });
        vipNoticeDialog.show();
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
            PersonalDataBundle.getInstance().getUserInfo().vip_remain_days = info.vip_remain_days;
            getBuyReadVipModel().getVipUserInfoViewModel().vipStatus.set(String.format(
                    ResManager.getString(R.string.vip_read_days), info.vip_remain_days));
        }
    }

    private boolean checkVipGoodCanBuy(GetVipGoodsListResultBean.DataBean dataBean) {
        return dataBean != null && dataBean.can_buy;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVipGoodItemClickEvent(VipGoodItemClickEvent event) {
        if (checkWfiDisConnected()) {
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
