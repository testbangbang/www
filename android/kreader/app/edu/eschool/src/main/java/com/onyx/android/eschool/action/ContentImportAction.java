package com.onyx.android.eschool.action;

import android.util.Log;
import android.widget.Toast;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentImportFromJsonRequest;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/8/18.
 */
public class ContentImportAction extends BaseAction<LibraryDataHolder> {

    private String jsonFilePath;
    private boolean supportCFA = true;

    public ContentImportAction(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    public ContentImportAction(String jsonFilePath, boolean supportCFA) {
        this.jsonFilePath = jsonFilePath;
        this.supportCFA = supportCFA;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        if (StringUtils.isNullOrEmpty(jsonFilePath)) {
            return;
        }
        final List<String> filePathList = new ArrayList<>();
        filePathList.add(jsonFilePath);
        CloudContentImportFromJsonRequest listImportRequest = new CloudContentImportFromJsonRequest(filePathList);
        listImportRequest.setSupportCFA(supportCFA);
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
                if (e != null) {
                    Log.e(ContentImportAction.class.getSimpleName(), filePathList.toString(), e);
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
