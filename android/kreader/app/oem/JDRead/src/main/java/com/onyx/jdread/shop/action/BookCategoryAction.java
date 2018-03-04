package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.cloud.entity.BaseRequestInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestCategoryList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/12/15.
 */

public class BookCategoryAction extends BaseAction<ShopDataBundle> {

    private List<CategoryListResultBean.CategoryBeanLevelOne> levelOneData;

    public BookCategoryAction() {

    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        BaseRequestInfo baseRequestBean = new BaseRequestInfo();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        appBaseInfo.setSign(appBaseInfo.getSignValue(CloudApiContext.BookShopURI.CATEGORY_URI));
        baseRequestBean.setAppBaseInfo(appBaseInfo);
        final RxRequestCategoryList request = new RxRequestCategoryList();
        request.setBaseRequestBean(baseRequestBean);
        request.execute(new RxCallback<RxRequestCategoryList>() {

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
            public void onNext(RxRequestCategoryList request) {
                CategoryListResultBean categoryListResultBean = request.getCategoryListResultBean();
                if (categoryListResultBean != null) {
                    levelOneData = categoryListResultBean.data;
                }
                if (rxCallback != null) {
                    rxCallback.onNext(BookCategoryAction.this);
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

    public List<CategoryListResultBean.CategoryBeanLevelOne> getCategoryBeanLevelOneList() {
        return levelOneData == null ? new ArrayList<CategoryListResultBean.CategoryBeanLevelOne>() : levelOneData;
    }

    public List<String> getTitleList() {
        List<String> titleList = new ArrayList<>();
        String[] names = ResManager.getStringArray(R.array.all_category_level_one_name);
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                titleList.add(names[i]);
            }
        }
        return titleList;
    }
}
