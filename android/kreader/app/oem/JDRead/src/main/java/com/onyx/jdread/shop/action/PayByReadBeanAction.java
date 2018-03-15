package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.cloud.entity.PayCommonRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BaseResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestPayByReadBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackdeng on 2018/1/23.
 */

public class PayByReadBeanAction extends BaseAction {

    private BaseResultBean resultBean;
    private String token;

    public BaseResultBean getResultBean() {
        return resultBean;
    }

    public PayByReadBeanAction(String token) {
        this.token = token;
    }

    @Override
    public void execute(ShopDataBundle dataBundle, final RxCallback rxCallback) {
        PayCommonRequestBean requestBean = new PayCommonRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        Map<String, String> queryArgs = new HashMap<>();
        queryArgs.put(CloudApiContext.ReadBean.PAY_TOKEN, token);
        baseInfo.addRequestParams(queryArgs);
        baseInfo.removeApp();
        baseInfo.setSign(baseInfo.getSignValue(CloudApiContext.ReadBean.PAY_BY_READ_BEAN));
        requestBean.setBaseInfo(baseInfo);
        requestBean.saltValue = PersonalDataBundle.getInstance().getSalt();
        RxRequestPayByReadBean request = new RxRequestPayByReadBean();
        request.setRequestBean(requestBean);
        request.execute(new RxCallback<RxRequestPayByReadBean>() {

            @Override
            public void onNext(RxRequestPayByReadBean request) {
                resultBean = request.getResultBean();
                invokeNext(rxCallback, PayByReadBeanAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                invokeNext(rxCallback, throwable);
            }

            @Override
            public void onFinally() {
                invokeFinally(rxCallback);
            }
        });
    }
}
