package com.onyx.jdread.shop.request.cloud;


import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.CloudApiContext;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.main.servie.ReadContentService;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDownloadUrlResultBean;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jackdeng on 17-12-29.
 */

public class RxRequestBookDownloadUrl extends RxBaseCloudRequest {
    private BaseRequestBean bookDetailRequestBean;
    private BookDownloadUrlResultBean bookDownloadUrlResultBean;

    public void setBaseRequestBean(BaseRequestBean baseRequestBean) {
        this.bookDetailRequestBean = baseRequestBean;
    }

    public BookDownloadUrlResultBean getBookDownloadUrlResultBean() {
        return bookDownloadUrlResultBean;
    }

    public RxRequestBookDownloadUrl() {

    }

    @Override
    public Object call() throws Exception {
        if (CommonUtils.isNetworkConnected(JDReadApplication.getInstance().getBaseContext())) {
            executeCloudRequest();
        }
        return this;
    }

    private void executeCloudRequest() throws IOException {
        ReadContentService getBookDownloadUrlService = CloudApiContext.getService(CloudApiContext.JD_BOOK_VERIFY_URL);
        Call<BookDownloadUrlResultBean> call = getCall(getBookDownloadUrlService);
        Response<BookDownloadUrlResultBean> response = call.execute();
        if (response != null) {
            bookDownloadUrlResultBean = response.body();
        }
    }

    private Call<BookDownloadUrlResultBean> getCall(ReadContentService getBookDetailService) {
        return getBookDetailService.getBookDownloadUrl(bookDetailRequestBean.getAppBaseInfo().getRequestParamsMap(), CloudApiContext.BookDownloadUrl.GET_CONTENT, bookDetailRequestBean.getBody());
    }
}
