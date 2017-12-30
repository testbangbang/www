package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.common.CommonUtils;
import com.onyx.jdread.shop.cloud.api.AddBookToSmoothCardBookService;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.AddBookToSmoothCardBookBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 12 on 2017/4/6.
 */

public class RxRequestAddBookToSmoothCard extends RxBaseCloudRequest {
    private AddBookToSmoothCardBookBean addBookToSmoothCardBookBean;
    private BaseRequestBean requestBean;
    private BookDetailResultBean.Detail bookDetailBean;

    public AddBookToSmoothCardBookBean getAddBookToSmoothCardBookBean() {
        return addBookToSmoothCardBookBean;
    }

    public void setRequestBean(BaseRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public RxRequestAddBookToSmoothCard(BookDetailResultBean.Detail bookDetailBean) {
        this.bookDetailBean = bookDetailBean;
    }

    @Override
    public Object call() throws Exception {
        if (CommonUtils.isNetworkConnected(JDReadApplication.getInstance())) {
            executeCloudRequest();
        }
        return this;
    }

    private void executeCloudRequest() throws IOException {
        AddBookToSmoothCardBookService service = init(CloudApiContext.getJdSmoothReadUrl());
        Call<AddBookToSmoothCardBookBean> call = getCall(service);
        Response<AddBookToSmoothCardBookBean> response = call.execute();
        if (response != null) {
            addBookToSmoothCardBookBean = response.body();
        }
    }

    private Call<AddBookToSmoothCardBookBean> getCall(AddBookToSmoothCardBookService service) {
        return service.addBookToSmoothCardBook(CloudApiContext.NewBookDetail.ADD_BOOK_TO_SMOOTH_CARD,
                requestBean.getBody(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    private AddBookToSmoothCardBookService init(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(CloudApiContext.getClient())
                .build();
        return retrofit.create(AddBookToSmoothCardBookService.class);
    }
}
