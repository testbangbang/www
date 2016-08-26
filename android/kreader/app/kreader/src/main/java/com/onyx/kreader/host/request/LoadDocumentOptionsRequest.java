package com.onyx.kreader.host.request;

import com.onyx.android.sdk.dataprovider.DocumentOptions;
import com.onyx.android.sdk.dataprovider.DocumentOptionsProvider;
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
    private DocumentOptions documentOptions;

    public LoadDocumentOptionsRequest(final String path, final String md5Value) {
        documentPath = path;
        md5 = md5Value;
    }

    public void execute() throws Exception {
        if (StringUtils.isNullOrEmpty(md5)) {
            md5 = FileUtils.computeMD5(new File(documentPath));
        }
        documentOptions = DocumentOptionsProvider.loadDocumentOptions(getContext(), documentPath, md5);
        documentOptions.setMd5(md5);
    }


    public final BaseOptions getDocumentOptions() {
        final BaseOptions baseOptions = BaseOptions.optionsFromJSONString(documentOptions.getExtraAttributes());
        baseOptions.setMd5(md5);
        return baseOptions;
    }

}
