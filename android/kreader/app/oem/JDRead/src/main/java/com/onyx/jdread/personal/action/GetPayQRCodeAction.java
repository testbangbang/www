package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetPayQRCodeBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetPayQRCodeRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by li on 2018/1/31.
 */

public class GetPayQRCodeAction extends BaseAction {
    private int packageId;
    private GetPayQRCodeBean.DataBean data;

    public GetPayQRCodeAction(int packageId) {
        this.packageId = packageId;
    }
    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.removeApp();
        Map<String, String> map = new HashMap<>();
        map.put("package_id", String.valueOf(packageId));
        baseInfo.getRequestParamsMap().putAll(map);
        String signValue = baseInfo.getSignValue(CloudApiContext.ReadBean.RECHARGE);
        baseInfo.setSign(signValue);

        final RxGetPayQRCodeRequest rq = new RxGetPayQRCodeRequest();
        rq.setSaltValue(dataBundle.getSalt());
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetPayQRCodeBean qrCodeBean = rq.getQrCodeBean();
                if (qrCodeBean != null && qrCodeBean.data != null) {
                    data = qrCodeBean.data;
                }
                if (rxCallback != null) {
                    rxCallback.onNext(GetPayQRCodeAction.class);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public GetPayQRCodeBean.DataBean getData() {
        return data;
    }
}
