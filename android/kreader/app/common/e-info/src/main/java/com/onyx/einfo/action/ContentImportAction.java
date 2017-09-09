package com.onyx.einfo.action;

import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentImportFromJsonRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.einfo.R;
import com.onyx.einfo.device.DeviceConfig;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.einfo.manager.ConfigPreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/8/18.
 */
public class ContentImportAction extends BaseAction<LibraryDataHolder> {

    private String jsonFilePath;
    private boolean forceImport = false;

    public ContentImportAction(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    public ContentImportAction(String jsonFilePath, boolean forceImport) {
        this.jsonFilePath = jsonFilePath;
        this.forceImport = forceImport;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        if (!forceImport && ConfigPreferenceManager.hasImportContent(dataHolder.getContext())) {
            return;
        }
        if (StringUtils.isNullOrEmpty(jsonFilePath)) {
            return;
        }
        ConfigPreferenceManager.setImportContent(dataHolder.getContext(), true);
        List<String> filePathList = new ArrayList<>();
        filePathList.add(jsonFilePath);
        CloudContentImportFromJsonRequest listImportRequest = new CloudContentImportFromJsonRequest(filePathList);
        dataHolder.getCloudManager().submitRequest(dataHolder.getContext(), listImportRequest, new BaseCallback() {

            @Override
            public void start(BaseRequest request) {
                BaseCallback.invokeStart(baseCallback, request);
            }

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                BaseCallback.invokeProgress(baseCallback, request, info);
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                Toast.makeText(request.getContext().getApplicationContext(), e == null ?
                        R.string.cloud_content_import_success : R.string.cloud_content_import_failed, Toast.LENGTH_LONG).show();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
