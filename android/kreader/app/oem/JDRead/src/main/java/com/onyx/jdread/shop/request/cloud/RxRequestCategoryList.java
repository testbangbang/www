package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import retrofit2.Call;

/**
 * Created by hehai on 17-3-30.
 */
public class RxRequestCategoryList extends RxBaseCloudRequest {
    private BaseRequestBean baseRequestBean;
    private CategoryListResultBean categoryListResultBean;

    public CategoryListResultBean getCategoryListResultBean() {
        return categoryListResultBean;
    }

    public void setBaseRequestBean(BaseRequestBean baseRequestBean) {
        this.baseRequestBean = baseRequestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        Call<CategoryListResultBean> call = getCall(getCommonService);
        categoryListResultBean = done(call);
        checkQuestResult();
    }

    private CategoryListResultBean done(Call<CategoryListResultBean> call) {
        EnhancedCall<CategoryListResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, CategoryListResultBean.class);
    }

    private void checkQuestResult() {
        if (categoryListResultBean != null && StringUtils.isNullOrEmpty(categoryListResultBean.code)) {
            switch (categoryListResultBean.code) {
                default:
                    break;
            }
        }
    }

    private Call<CategoryListResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getCategoryList(baseRequestBean.getAppBaseInfo().getRequestParamsMap(),
                CloudApiContext.CategoryList.CATEGORY_LIST,
                baseRequestBean.getBody()
        );
    }
}
