package com.onyx.jdread.personal.dialog;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogTopUpBinding;
import com.onyx.jdread.library.utils.QRCodeUtil;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.action.GetPayQRCodeAction;
import com.onyx.jdread.personal.action.GetRechargeStatusAction;
import com.onyx.jdread.personal.action.GetTopUpValueAction;
import com.onyx.jdread.personal.adapter.TopUpAdapter;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetPayQRCodeBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetRechargePackageBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetRechargeStatusBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfo;
import com.onyx.jdread.personal.event.GetRechargePollEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetPayResultByCashRequest;
import com.onyx.jdread.shop.action.BuyChaptersAction;
import com.onyx.jdread.shop.action.PayByReadBeanAction;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.cloud.entity.NetBookPayParamsBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BaseResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BuyChaptersResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetOrderInfoResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.event.BuyBookSuccessEvent;
import com.onyx.jdread.shop.event.ConfirmPayClickEvent;
import com.onyx.jdread.shop.event.PayByCashSuccessEvent;
import com.onyx.jdread.shop.model.PayOrderViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.utils.ViewHelper;
import com.onyx.jdread.shop.view.DividerItemDecoration;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by li on 2017/12/29.
 */

public class TopUpDialog extends DialogFragment {
    private DialogTopUpBinding binding;
    private TopUpAdapter topUpAdapter;
    private GetRechargePollEvent getRechargePollEvent;
    private static final int DEFAULT_POLL_TIME = 300;
    private Disposable countDownDisposable;
    private RxGetPayResultByCashRequest payByCashRequest;
    private int payDialogType;
    private NetBookPayParamsBean payParamsBean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        binding = (DialogTopUpBinding) DataBindingUtil.inflate(inflater, R.layout.dialog_top_up_layout, container, false);
        initView();
        initData();
        initListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        int screenWidth = Utils.getScreenWidth(JDReadApplication.getInstance());
        int screenHeight = Utils.getScreenHeight(JDReadApplication.getInstance());
        attributes.width = (int) (screenWidth * Utils.getValuesFloat(R.integer.top_up_dialog_width_rate));
        attributes.height = (int) (screenHeight * Utils.getValuesFloat(R.integer.top_up_dialog_height_rate));
        window.setAttributes(attributes);
        Utils.ensureRegister(PersonalDataBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(PersonalDataBundle.getInstance().getEventBus(), this);
        if (getRechargePollEvent != null) {
            getRechargePollEvent.setPollTime(0);
        }
    }

    private void initView() {
        binding.dialogTopUpDetailLayout.dialogTopUpRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DividerItemDecoration decoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        binding.dialogTopUpDetailLayout.dialogTopUpRecycler.addItemDecoration(decoration);
        topUpAdapter = new TopUpAdapter();
        binding.dialogTopUpDetailLayout.dialogTopUpRecycler.setAdapter(topUpAdapter);
        SpannableString ss = new SpannableString(ResManager.getString(R.string.way_to_pay));
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        StyleSpan styleSpan2 = new StyleSpan(Typeface.BOLD);
        ss.setSpan(styleSpan, 2, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(styleSpan2, 8, 12, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        binding.dialogTopUpQrCodeLayout.payWay.setText(ss);
        binding.payOrder.payWay.setText(ss);
        if (isPayByCash()) {
            binding.payOrder.payOrderBalance.setVisibility(View.GONE);
            binding.payOrder.paymentReadBean.setVisibility(View.GONE);
            binding.payOrder.paymentCash.setChecked(true);
        }
    }

    private void initData() {
        Bundle arguments = getArguments();
        PayOrderViewModel payOrderViewModel = getPayOrderViewModel();
        if (arguments != null) {
            payDialogType = arguments.getInt(Constants.PAY_DIALOG_TYPE);
            if (payDialogType == Constants.PAY_DIALOG_TYPE_PAY_ORDER) {
                GetOrderInfoResultBean.DataBean orderInfo = (GetOrderInfoResultBean.DataBean) arguments.getSerializable(Constants.ORDER_INFO);
                payOrderViewModel.showPaymentLinearLayout.set(true);
                payOrderViewModel.setOrderInfo(orderInfo);
                getPayOrderViewModel().title.set("");
                binding.setOrderModel(payOrderViewModel);
                changePayButtonState(!orderInfo.need_recharge);
                binding.payOrder.paymentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        if (R.id.payment_read_bean == checkedId) {
                            setPayOrderView(false);
                            changePayButtonState(!getPayOrderViewModel().getOrderInfo().need_recharge);
                        } else {
                            changePayButtonState(true);
                        }
                    }
                });
            } else if (isBuyNetBook()) {
                payOrderViewModel.showPaymentLinearLayout.set(false);
                payParamsBean = (NetBookPayParamsBean) arguments.getSerializable(Constants.ORDER_INFO);
                GetOrderInfoResultBean.DataBean orderInfo = new GetOrderInfoResultBean.DataBean();
                orderInfo.pay_amount = payParamsBean.jd_price;
                orderInfo.voucher_amount = payParamsBean.voucher;
                orderInfo.yuedou_amount = payParamsBean.yuedou;
                orderInfo.desc = payParamsBean.bookName;
                payOrderViewModel.setOrderInfo(orderInfo);
                getPayOrderViewModel().getOrderInfo().need_recharge = false;
                getPayOrderViewModel().title.set("");
                changePayButtonState(true);
                binding.setOrderModel(payOrderViewModel);
            } else {
                gotoTopUpPage();
            }
        }
    }

    private boolean isPayByCash() {
        return getArguments() != null && getArguments().getBoolean(Constants.PAY_BY_CASH, false);
    }

    private boolean isBuyNetBook() {
        return payDialogType == Constants.PAY_DIALOG_TYPE_NET_BOOK;
    }

    private void buyNetBookChapters(NetBookPayParamsBean orderInfo) {
        BuyChaptersAction action = new BuyChaptersAction(orderInfo.ebookId, orderInfo.start_chapter, orderInfo.count);
        action.execute(ShopDataBundle.getInstance(), new RxCallback<BuyChaptersAction>() {
            @Override
            public void onNext(BuyChaptersAction action) {
                BuyChaptersResultBean resultBean = action.getResultBean();
                checkPayResult(resultBean, true);
            }
        });
    }

    private void changePayButtonState(boolean showPayStatus) {
        PayOrderViewModel payOrderViewModel = getPayOrderViewModel();
        payOrderViewModel.confirmButtonText.set(showPayStatus ? ResManager.getString(R.string.dialog_pay_order_confirm_pay) :
                ResManager.getString(R.string.pay_dialog_insufficient_balance));
    }

    private PayOrderViewModel getPayOrderViewModel() {
        return ShopDataBundle.getInstance().getPayOrderViewModel();
    }

    private void getTopUpValue() {
        GetTopUpValueAction action = new GetTopUpValueAction();
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<GetRechargePackageBean.DataBean> topValueBeans = PersonalDataBundle.getInstance().getTopValueBeans();
                topUpAdapter.setData(topValueBeans);
            }
        });

        UserInfo userInfo = PersonalDataBundle.getInstance().getUserInfo();
        if (userInfo != null) {
            binding.setUserInfo(userInfo);
        }
    }

    private void initListener() {
        if (topUpAdapter != null) {
            topUpAdapter.setOnItemClickListener(new PageRecyclerView.PageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    List<GetRechargePackageBean.DataBean> topValueBeans = PersonalDataBundle.getInstance().getTopValueBeans();
                    GetRechargePackageBean.DataBean dataBean = topValueBeans.get(position);
                    setVisible(R.id.dialog_top_up_qr_code_layout);
                    getQRCode(dataBean.package_id);
                }
            });
        }

        binding.dialogTopUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTopUpQrcodeLayoutVisible()) {
                    setVisible(R.id.dialog_top_up_detail_layout);
                } else if (isTopUpType() && isTopUpDetailVisible()) {
                    dismiss();
                } else if (!isTopUpType() && isTopUpDetailVisible()) {
                    setVisible(R.id.dialog_pay_order);
                } else {
                    abortPayByCashRequest();
                    dismiss();
                }
            }
        });

        binding.dialogTopUpSuccessLayout.topUpContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisible(R.id.dialog_top_up_detail_layout);
            }
        });
    }

    private boolean isTopUpQrcodeLayoutVisible() {
        return binding.dialogTopUpQrCodeLayout.dialogTopUpQrCode.getVisibility() == View.VISIBLE;
    }

    private boolean isTopUpDetailVisible() {
        return binding.dialogTopUpDetailLayout.dialogTopUpDetail.getVisibility() == View.VISIBLE;
    }

    private boolean isTopUpType() {
        return Constants.PAY_DIALOG_TYPE_TOP_UP == payDialogType;
    }

    private void getQRCode(int packageId) {
        final GetPayQRCodeAction action = new GetPayQRCodeAction(packageId);
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetPayQRCodeBean.DataBean data = action.getData();
                Bitmap qrImage = QRCodeUtil.createQRImage(data.qr_code, ResManager.getDimens(
                        R.dimen.top_up_qr_code_height), ResManager.getDimens(R.dimen.top_up_qr_code_height));
                binding.dialogTopUpQrCodeLayout.topUpQrCode.setImageBitmap(qrImage);
                getRechargePollEvent = new GetRechargePollEvent(data.trade_num);
                getRechargePollEvent.startPoll(DEFAULT_POLL_TIME);
            }
        });
    }

    private void setVisible(int id) {
        binding.dialogTopUpDetailLayout.dialogTopUpDetail.setVisibility(R.id.dialog_top_up_detail_layout == id ? View.VISIBLE : View.GONE);
        binding.dialogTopUpQrCodeLayout.dialogTopUpQrCode.setVisibility(R.id.dialog_top_up_qr_code_layout == id ? View.VISIBLE : View.GONE);
        binding.dialogTopUpSuccessLayout.dialogTopUpSuccess.setVisibility(R.id.dialog_top_up_success_layout == id ? View.VISIBLE : View.GONE);
        binding.payOrder.dialogPayOrder.setVisibility(R.id.dialog_pay_order == id ? View.VISIBLE : View.GONE);
        getPayOrderViewModel().title.set(R.id.dialog_top_up_detail_layout == id ? ResManager.getString(R.string.top_up) : "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRechargePollEvent(GetRechargePollEvent event) {
        final GetRechargeStatusAction action = new GetRechargeStatusAction(event.getOrderId());
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetRechargeStatusBean rechargeStatusBean = action.getRechargeStatusBean();
                if (rechargeStatusBean.data) {
                    getRechargePollEvent.setPollTime(0);
                    setVisible(R.id.dialog_top_up_success_layout);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfirmPayClickEvent(ConfirmPayClickEvent event) {
        if (isBuyNetBook()) {
            if (getPayOrderViewModel().getOrderInfo().need_recharge) {
                gotoTopUpPage();
            } else {
                if (payParamsBean != null) {
                    buyNetBookChapters(payParamsBean);
                }
            }
        } else {
            int checkedRadioButtonId = binding.payOrder.paymentRadioGroup.getCheckedRadioButtonId();
            if (checkedRadioButtonId == R.id.payment_read_bean) {
                if (getPayOrderViewModel().getOrderInfo().need_recharge) {
                    gotoTopUpPage();
                } else {
                    payByReadBean();
                }
            } else {
                payByCash();
            }
        }
    }

    private void gotoTopUpPage() {
        setVisible(R.id.dialog_top_up_detail_layout);
        binding.setOrderModel(getPayOrderViewModel());
        getTopUpValue();
    }

    private void payByCash() {
        String encryptUrl = getPayByCashUrl();
        Bitmap qrImage = QRCodeUtil.createQRImage(encryptUrl, ResManager.getDimens(
                R.dimen.top_up_qr_code_height), ResManager.getDimens(R.dimen.top_up_qr_code_height));
        setPayOrderView(true);
        binding.payOrder.topUpQrCode.setImageBitmap(qrImage);
        payByCashRequest = new RxGetPayResultByCashRequest(getPayOrderViewModel().getOrderInfo().token);
        payByCashRequest.execute(new RxCallback<RxGetPayResultByCashRequest>() {
            @Override
            public void onNext(RxGetPayResultByCashRequest request) {
                if (!request.getAbort() && request.checkResult(request.getOrderStatusBean())) {
                    dismissDialog(new PayByCashSuccessEvent());
                }
            }
        });
    }

    private void setPayOrderView(boolean isPayByCash) {
        binding.payOrder.confirmPay.setVisibility(isPayByCash ? View.GONE : View.VISIBLE);
        binding.payOrder.payByCashView.setVisibility(isPayByCash ? View.VISIBLE : View.GONE);
    }

    private String getPayByCashUrl() {
        BaseShopRequestBean baseShopRequestBean = new BaseShopRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        Map<String, String> queryArgs = new HashMap<>();
        queryArgs.put(CloudApiContext.ReadBean.PAY_TOKEN, getPayOrderViewModel().getOrderInfo().token);
        baseInfo.addRequestParams(queryArgs);
        String signValue = baseInfo.getSignValue(CloudApiContext.ReadBean.PAY_BY_CASH);
        baseInfo.setSign(signValue);
        baseShopRequestBean.setBaseInfo(baseInfo);
        return ViewHelper.getPayByCashUrl(baseInfo.getRequestParamsMap());
    }

    private void payByReadBean() {
        abortPayByCashRequest();
        setPayOrderView(false);
        String token = getPayOrderViewModel().getOrderInfo().token;
        PayByReadBeanAction payByReadBeanAction = new PayByReadBeanAction(token);
        payByReadBeanAction.execute(ShopDataBundle.getInstance(), new RxCallback<PayByReadBeanAction>() {
            @Override
            public void onNext(PayByReadBeanAction action) {
                BaseResultBean resultBean = action.getResultBean();
                checkPayResult(resultBean, false);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    private void checkPayResult(BaseResultBean resultBean, boolean isNetBook) {
        if (resultBean != null) {
            if (BaseResultBean.checkSuccess(resultBean)) {
                if (isNetBook && ((BuyChaptersResultBean) resultBean).data != null) {
                    BuyChaptersResultBean buyChaptersResultBean = (BuyChaptersResultBean) resultBean;
                    ShopDataBundle.getInstance().getBookDetailViewModel().netBookIds.set((ArrayList<String>) buyChaptersResultBean.data.chapter_ids);
                }
                onPaySuccess(isNetBook);
            } else if (resultBean.result_code == Constants.RESULT_PAY_ORDER_INSUFFICIENT_BALANCE) {
                getPayOrderViewModel().getOrderInfo().need_recharge = true;
                changePayButtonState(false);
            } else {
                ToastUtil.showToast(ResManager.getString(R.string.pay_failed));
            }
        }
    }

    private void onPaySuccess(boolean isNetBook) {
        binding.payOrder.paySuccess.setVisibility(View.VISIBLE);
        binding.payOrder.paySuccess.setText(ResManager.getString(R.string.pay_success));
        binding.payOrder.confirmPay.setVisibility(View.GONE);
        dismissDialog(new BuyBookSuccessEvent("", isNetBook));
    }

    private void dismissDialog(final Object event) {
        int delayTime = ResManager.getInteger(R.integer.delay_pay_success_close_pay_dialog);
        Observable<Long> timer = Observable.timer(delayTime, TimeUnit.SECONDS);
        countDownDisposable = timer.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        getPayOrderViewModel().getEventBus().post(event);
                        dismiss();
                    }
                });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (countDownDisposable != null) {
            countDownDisposable.dispose();
        }
        abortPayByCashRequest();
    }

    private void abortPayByCashRequest() {
        if (payByCashRequest != null) {
            payByCashRequest.setAbort(true);
            payByCashRequest = null;
        }
    }
}
