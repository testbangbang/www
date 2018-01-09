package com.onyx.jdread.shop.action;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.main.common.CloudApiContext;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestCategoryList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/12/15.
 */

public class BookCategoryAction extends BaseAction<ShopDataBundle> {

    private boolean isAllCategory;
    private Context context;
    private BookShopViewModel shopViewModel;
    private List<CategoryListResultBean.CatListBean> catList;

    public BookCategoryAction(Context context, boolean isAllCategory) {
        this.context = context;
        this.isAllCategory = isAllCategory;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
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
                    catList = categoryListResultBean.catList;
                    if (!isAllCategory) {
                        shopViewModel.setCategorySubjectItems(catList);
                    }
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

    public List<CategoryListResultBean.CatListBean> getCatList() {
        return catList;
    }

    public List<CategoryListResultBean.CatListBean> loadCategoryV2(List<CategoryListResultBean.CatListBean> catList, int catId) {
        List<CategoryListResultBean.CatListBean> list = new ArrayList<>();
        if (catList != null) {
            for (CategoryListResultBean.CatListBean catListBean : catList) {
                if (catId == catListBean.catId) {
                    List<CategoryListResultBean.CatListBean.ChildListBean> childList = catListBean.childList;
                    for (CategoryListResultBean.CatListBean.ChildListBean childListBean : childList) {
                        CategoryListResultBean.CatListBean bean = new CategoryListResultBean.CatListBean();
                        bean.amount = childListBean.amount;
                        bean.catId = childListBean.catId;
                        bean.catName = childListBean.catName;
                        bean.catType = childListBean.catType;
                        bean.isLeaf = childListBean.isLeaf;
                        list.add(bean);
                    }
                }
            }
        }
        return list;
    }
}
