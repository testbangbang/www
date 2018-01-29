package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.cloud.entity.jdbean.VerifySignBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxVerifySignRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

/**
 * Created by li on 2018/1/27.
 */

public class VerifyCheckInAction extends BaseAction {
    private VerifySignBean.DataBean data;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.SIGN_CHECK);
        baseInfo.setSign(signValue);

        final RxVerifySignRequest rq = new RxVerifySignRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                VerifySignBean verifySignBean = rq.getVerifySignBean();
                if (verifySignBean != null) {
                    data = verifySignBean.getData();
                }
                if (rxCallback != null) {
                    rxCallback.onNext(VerifyCheckInAction.class);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public VerifySignBean.DataBean getData() {
        return data;
    }
}
