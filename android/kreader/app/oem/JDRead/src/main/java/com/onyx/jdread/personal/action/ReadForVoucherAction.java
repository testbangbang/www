package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadForVoucherBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxReadingForVoucherRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

/**
 * Created by li on 2018/1/30.
 */

public class ReadForVoucherAction extends BaseAction {
    private ReadForVoucherBean resultBean;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.READING_VOUCHER);
        baseInfo.setSign(signValue);

        final RxReadingForVoucherRequest rq = new RxReadingForVoucherRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                resultBean = rq.getResultBean();
                if (rxCallback != null) {
                    rxCallback.onNext(ReadForVoucherAction.class);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public ReadForVoucherBean getResultBean() {
        return resultBean;
    }
}