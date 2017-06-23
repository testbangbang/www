package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.provider.SystemConfigProvider;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.TestUtils;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/6/16.
 */

public class CloudIndexServiceRequest extends BaseCloudRequest {
    private static final String TAG = "CloudIndexServerRequest";
    public static final String KEY_CENTRAL_INDEX_AUTH_SERVER = "sys.central_index_auth_server";

    private IndexService requestService;
    private IndexService resultService;
    private int localRetryCount = 1;

    public CloudIndexServiceRequest(IndexService service) {
        this.requestService = service;
    }

    public IndexService getResultIndexService() {
        return resultService;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        resultService = loadAuthServiceFromLocal(getContext(), localRetryCount);
        IndexService cloudService = loadAuthServiceFromCloud(parent);
        if (cloudService != null) {
            resultService = cloudService;
            saveAuthServiceToDb(getContext(), cloudService);
        }
        if (resultService != null && resultService.server != null) {
            String host = resultService.server.createServerHost();
            parent.setAllCloudConf(CloudConf.create(host,
                    host + "api/", Constant.DEFAULT_CLOUD_STORAGE));
            parent.setCloudDataProvider(parent.getCloudConf());
        }
    }

    private IndexService loadAuthServiceFromLocal(Context context, int retryCount) {
        IndexService service = null;
        for (int i = 0; i < retryCount; i++) {
            Log.w(TAG, "localLoadRetry:" + i);
            service = loadAuthServiceFromLocal(context);
            if (service != null) {
                break;
            }
            TestUtils.sleep(300);
        }
        return service;
    }

    private IndexService loadAuthServiceFromLocal(Context context) {
        return JSONObjectParseUtils.parseObject(getKeyCentralIndexAuthServer(context),
                IndexService.class);
    }

    private IndexService loadAuthServiceFromCloud(CloudManager parent) {
        try {
            Response<IndexService> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                    .getIndexService(requestService.mac, requestService.installationId));
            if (response.isSuccessful()) {
                return response.body();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveAuthServiceToDb(Context context, IndexService authService) {
        setKeyCentralIndexAuthServer(context, JSON.toJSONString(authService));
    }

    private String getKeyCentralIndexAuthServer(Context context) {
        return SystemConfigProvider.getStringValue(context, KEY_CENTRAL_INDEX_AUTH_SERVER);
    }

    private boolean setKeyCentralIndexAuthServer(Context context, String value) {
        return SystemConfigProvider.setStringValue(context, KEY_CENTRAL_INDEX_AUTH_SERVER, value);
    }

    public void setLocalLoadRetryCount(int retryCount) {
        if (retryCount <= 0) {
            return;
        }
        this.localRetryCount = retryCount;
    }
}
