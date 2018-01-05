package com.onyx.jdread.shop.action;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.SubjectViewModel;
import com.onyx.jdread.shop.request.cloud.RxRequestBookModule;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class BookSpecialTodayAction extends BaseAction<ShopDataBundle> {

    private Context context;
    private BookShopViewModel shopViewModel;
    private BookModelResultBean bookModelResultBean;

    public BookModelResultBean getBookModelResultBean() {
        return bookModelResultBean;
    }

    public BookSpecialTodayAction(Context context) {
        this.context = context;
    }

    @Override
    public void execute(ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        shopViewModel = shopDataBundle.getShopViewModel();
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(shopDataBundle.getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.BookShopModule.MODULE_ID, CloudApiContext.BookShopModule.TODAY_SPECIAL_ID);
        body.put(CloudApiContext.BookShopModule.MODULE_TYPE, CloudApiContext.BookShopModule.TODAY_SPECIAL_MODULE_TYPE);
        baseRequestBean.setBody(body.toJSONString());
        RxRequestBookModule request = new RxRequestBookModule();
        request.setBaseRequestBean(baseRequestBean);
        request.execute(new RxCallback<RxRequestBookModule>() {
            @Override
            public void onNext(RxRequestBookModule request) {
                bookModelResultBean = request.getBookModelResultBean();
                SubjectViewModel subjectViewModel = new SubjectViewModel();
                subjectViewModel.setModelBean(bookModelResultBean);
                shopViewModel.setCoverSubjectTwoItems(subjectViewModel);
                if (rxCallback != null) {
                    rxCallback.onNext(BookSpecialTodayAction.this);
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