package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.GiftBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetGiftInfoRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

/**
 * Created by li on 2018/2/26.
 */

public class GetGiftAction extends BaseAction {
    private GiftBean giftBean;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setSn();
        baseInfo.setMac();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.USER_GIFT);
        baseInfo.setSign(signValue);
        final RxGetGiftInfoRequest rq = new RxGetGiftInfoRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                giftBean = rq.getGiftBean();
                RxCallback.invokeNext(rxCallback, GetGiftAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public GiftBean getGiftBean() {
        return giftBean;
    }
}
