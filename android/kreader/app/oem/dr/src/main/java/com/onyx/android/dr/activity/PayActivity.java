package com.onyx.android.dr.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.OrderPaidEvent;
import com.onyx.android.dr.interfaces.PayActivityView;
import com.onyx.android.dr.presenter.PayPresenter;
import com.onyx.android.dr.service.QueryPayResultService;
import com.onyx.android.dr.util.AppConfig;
import com.onyx.android.dr.util.QRCodeUtil;
import com.onyx.android.sdk.data.model.v2.PayBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;

/**
 * Created by hehai on 17-9-7.
 */

public class PayActivity extends BaseActivity implements PayActivityView {
    @Bind(R.id.pay_qr_code)
    ImageView payQrCode;
    @Bind(R.id.order_number)
    TextView orderNumber;
    @Bind(R.id.order_price)
    TextView orderPrice;
    @Bind(R.id.loading_layout)
    ImageView loadingLayout;
    private float viewWidth = 0.0f;
    private float viewHeight = 0.0f;
    private String orderId;

    private QueryPayResultService.PayResultBinder myBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (QueryPayResultService.PayResultBinder) service;
            myBinder.queryPayResult(orderId);
        }
    };

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_pay;
    }

    @Override
    protected void initConfig() {
        viewWidth = AppConfig.sharedInstance(this).getPayPageViewSizeWidth();
        viewHeight = AppConfig.sharedInstance(this).getPayPageViewSizeHeight();
        setWindowAttributes();
        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getStringExtra(Constants.ORDER_ID);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        PayPresenter payPresenter = new PayPresenter(this);
        payPresenter.pay(orderId);
    }

    public void setWindowAttributes() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.height = (int) (display.getHeight() * viewHeight);
        layoutParams.width = (int) (display.getWidth() * viewWidth);
        getWindow().setAttributes(layoutParams);

        setFinishOnTouchOutside(true);
    }

    @Override
    public void setPayBean(PayBean payBean) {
        Bitmap qrImage = QRCodeUtil.createQRImage(payBean.code_url, getResources().getInteger(R.integer.pay_qr_code_width), getResources().getInteger(R.integer.pay_qr_code_width));
        payQrCode.setImageBitmap(qrImage);
        loadingLayout.setVisibility(View.GONE);
        orderNumber.setText(String.format(getString(R.string.order_number), orderId));
        orderPrice.setText(String.format(getString(R.string.order_price), Float.valueOf(payBean.total)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, QueryPayResultService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        myBinder.setWait(false);
        unbindService(connection);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderPaidEvent(OrderPaidEvent event) {
        finish();
    }
}
