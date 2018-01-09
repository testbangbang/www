package com.onyx.jdread.shop.request.cloud;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.servie.ReadContentService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryLevel2BooksResultBean;
import com.onyx.jdread.main.common.CloudApiContext;

import retrofit2.Call;

/**
 * Created by hehai on 17-3-30.
 */

public class RxRequestCategoryLevel2Books extends RxBaseCloudRequest {
    private BaseRequestBean baseRequestBean;
    private int sortType;
    private int currentPage;
    private int catId;
    private CategoryLevel2BooksResultBean categoryLevel2BooksResultBean;

    public CategoryLevel2BooksResultBean getCategoryLevel2BooksResultBean() {
        return categoryLevel2BooksResultBean;
    }

    public void setBaseRequestBean(BaseRequestBean baseRequestBean, int currentPage, int catId, int sortType) {
        this.baseRequestBean = baseRequestBean;
        this.currentPage = currentPage;
        this.catId = catId;
        this.sortType = sortType;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        setBaseRequestBeanParams();
        ReadContentService getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        Call<CategoryLevel2BooksResultBean> call = getCall(getCommonService);
        categoryLevel2BooksResultBean = done(call);
        checkQuestResult();
    }

    private void setBaseRequestBeanParams() {
        if (baseRequestBean != null) {
            JSONObject body = new JSONObject();
            body.put(CloudApiContext.CategoryLevel2BookList.SORT_TYPE, sortType);
            body.put(CloudApiContext.CategoryLevel2BookList.PAGE_SIZE, CloudApiContext.CategoryLevel2BookList.PAGE_SIZE_DEFAULT_VALUES);
            body.put(CloudApiContext.CategoryLevel2BookList.CAT_ID, catId);
            body.put(CloudApiContext.CategoryLevel2BookList.CURRENT_PAGE, currentPage);
            body.put(CloudApiContext.CategoryLevel2BookList.SORT_KEY, CloudApiContext.CategoryLevel2BookList.SORT_KEY_DEFAULT_VALUES);
            body.put(CloudApiContext.CategoryLevel2BookList.CLIENT_PLATFORM, CloudApiContext.CategoryLevel2BookList.CLIENT_PLATFORM_DEFAULT_VALUES);
            body.put(CloudApiContext.CategoryLevel2BookList.ROOT_ID, CloudApiContext.CategoryLevel2BookList.ROOT_ID_DEFAULT_VALUES);
            baseRequestBean.setBody(body.toJSONString());
        }
    }

    private CategoryLevel2BooksResultBean done(Call<CategoryLevel2BooksResultBean> call) {
        EnhancedCall<CategoryLevel2BooksResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, CategoryLevel2BooksResultBean.class);
    }

    private void checkQuestResult() {
        if (categoryLevel2BooksResultBean != null && categoryLevel2BooksResultBean.bookList != null) {
        }
    }

    private Call<CategoryLevel2BooksResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getCategoryLevel2BookList(baseRequestBean.getAppBaseInfo().getRequestParamsMap(),
                CloudApiContext.CategoryLevel2BookList.CATEGORY_LEVEL2_BOOK_LIST,
                baseRequestBean.getBody());
    }
}
