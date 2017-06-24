package com.onyx.edu.reader.ui.actions;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.request.cloud.v2.CloudIndexServiceRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/6/24.
 */

public class CloudConfInitAction extends BaseAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final CloudIndexServiceRequest indexServiceRequest = new CloudIndexServiceRequest(createIndexService(readerDataHolder.getContext()));
        readerDataHolder.getCloudManager().submitRequest(readerDataHolder.getContext(), indexServiceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || !IndexService.hasValidServer(indexServiceRequest.getResultIndexService())) {
                    useLocalServerCloudConf(readerDataHolder.getContext(), readerDataHolder.getCloudManager());
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public static void useLocalServerCloudConf(Context context, CloudManager cloudManager) {
        cloudManager.setAllCloudConf(CloudConf.create(Constant.ONYX_HOST_BASE,
                Constant.ONYX_API_BASE,
                Constant.DEFAULT_CLOUD_STORAGE));
        cloudManager.setCloudDataProvider(cloudManager.getCloudConf());
    }

    private IndexService createIndexService(Context context) {
        IndexService authService = new IndexService();
        authService.mac = NetworkUtil.getMacAddress(context);
        return authService;
    }
}
