package com.onyx.jdread.shop.action;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestCategoryList;

/**
 * Created by jackdeng on 2017/12/15.
 */

public class BookCategoryAction extends BaseAction<ShopDataBundle> {

    private Context context;
    private BookShopViewModel shopViewModel;

    public BookCategoryAction(Context context) {
        this.context = context;
    }

    @Override
    public void execute(ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        shopViewModel = shopDataBundle.getShopViewModel();
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(shopDataBundle.getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.CategoryList.CLIENT_PLATFORM, CloudApiContext.CategoryList.CLIENT_PLATFORM_VALUE);
        baseRequestBean.setBody(body.toJSONString());
        final RxRequestCategoryList request = new RxRequestCategoryList();
        request.setBaseRequestBean(baseRequestBean);
        request.execute(new RxCallback<RxRequestCategoryList>() {
            @Override
            public void onNext(RxRequestCategoryList request) {
                CategoryListResultBean categoryListResultBean = request.getCategoryListResultBean();
                shopViewModel.setCategorySubjectItems(categoryListResultBean.catList);
                if (rxCallback != null) {
                    rxCallback.onNext(BookCategoryAction.this);
                    rxCallback.onComplete();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (rxCallback != null) {
                    rxCallback.onError(throwable);
                }
            }
        });
    }
}
