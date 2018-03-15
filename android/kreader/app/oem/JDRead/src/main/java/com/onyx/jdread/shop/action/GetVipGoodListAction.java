package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.shop.cloud.entity.GetVipGoodListRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetVipGoodsListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestGetVipGoodList;

/**
 * Created by jackdeng on 2018/2/3.
 */

public class GetVipGoodListAction extends BaseAction<ShopDataBundle> {

    private GetVipGoodsListResultBean resultBean;

    public GetVipGoodsListResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        GetVipGoodListRequestBean requestBean = new GetVipGoodListRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        appBaseInfo.setSign(appBaseInfo.getSignValue(CloudApiContext.BookShopURI.GET_VIP_GOOD_LIST));
        requestBean.setAppBaseInfo(appBaseInfo);
        requestBean.withCookie = JDReadApplication.getInstance().getLogin();
        RxRequestGetVipGoodList request = new RxRequestGetVipGoodList();
        request.setRequestBean(requestBean);
        request.execute(new RxCallback<RxRequestGetVipGoodList>() {

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showLoadingDialog(shopDataBundle, R.string.loading);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                hideLoadingDialog(shopDataBundle);
            }

            @Override
            public void onNext(RxRequestGetVipGoodList request) {
                resultBean = request.getResultBean();
                setResult(shopDataBundle);
                if (rxCallback != null) {
                    rxCallback.onNext(GetVipGoodListAction.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), shopDataBundle.getEventBus());
                if (rxCallback != null) {
                    rxCallback.onError(throwable);
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                if (rxCallback != null) {
                    rxCallback.onComplete();
                }
            }
        });
    }

    private void setResult(ShopDataBundle shopDataBundle) {
        if (resultBean != null) {
            shopDataBundle.getBuyReadVipModel().setGoodsList(resultBean.data);
        }
    }
}
