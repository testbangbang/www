package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.ConsumeRecordBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetConsumeRecordRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.List;

/**
 * Created by li on 2018/1/26.
 */

public class ConsumeRecordAction extends BaseAction {
    private List<ConsumeRecordBean.DataBean> data;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setPageSize(null, null);
        String signValue = baseInfo.getSignValue(CloudApiContext.ReadBean.CONSUME_RECORD);
        baseInfo.setSign(signValue);

        final RxGetConsumeRecordRequest rq = new RxGetConsumeRecordRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ConsumeRecordBean consumeRecordBean = rq.getConsumeRecordBean();
                if (consumeRecordBean != null) {
                    data = consumeRecordBean.getData();
                }
                if (rxCallback != null) {
                    rxCallback.onNext(ConsumeRecordAction.class);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public List<ConsumeRecordBean.DataBean> getData() {
        return data;
    }
}
