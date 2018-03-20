package com.onyx.jdread.personal.request.cloud;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.reader.data.ReadingData;
import com.onyx.jdread.reader.data.ReadingDataResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import java.util.List;

import okhttp3.MediaType;
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
    private List<ReadingData> list;

    @Override
    public Object call() throws Exception {
        getRequestBody();
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<ReadingDataResultBean> call = getCall(service);
        Response<ReadingDataResultBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
        }
        return this;
    }

    private void getRequestBody() {
        String s = JSON.toJSONString(list);
        MediaType mediaType = MediaType.parse(Constants.PARSE_JSON_TYPE);
        requestBody = RequestBody.create(mediaType, s);
    }

    private Call<ReadingDataResultBean> getCall(ReadContentService service) {
        return service.syncReadingData(baseInfo.getRequestParamsMap(), requestBody);
    }

    public void setRequestData(JDAppBaseInfo baseInfo, List<ReadingData> list) {
        this.baseInfo = baseInfo;
        this.list = list;
    }

    public ReadingDataResultBean getResultBean() {
        return resultBean;
    }
}
