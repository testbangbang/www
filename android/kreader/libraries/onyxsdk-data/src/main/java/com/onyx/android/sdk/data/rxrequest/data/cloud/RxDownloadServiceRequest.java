package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.data.v1.OnyxFileDownloadService;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/14.
 */

public class RxDownloadServiceRequest extends RxBaseCloudRequest {

    private final String url;
    public ResponseBody result;

    public RxDownloadServiceRequest(String url) {
        this.url = url;
    }

    @Override
    public RxDownloadServiceRequest call() throws Exception {
        try {
            OnyxFileDownloadService service = ServiceFactory.getFileDownloadService(Constant.CN_API_BASE);
            Response<ResponseBody> response = service.fileDownload(url).execute();
            if (response != null && response.isSuccessful()) {
                result = response.body();
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return this;
    }

    public ResponseBody getResult() {
        return result;
    }
}