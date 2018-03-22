package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.reader.data.UpdateMakerRequestBean;
import com.onyx.jdread.reader.data.UpdateMarkerResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/3/21.
 */

public class RxUpdateMarkerRequest extends RxBaseCloudRequest {
    private UpdateMakerRequestBean requestBean;
    private UpdateMarkerResultBean resultBean;

    public RxUpdateMarkerRequest(UpdateMakerRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<UpdateMarkerResultBean> call = getCall(service);
        Response<UpdateMarkerResultBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
        return this;
    }

    private Call<UpdateMarkerResultBean> getCall(ReadContentService service) {
        return service.updateMaker(requestBean.baseInfo, requestBean.body);
    }

    public UpdateMarkerResultBean getResultBean() {
        return resultBean;
    }
}
