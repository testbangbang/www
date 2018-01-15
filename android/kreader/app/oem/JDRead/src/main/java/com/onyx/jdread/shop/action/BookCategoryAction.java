package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BaseRequestInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestCategoryList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/12/15.
 */

public class BookCategoryAction extends BaseAction<ShopDataBundle> {

    private boolean isMainCategory;
    private BookShopViewModel shopViewModel;
    private List<CategoryListResultBean.CategoryBeanLevelOne> levelOneData;

    public BookCategoryAction(boolean isMainCategory) {
        this.isMainCategory = isMainCategory;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        shopViewModel = shopDataBundle.getShopViewModel();
        BaseRequestInfo baseRequestBean = new BaseRequestInfo();
        JDAppBaseInfo jdAppBaseInfo = JDReadApplication.getInstance().getJDAppBaseInfo();
        jdAppBaseInfo.setTime();
        baseRequestBean.setAppBaseInfo(jdAppBaseInfo);
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
                if (levelOneData != null) {
                    if (isMainCategory) {
                        shopViewModel.setCategorySubjectItems(levelOneData.get(0).sub_category);
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

    public List<CategoryListResultBean.CategoryBeanLevelOne> getCategoryBeanLevelOneList() {
        return levelOneData == null ? new ArrayList<CategoryListResultBean.CategoryBeanLevelOne>() : levelOneData;
    }

    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> getCateTwo(){
        List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> cateTwoList = new ArrayList<>();
        for (CategoryListResultBean.CategoryBeanLevelOne cateOne : getCategoryBeanLevelOneList()){
            CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBeanLevelTwo = new CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo();
            categoryBeanLevelTwo.cateLevel = Constants.CATEGORY_LEVEL_ONE;
            categoryBeanLevelTwo.id = cateOne.id;
            categoryBeanLevelTwo.image_url = cateOne.image_url;
            categoryBeanLevelTwo.name = cateOne.name;
            categoryBeanLevelTwo.sub_category = cateOne.sub_category;
            cateTwoList.add(categoryBeanLevelTwo);
        }
        return cateTwoList;
    }

    public List<String> getTitleList() {
        List<String> titleList = new ArrayList<>();
        for (CategoryListResultBean.CategoryBeanLevelOne categoryOne : getCategoryBeanLevelOneList()) {
            titleList.add(categoryOne.name);
        }
        return titleList;
    }
}
