package com.onyx.jdread.personal.dialog;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogTopUpBinding;
import com.onyx.jdread.library.utils.QRCodeUtil;
import com.onyx.jdread.main.common.ResManager;
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
import com.onyx.jdread.shop.view.DividerItemDecoration;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by li on 2017/12/29.
 */

public class TopUpDialog extends DialogFragment {
    private DialogTopUpBinding binding;
    private TopUpAdapter topUpAdapter;
    private GetRechargePollEvent getRechargePollEvent;
    private static final int DEFAULT_POLL_TIME = 300;

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
        getRechargePollEvent.setPollTime(0);
    }

    private void initView() {
        binding.dialogTopUpDetailLayout.dialogTopUpRecycler.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
        DividerItemDecoration decoration = new DividerItemDecoration(JDReadApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        binding.dialogTopUpDetailLayout.dialogTopUpRecycler.addItemDecoration(decoration);
        topUpAdapter = new TopUpAdapter();
        binding.dialogTopUpDetailLayout.dialogTopUpRecycler.setAdapter(topUpAdapter);
    }

    private void initData() {
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
                dismiss();
            }
        });

        binding.dialogTopUpSuccessLayout.topUpContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisible(R.id.dialog_top_up_detail_layout);
            }
        });
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
}
