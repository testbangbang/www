package com.onyx.android.dr.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.event.OrderPaidEvent;
import com.onyx.android.dr.request.cloud.RequestGetOrder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-9-8.
 */

public class QueryPayResultService extends Service {
    private PayResultBinder binder = new PayResultBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class PayResultBinder extends Binder {
        private boolean wait = true;

        public void queryPayResult(final String orderId) {
            final RequestGetOrder req = new RequestGetOrder(orderId);
            DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    if (req.getOrder() != null && req.getOrder().status == 1) {
                        EventBus.getDefault().post(new OrderPaidEvent());
                    } else if (wait) {
                        queryPayResult(orderId);
                    }
                }
            });
        }

        public void setWait(boolean wait) {
            this.wait = wait;
        }
    }
}
