package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.CloudApiContext;
import com.onyx.jdread.main.servie.ReadContentService;
import com.onyx.jdread.personal.cloud.entity.GetReadInfoRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadOverInfoBean;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/2.
 */

public class RxGetReadOverRequest extends RxBaseCloudRequest {
    private GetReadInfoRequestBean requestBean;
    private ReadOverInfoBean readOverInfoBean;

    public void setRequestBean(GetReadInfoRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public ReadOverInfoBean getReadOverInfoBean() {
        return readOverInfoBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_STATISTIC_URL);
        Call<ReadOverInfoBean> call = getCall(service);
        Response<ReadOverInfoBean> response = call.execute();
        if (response.isSuccessful()) {
            readOverInfoBean = response.body();
        }
        return this;
    }

    private Call<ReadOverInfoBean> getCall(ReadContentService service) {
        return service.getReadOverBook(requestBean.getUserName(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
