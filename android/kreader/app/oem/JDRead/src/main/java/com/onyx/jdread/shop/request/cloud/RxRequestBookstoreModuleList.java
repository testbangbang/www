package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.api.GetBookstoreModuleListService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookstoreModuleListResultBean;
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

public class RxRequestBookstoreModuleList extends RxBaseCloudRequest {
    private BaseRequestBean baseRequestBean;
    private BookstoreModuleListResultBean bookstoreModuleListResultBean;
    private List<ModulesBean> list = new ArrayList<>();

    public BookstoreModuleListResultBean getBookstoreModuleListResultBean() {
        return bookstoreModuleListResultBean;
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
        GetBookstoreModuleListService getCommonService = init(CloudApiContext.getJDBooxBaseUrl());
        Call<BookstoreModuleListResultBean> call = getCall(getCommonService);
        bookstoreModuleListResultBean = done(call);
        checkQuestResult();

    }

    private BookstoreModuleListResultBean done(Call<BookstoreModuleListResultBean> call) {
        EnhancedCall<BookstoreModuleListResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookstoreModuleListResultBean.class);
    }

    private void checkQuestResult() {
        list.clear();
        if (bookstoreModuleListResultBean != null && bookstoreModuleListResultBean.mainThemeList != null) {
            for (BookstoreModuleListResultBean.MainThemeListBean mainThemeListBean : bookstoreModuleListResultBean.mainThemeList) {
                if (mainThemeListBean.modules != null) {
                    for (ModulesBean modulesBean : mainThemeListBean.modules) {
                        list.add(modulesBean);
                    }
                }
            }
        }
    }

    private Call<BookstoreModuleListResultBean> getCall(GetBookstoreModuleListService getCommonService) {
        return getCommonService.getBookstoreModuleList(baseRequestBean.getAppBaseInfo().getRequestParamsMap(),
                CloudApiContext.BookstoreModuleList.API_GET_MAIN_THEME_INFO,
                baseRequestBean.getBody()
        );
    }

    private GetBookstoreModuleListService init(String URL) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(EnhancedCall.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GetBookstoreModuleListService.class);
    }
}
