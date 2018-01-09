package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestBookModule;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class BookModelAction extends BaseAction<ShopDataBundle> {

    private int modelId;
    private int modelType;
    private BookModelResultBean bookModelResultBean;

    public BookModelResultBean getBookModelResultBean() {
        return bookModelResultBean;
    }

    public BookModelAction(int modelId, int modelType) {
        this.modelId = modelId;
        this.modelType = modelType;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        shopViewModel = shopDataBundle.getShopViewModel();
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(shopDataBundle.getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.BookShopModule.MODULE_ID, modelId);
        body.put(CloudApiContext.BookShopModule.MODULE_TYPE, modelType);
        baseRequestBean.setBody(body.toJSONString());
        RxRequestBookModule request = new RxRequestBookModule();
        request.setBaseRequestBean(baseRequestBean);
        request.execute(new RxCallback<RxRequestBookModule>() {

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
            public void onNext(RxRequestBookModule request) {
                bookModelResultBean = request.getBookModelResultBean();
                if (rxCallback != null) {
                    rxCallback.onNext(BookModelAction.this);
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
