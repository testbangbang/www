package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.reader.data.ReadingDataResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/2/28.
 */

public class RxSyncReadingDataRequest extends RxBaseCloudRequest {
    private RequestBody requestBody;
    private JDAppBaseInfo baseInfo;
    private ReadingDataResultBean resultBean;

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<ReadingDataResultBean> call = getCall(service);
        Response<ReadingDataResultBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
        return this;
    }

    private Call<ReadingDataResultBean> getCall(ReadContentService service) {
        return service.syncReadingData(baseInfo.getRequestParamsMap(), requestBody);
    }

    public void setRequestData(JDAppBaseInfo baseInfo, RequestBody requestBody) {
        this.baseInfo = baseInfo;
        this.requestBody = requestBody;
    }

    public ReadingDataResultBean getResultBean() {
        return resultBean;
    }
}
