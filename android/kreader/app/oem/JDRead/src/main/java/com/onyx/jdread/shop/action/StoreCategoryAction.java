package com.onyx.jdread.shop.action;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.BookStoreViewModel;
import com.onyx.jdread.shop.model.StoreDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestCategoryList;

/**
 * Created by jackdeng on 2017/12/15.
 */

public class StoreCategoryAction extends BaseAction<StoreDataBundle> {

    private Context context;
    private BookStoreViewModel storeViewModel;

    public StoreCategoryAction(Context context) {
        this.context = context;
    }

    @Override
    public void execute(StoreDataBundle storeDataBundle, final RxCallback rxCallback) {
        storeViewModel = storeDataBundle.getStoreViewModel();
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.CategoryList.CLIENT_PLATFORM, CloudApiContext.CategoryList.CLIENT_PLATFORM_VALUE);
        baseRequestBean.setBody(body.toJSONString());
        final RxRequestCategoryList request = new RxRequestCategoryList();
        request.setBaseRequestBean(baseRequestBean);
        request.execute(new RxCallback<RxRequestCategoryList>() {
            @Override
            public void onNext(RxRequestCategoryList request) {
                CategoryListResultBean categoryListResultBean = request.getCategoryListResultBean();
                storeViewModel.setCategorySubjectItems(categoryListResultBean.catList);
                if (rxCallback != null) {
                    rxCallback.onNext(request);
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
