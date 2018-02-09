package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

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
        ReadContentService getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
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
            if (categoryListResultBean.result_Code == 0) {
                List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> adjustLevelTwoList = new ArrayList<>();
                for (int i = 0; i < categoryListResultBean.data.size(); i++) {
                    adjustLevelTwoList.clear();
                    CategoryListResultBean.CategoryBeanLevelOne categoryBeanLevelOne = categoryListResultBean.data.get(i);
                    categoryBeanLevelOne.cateLevel = Constants.CATEGORY_LEVEL_ONE;
                    for (CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBeanLevelTwo : categoryBeanLevelOne.sub_category) {
                        categoryBeanLevelTwo.cateLevel = Constants.CATEGORY_LEVEL_TWO;
                        for (CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBeanLevelThree : categoryBeanLevelTwo.sub_category) {
                            categoryBeanLevelThree.cateLevel = Constants.CATEGORY_LEVEL_TWO;
                            adjustLevelTwoList.add(categoryBeanLevelThree);
                        }
                    }
                    if (adjustLevelTwoList.size() > 0) {
                        categoryBeanLevelOne.sub_category.clear();
                        categoryBeanLevelOne.sub_category.addAll(adjustLevelTwoList);
                    }
                }
                getCateTwo(categoryListResultBean.data);
            } else {
                ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(categoryListResultBean.message));
            }
        }
    }

    private Call<CategoryListResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getCategoryList(requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    public void getCateTwo(List<CategoryListResultBean.CategoryBeanLevelOne> data) {
        List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> cateTwoList = new ArrayList<>();
        for (CategoryListResultBean.CategoryBeanLevelOne cateOne : data) {
            CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBeanLevelTwo = new CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo();
            categoryBeanLevelTwo.cateLevel = Constants.CATEGORY_LEVEL_ONE;
            categoryBeanLevelTwo.id = cateOne.id;
            categoryBeanLevelTwo.image_url = cateOne.image_url;
            categoryBeanLevelTwo.name = cateOne.name;
            categoryBeanLevelTwo.sub_category = cateOne.sub_category;
            cateTwoList.add(categoryBeanLevelTwo);
        }
        ShopDataBundle.getInstance().setCategoryBean(cateTwoList);
    }
}
