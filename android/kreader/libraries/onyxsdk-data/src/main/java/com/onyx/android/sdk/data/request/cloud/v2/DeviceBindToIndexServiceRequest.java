package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.BindServer;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/8/18.
 */
public class DeviceBindToIndexServiceRequest extends BaseCloudRequest {

    private String indexServiceApi;
    private BindServer bindServer;

    private boolean result;

    public DeviceBindToIndexServiceRequest(String indexServiceApi, BindServer bindServer) {
        this.indexServiceApi = indexServiceApi;
        setBindServer(bindServer);
    }

    public void setBindServer(BindServer bindServer) {
        this.bindServer = bindServer;
    }

    public boolean isSuccessful() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<ResponseBody> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(indexServiceApi)
                .deviceBindToIndexService(bindServer));
        result = response.isSuccessful();
    }
}
