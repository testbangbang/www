package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.provider.SystemConfigProvider;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowManager;

import retrofit2.Response;

import static com.onyx.android.sdk.data.provider.SystemConfigProvider.KEY_CONTENT_SERVER_INFO;

/**
 * Created by suicheng on 2017/6/16.
 */

public class CloudIndexServiceRequest extends BaseCloudRequest {

    private volatile String apiBase;
    private IndexService requestService;
    private IndexService resultService;
    private int localRetryCount = 1;

    public CloudIndexServiceRequest(final String base, IndexService service) {
        requestService = service;
        apiBase = base;
    }

    public IndexService getResultIndexService() {
        return resultService;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        resultService = loadContentServiceInfoFromLocal(getContext(), localRetryCount);
        IndexService cloudService = loadContentServiceInfoFromCloud(parent);
        if (cloudService != null) {
            if (!cloudService.equals(resultService)) {
                onContentServerChanged(parent, cloudService);
            }
        }

        if (resultService != null && resultService.server != null) {
            String host = resultService.server.getServerHost();
            String api = resultService.server.getApiBase();
            parent.setAllCloudConf(CloudConf.create(host,
                    api, Constant.DEFAULT_CLOUD_STORAGE));
            parent.setCloudDataProvider(parent.getCloudConf());
        }
    }

    private void clearAccountDb() {
        try {
            FlowManager.getContext().getContentResolver().delete(EduAccountProvider.CONTENT_URI, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onContentServerChanged(CloudManager parent, IndexService newIndexService) {
        clearAccountDb();
        parent.setToken(null);
        saveContentServiceInfo(getContext(), resultService = newIndexService);
    }

    private IndexService loadContentServiceInfoFromLocal(Context context, int retryCount) {
        IndexService service = null;
        for (int i = 0; i < retryCount; i++) {
            service = loadContentServiceInfoFromLocalImpl(context);
            if (service != null) {
                break;
            }
            if (retryCount > 1) {
                TestUtils.sleep(300);
            }
        }
        return service;
    }

    private IndexService loadContentServiceInfoFromLocalImpl(Context context) {
        final String value = SystemConfigProvider.getStringValue(context, KEY_CONTENT_SERVER_INFO);
        return JSONObjectParseUtils.parseObject(value, IndexService.class);
    }

    private IndexService loadContentServiceInfoFromCloud(CloudManager parent) throws Exception {
        try {
            Response<IndexService> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(apiBase)
                    .getIndexService(requestService.mac, requestService.installationId));
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (Exception e) {
            if (ContentException.isCloudException(e)) {
                processCloudException(parent, (ContentException) e);
            }
        }
        return null;
    }

    private void processCloudException(CloudManager parent, ContentException cloudException) {
        cloudException.printStackTrace();
        if (cloudException.isCloudNotFound()) {
            onContentServerChanged(parent, null);
        }
    }

    private void saveContentServiceInfo(Context context, IndexService contentService) {
        try {
            SystemConfigProvider.setStringValue(context, KEY_CONTENT_SERVER_INFO, JSONObjectParseUtils.toJson(contentService));
        } catch (Exception e) {
        }
    }

    public void setLocalLoadRetryCount(int retryCount) {
        if (retryCount <= 0) {
            return;
        }
        this.localRetryCount = retryCount;
    }
}
