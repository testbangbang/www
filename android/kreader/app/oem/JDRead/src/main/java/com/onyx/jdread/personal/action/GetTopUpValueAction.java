package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.cloud.entity.jdbean.TopUpValueBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetTopUpValueRequest;

import java.util.List;

/**
 * Created by li on 2017/12/30.
 */

public class GetTopUpValueAction extends BaseAction {
    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        final RxGetTopUpValueRequest rq = new RxGetTopUpValueRequest();
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<TopUpValueBean> topUpValueBeans = rq.getTopUpValueBeans();
                if (topUpValueBeans != null && topUpValueBeans.size() > 0) {
                    dataBundle.setTopValueBeans(topUpValueBeans);
                    if (rxCallback != null) {
                        rxCallback.onNext(GetTopUpValueAction.class);
                    }
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
