package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.CheckGiftBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxCheckGiftRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

/**
 * Created by li on 2018/2/26.
 */

public class CheckGiftAction extends BaseAction {
    private CheckGiftBean.DataBean data;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setMac();
        baseInfo.setSn();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.CHECK_GIFT);
        baseInfo.setSign(signValue);
        final RxCheckGiftRequest rq = new RxCheckGiftRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                CheckGiftBean checkGiftBean = rq.getCheckGiftBean();
                if (checkGiftBean != null) {
                    data = checkGiftBean.data;
                }
                RxCallback.invokeNext(rxCallback, CheckGiftAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public CheckGiftBean.DataBean getData() {
        return data;
    }
}
