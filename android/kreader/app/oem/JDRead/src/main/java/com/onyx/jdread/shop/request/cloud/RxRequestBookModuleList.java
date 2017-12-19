package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.api.GetBookModuleListService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModuleListResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ModulesBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class RxRequestBookModuleList extends RxBaseCloudRequest {
    private BaseRequestBean baseRequestBean;
    private BookModuleListResultBean bookModuleListResultBean;
    private List<ModulesBean> list = new ArrayList<>();

    public BookModuleListResultBean getBookModuleListResultBean() {
        return bookModuleListResultBean;
    }

    public void setBaseRequestBean(BaseRequestBean baseRequestBean) {
        this.baseRequestBean = baseRequestBean;
    }

    public List<ModulesBean> getList() {
        return list;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        GetBookModuleListService getCommonService = init(CloudApiContext.getJDBooxBaseUrl());
        Call<BookModuleListResultBean> call = getCall(getCommonService);
        bookModuleListResultBean = done(call);
        checkQuestResult();

    }

    private BookModuleListResultBean done(Call<BookModuleListResultBean> call) {
        EnhancedCall<BookModuleListResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookModuleListResultBean.class);
    }

    private void checkQuestResult() {
        list.clear();
        if (bookModuleListResultBean != null && bookModuleListResultBean.mainThemeList != null) {
            for (BookModuleListResultBean.MainThemeListBean mainThemeListBean : bookModuleListResultBean.mainThemeList) {
                if (mainThemeListBean.modules != null) {
                    for (ModulesBean modulesBean : mainThemeListBean.modules) {
                        list.add(modulesBean);
                    }
                }
            }
        }
    }

    private Call<BookModuleListResultBean> getCall(GetBookModuleListService getCommonService) {
        return getCommonService.getBookShopModuleList(baseRequestBean.getAppBaseInfo().getRequestParamsMap(),
                CloudApiContext.BookShopModuleList.API_GET_MAIN_THEME_INFO,
                baseRequestBean.getBody()
        );
    }

    private GetBookModuleListService init(String URL) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(EnhancedCall.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GetBookModuleListService.class);
    }
}
