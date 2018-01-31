package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetRechargeStatusBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetRechargeStatusRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by li on 2018/1/31.
 */

public class GetRechargeStatusAction extends BaseAction {
    private String orderId;
    private GetRechargeStatusBean rechargeStatusBean;

    public GetRechargeStatusAction(String orderId) {
        this.orderId = orderId;
    }
    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        Map<String, String> map = new HashMap<>();
        map.put("order_id", orderId);
        baseInfo.getRequestParamsMap().putAll(map);
        String signValue = baseInfo.getSignValue(CloudApiContext.ReadBean.RECHARGE_STATUS);
        baseInfo.setSign(signValue);

        final RxGetRechargeStatusRequest rq = new RxGetRechargeStatusRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                rechargeStatusBean = rq.getRechargeStatusBean();
                if (rxCallback != null) {
                    rxCallback.onNext(GetRechargeStatusAction.class);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public GetRechargeStatusBean getRechargeStatusBean() {
        return rechargeStatusBean;
    }
}
