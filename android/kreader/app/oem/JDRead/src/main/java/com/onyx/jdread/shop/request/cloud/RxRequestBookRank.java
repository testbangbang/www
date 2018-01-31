package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class RxRequestBookRank extends RxBaseCloudRequest {

    private BaseRequestInfo requestBean;
    private BookModelConfigResultBean resultBean;
    private List<BookModelConfigResultBean.DataBean.ModulesBean> rankDataList;

    public BookModelConfigResultBean getResultBean() {
        return resultBean;
    }

    public List<BookModelConfigResultBean.DataBean.ModulesBean> getRankDataList() {
        return rankDataList;
    }

    public void setRequestBean(BaseRequestInfo requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
        Call<BookModelConfigResultBean> call = getCall(getCommonService);
        resultBean = done(call);
        checkRequestResult();
    }

    private void checkRequestResult() {
        if (resultBean != null) {
            parseResult();
        }
    }

    private void parseResult() {
        BookModelConfigResultBean.DataBean data = resultBean.data;
        if (rankDataList == null) {
            rankDataList = new ArrayList<>();
        } else {
            rankDataList.clear();
        }
        for (int i = 0; i < data.modules.size(); i++) {
            parseSubjectDataList(data, i);
        }
    }

    public void parseSubjectDataList(BookModelConfigResultBean.DataBean dataBean, int index) {
        if (dataBean.ebook != null && dataBean.modules != null) {
            ArrayList<ResultBookBean> bookList = new ArrayList<>();
            BookModelConfigResultBean.DataBean.ModulesBean modulesBean = dataBean.modules.get(index);
            if (!filterRankList(modulesBean)){
                return;
            }
            List<BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean> items = modulesBean.items;
            for (BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean itemsBean : items) {
                ResultBookBean bookBean = dataBean.ebook.get(itemsBean.id);
                bookList.add(bookBean);
            }
            modulesBean.bookList = bookList;
            if ((index == Constants.SHOP_MAIN_INDEX_ONE || index == Constants.SHOP_MAIN_INDEX_THREE)) {
                if (dataBean.modules.size() - 1 >= (index + 1)) {
                    BookModelConfigResultBean.DataBean.ModulesBean modulesBeanNext = dataBean.modules.get(index + 1);
                    modulesBean.show_name_next = modulesBeanNext.show_name;
                    modulesBean.f_type_next = modulesBeanNext.f_type;
                    modulesBean.id_next = modulesBeanNext.id;
                    modulesBean.showNextTitle = true;
                }
            }
            rankDataList.add(modulesBean);
        }
    }

    private boolean filterRankList(BookModelConfigResultBean.DataBean.ModulesBean modulesBean) {
        int id = modulesBean.id;
        //TODO filter the special ranking list
        return true;
    }

    private BookModelConfigResultBean done(Call<BookModelConfigResultBean> call) {
        EnhancedCall<BookModelConfigResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookModelConfigResultBean.class);
    }

    private Call<BookModelConfigResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getBookRank(requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
