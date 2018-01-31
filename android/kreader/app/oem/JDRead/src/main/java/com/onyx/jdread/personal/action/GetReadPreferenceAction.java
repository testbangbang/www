package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetReadPreferenceBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetReadPreferenceRequest;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.List;

/**
 * Created by li on 2018/1/29.
 */

public class GetReadPreferenceAction extends BaseAction {
    private List<Integer> data;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        BaseShopRequestBean bean = new BaseShopRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.READ_PREFERENCE);
        baseInfo.setSign(signValue);
        bean.setBaseInfo(baseInfo);

        final RxGetReadPreferenceRequest rq = new RxGetReadPreferenceRequest();
        rq.setRequestBean(bean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetReadPreferenceBean resultBean = rq.getResultBean();
                if (resultBean != null) {
                    data = resultBean.data;
                }
                if (rxCallback != null) {
                    rxCallback.onNext(GetReadPreferenceAction.class);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public List<Integer> getData() {
        return data;
    }
}
