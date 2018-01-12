package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by hehai on 17-3-30.
 */
public class RxRequestCategoryList extends RxBaseCloudRequest {
    private BaseRequestInfo requestBean;
    private CategoryListResultBean categoryListResultBean;

    public CategoryListResultBean getCategoryListResultBean() {
        return categoryListResultBean;
    }

    public void setBaseRequestBean(BaseRequestInfo requestBean) {
        this.requestBean = requestBean;
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
        if (categoryListResultBean != null) {
            List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> adjustLevelTwoList = new ArrayList<>();
            for (int i = 0; i < categoryListResultBean.data.size(); i++) {
                adjustLevelTwoList.clear();
                CategoryListResultBean.CategoryBeanLevelOne categoryBeanLevelOne = categoryListResultBean.data.get(i);
                for (CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBeanLevelTwo : categoryBeanLevelOne.sub_category) {
                    for (CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBeanLevelThree : categoryBeanLevelTwo.sub_category) {
                        adjustLevelTwoList.add(categoryBeanLevelThree);
                    }
                }
                if (adjustLevelTwoList.size() > 0) {
                    categoryBeanLevelOne.sub_category.clear();
                    categoryBeanLevelOne.sub_category.addAll(adjustLevelTwoList);
                }
            }
        }
    }

    private Call<CategoryListResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getCategoryList(requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
