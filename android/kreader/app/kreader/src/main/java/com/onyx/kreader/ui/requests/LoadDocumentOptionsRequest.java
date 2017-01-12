package com.onyx.kreader.ui.requests;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.device.DeviceConfig;

import java.io.File;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class LoadDocumentOptionsRequest extends BaseDataRequest {

    private String documentPath;
    private volatile String md5;
    private Metadata document;

    public LoadDocumentOptionsRequest(final String path, final String md5Value) {
        documentPath = path;
        md5 = md5Value;
    }

    public void execute(final DataManager dataManager) throws Exception {
        initBaseOptions();
        if (StringUtils.isNullOrEmpty(md5)) {
            md5 = FileUtils.computeMD5(new File(documentPath));
        }
        document = DataProviderManager.getDataProvider().loadMetadata(getContext(), documentPath, md5);
        document.setIdString(md5);
    }

    private void initBaseOptions() {
        BaseOptions.setGlobalDefaultGamma(DeviceConfig.sharedInstance(getContext()).getDefaultGamma());
    }

    public final BaseOptions getDocumentOptions() {
        final BaseOptions baseOptions = BaseOptions.optionsFromJSONString(document.getExtraAttributes());
        baseOptions.setMd5(md5);
        if (DeviceConfig.sharedInstance(getContext()).getFixedGamma() > 0) {
            baseOptions.setGamma(DeviceConfig.sharedInstance(getContext()).getFixedGamma());
        }
        return baseOptions;
    }

}
