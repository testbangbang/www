package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetRechargePackageBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxRechargePackageRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.List;

/**
 * Created by li on 2017/12/30.
 */

public class GetTopUpValueAction extends BaseAction {
    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.removeApp();
        String signValue = baseInfo.getSignValue(CloudApiContext.ReadBean.RECHARGE_PACKAGE);
        baseInfo.setSign(signValue);

        final RxRechargePackageRequest rq = new RxRechargePackageRequest();
        rq.setSaltValue("1513304880000");
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetRechargePackageBean resultBean = rq.getResultBean();
                if (resultBean != null) {
                    List<GetRechargePackageBean.DataBean> data = resultBean.data;
                    dataBundle.setTopValueBeans(data);
                }
                if (rxCallback != null) {
                    rxCallback.onNext(GetTopUpValueAction.class);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }
}
