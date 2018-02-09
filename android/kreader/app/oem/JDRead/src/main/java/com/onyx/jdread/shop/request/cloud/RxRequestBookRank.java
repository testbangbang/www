package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.BaseRequestInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.BaseSubjectViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.SubjectViewModel;
import com.onyx.jdread.shop.utils.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class RxRequestBookRank extends RxBaseCloudRequest {

    private BaseRequestInfo requestBean;
    private BookModelConfigResultBean resultBean;
    private List<BaseSubjectViewModel> rankDataList;

    public BookModelConfigResultBean getResultBean() {
        return resultBean;
    }

    public List<BaseSubjectViewModel> getRankDataList() {
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
            if (resultBean.result_code != 0) {
                ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
            } else {
                parseResult();
            }
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

        int totalPage = ViewHelper.calculateTotalPages(rankDataList, Constants.COMMOM_SUBJECT_RECYCLE_HEIGHT);
        ShopDataBundle.getInstance().getRankViewModel().setTotalPages(Math.max(totalPage, 1));
    }

    public void parseSubjectDataList(BookModelConfigResultBean.DataBean dataBean, int index) {
        if (dataBean.ebook != null && dataBean.modules != null) {
            ArrayList<ResultBookBean> bookList = new ArrayList<>();
            BookModelConfigResultBean.DataBean.ModulesBean modulesBean = dataBean.modules.get(index);
            List<BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean> items = modulesBean.items;
            for (BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean itemsBean : items) {
                ResultBookBean bookBean = dataBean.ebook.get(itemsBean.id);
                bookList.add(bookBean);
            }
            modulesBean.bookList = bookList;
            SubjectViewModel viewModel = new SubjectViewModel(ShopDataBundle.getInstance().getEventBus());
            viewModel.setModelBean(modulesBean);
            rankDataList.add(viewModel);
        }
    }

    private BookModelConfigResultBean done(Call<BookModelConfigResultBean> call) {
        EnhancedCall<BookModelConfigResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookModelConfigResultBean.class);
    }

    private Call<BookModelConfigResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getBookRank(requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
