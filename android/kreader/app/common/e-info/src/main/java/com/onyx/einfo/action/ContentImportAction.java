package com.onyx.einfo.action;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentImportFromJsonRequest;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.einfo.R;
import com.onyx.einfo.device.DeviceConfig;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.einfo.manager.ConfigPreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/8/18.
 */
public class ContentImportAction extends BaseAction<LibraryDataHolder> {

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        if (ConfigPreferenceManager.hasImportContent(dataHolder.getContext())) {
            return;
        }
        ConfigPreferenceManager.setImportContent(dataHolder.getContext(), true);
        String jsonFilePath = DeviceConfig.sharedInstance(dataHolder.getContext()).getCloudContentImportJsonFilePath();
        if (StringUtils.isNullOrEmpty(jsonFilePath)) {
            return;
        }
        if (!jsonFilePath.startsWith("/")) {
            File file = new File(EnvironmentUtil.getExternalStorageDirectory(), jsonFilePath);
            jsonFilePath = file.getAbsolutePath();
        }
        FileUtils.ensureFileExists(jsonFilePath);
        List<String> filePathList = new ArrayList<>();
        filePathList.add(jsonFilePath);
        CloudContentImportFromJsonRequest listImportRequest = new CloudContentImportFromJsonRequest(filePathList);
        dataHolder.getCloudManager().submitRequest(dataHolder.getContext(), listImportRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ToastUtils.showToast(request.getContext().getApplicationContext(), e == null ? R.string.cloud_content_import_success :
                        R.string.cloud_content_import_failed);
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
