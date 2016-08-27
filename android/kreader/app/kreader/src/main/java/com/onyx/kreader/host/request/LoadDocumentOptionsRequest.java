package com.onyx.kreader.host.request;

import com.onyx.android.sdk.dataprovider.Metadata;
import com.onyx.android.sdk.dataprovider.DataProviderManager;
import com.onyx.android.sdk.dataprovider.request.BaseDataProviderRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.host.options.BaseOptions;

import java.io.File;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class LoadDocumentOptionsRequest extends BaseDataProviderRequest {

    private String documentPath;
    private volatile String md5;
    private Metadata document;

    public LoadDocumentOptionsRequest(final String path, final String md5Value) {
        documentPath = path;
        md5 = md5Value;
    }

    public void execute() throws Exception {
        if (StringUtils.isNullOrEmpty(md5)) {
            md5 = FileUtils.computeMD5(new File(documentPath));
        }
        document = DataProviderManager.getDataProvider().loadMetadata(getContext(), documentPath, md5);
        document.setUniqueId(md5);
    }


    public final BaseOptions getDocument() {
        final BaseOptions baseOptions = BaseOptions.optionsFromJSONString(document.getExtraAttributes());
        baseOptions.setMd5(md5);
        return baseOptions;
    }

}
