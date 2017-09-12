package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.PayData;
import com.onyx.android.dr.interfaces.PayActivityView;
import com.onyx.android.dr.request.cloud.RequestPayForOrder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by hehai on 17-9-7.
 */

public class PayPresenter {
    private PayActivityView payActivityView;
    private PayData payData;

    public PayPresenter(PayActivityView payActivityView) {
        this.payActivityView = payActivityView;
        payData = new PayData();
    }


    public void pay(String orderId) {
        final RequestPayForOrder req = new RequestPayForOrder(orderId);
        payData.pay(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                payActivityView.setPayBean(req.getPayBean());
            }
        });
    }
}
