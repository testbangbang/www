package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.personal.cloud.entity.GetReadInfoRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadTotalInfoBean;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/12/29.
 */

public class RxGetReadTotalRequest extends RxBaseCloudRequest {
    private GetReadInfoRequestBean requestBean;
    private ReadTotalInfoBean readTotalInfoBean;

    public void setRequestBean(GetReadInfoRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public ReadTotalInfoBean getReadTotalInfoBean() {
        return readTotalInfoBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.getJdBaseUrl());
        Call<ReadTotalInfoBean> call = getCall(service);
        Response<ReadTotalInfoBean> response = call.execute();
        if (response.isSuccessful()) {
            readTotalInfoBean = response.body();
        }
        return this;
    }

    private Call<ReadTotalInfoBean> getCall(ReadContentService service) {
        return service.getReadTotalBook(CloudApiContext.NewBookDetail.READ_TOTAL_BOOK,
                requestBean.getUserName(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
