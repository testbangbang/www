package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.GetBookDetailRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import java.util.List;

import retrofit2.Call;

/**
 * Created by huxiaomao on 2016/12/19.
 */

public class RxRequestBookDetail extends RxBaseCloudRequest {
    private GetBookDetailRequestBean requestBean;
    private BookDetailResultBean bookDetailResultBean;

    public BookDetailResultBean getBookDetailResultBean() {
        return bookDetailResultBean;
    }

    public void setRequestBean(GetBookDetailRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService;
        if (requestBean.withCookie) {
            getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        } else {
            getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
        }
        Call<BookDetailResultBean> call = getCall(getCommonService);
        bookDetailResultBean = done(call);
        getCategoryPath(bookDetailResultBean);
        checkResult();
    }

    private void checkResult() {
        if (bookDetailResultBean != null && bookDetailResultBean.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(bookDetailResultBean.message));
        }
    }

    private BookDetailResultBean done(Call<BookDetailResultBean> call) {
        EnhancedCall<BookDetailResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookDetailResultBean.class);
    }

    private Call<BookDetailResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getBookDetail(requestBean.bookId, requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    public void getCategoryPath(BookDetailResultBean bookDetailResultBean) {
        try{
            if (bookDetailResultBean != null && bookDetailResultBean.data != null) {
                BookDetailResultBean.DetailBean data = bookDetailResultBean.data;
                List<CategoryListResultBean.CategoryBeanLevelOne> levelOneData = ShopDataBundle.getInstance().getShopViewModel().getAllCategoryViewModel().getLevelOneData();
                if (!CollectionUtils.isNullOrEmpty(levelOneData)) {
                    for (CategoryListResultBean.CategoryBeanLevelOne levelOne:levelOneData) {
                        for (CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo levelTwo : levelOne.sub_category) {
                            if (levelTwo.id == data.second_catid1) {
                                data.second_catid1_str = levelTwo.name;
                                data.second_cat_level = levelTwo.level;
                            }
                            for (CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo levelThree : levelTwo.sub_category) {
                                if (levelThree.id == data.third_catid1) {
                                    data.third_catid1_str = levelThree.name;
                                    data.third_cat_level = levelThree.level;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
