package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetOrderInfoResultBean;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestGetOrderInfo;

/**
 * Created by jackdeng on 2018/2/6.
 */

public class GetOrderInfoAction extends BaseAction {

    private String[] bookIds;
    private GetOrderInfoResultBean.DataBean dataBean;

    public GetOrderInfoAction(String[] bookIds) {
        this.bookIds = bookIds;
    }

    public GetOrderInfoResultBean.DataBean getDataBean() {
        return dataBean;
    }

    @Override
    public void execute(final ShopDataBundle dataBundle, final RxCallback rxCallback) {
        final RxRequestGetOrderInfo rq = new RxRequestGetOrderInfo();
        rq.setBookIds(bookIds);
        rq.setSaltValue(PersonalDataBundle.getInstance().getSalt());
        rq.execute(new RxCallback<RxRequestGetOrderInfo>() {

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showLoadingDialog(dataBundle, R.string.loading);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                hideLoadingDialog(dataBundle);
            }

            @Override
            public void onNext(RxRequestGetOrderInfo rq) {
                GetOrderInfoResultBean resultBean = rq.getResultBean();
                if (resultBean != null && resultBean.isSucceed()) {
                    dataBean = resultBean.data;
                }
                invokeNext(rxCallback, GetOrderInfoAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                invokeError(rxCallback, throwable);
            }
        });
    }
}
