package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.ShopMainConfigRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookShopMainConfigResultBean;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestShopMainConfig;

/**
 * Created by jackdeng on 2018/1/10.
 */

public class ShopMainConfigAction extends BaseAction {

    private BookShopMainConfigResultBean resultBean;

    public BookShopMainConfigResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(final ShopDataBundle dataBundle, final RxCallback rxCallback) {
        ShopMainConfigRequestBean requestBean = new ShopMainConfigRequestBean();
        JDAppBaseInfo jdAppBaseInfo = JDReadApplication.getInstance().getJDAppBaseInfo();
        jdAppBaseInfo.setTime(String.valueOf(System.currentTimeMillis()));
        requestBean.setAppBaseInfo(jdAppBaseInfo);
        requestBean.setCid(Constants.BOOK_SHOP_DEFAULT_CID);
        RxRequestShopMainConfig request = new RxRequestShopMainConfig();
        request.setRequestBean(requestBean);
        request.execute(new RxCallback<RxRequestShopMainConfig>() {

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
            public void onNext(RxRequestShopMainConfig request) {
                resultBean = request.getResultBean();
                if (rxCallback != null) {
                    rxCallback.onNext(ShopMainConfigAction.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
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
}
