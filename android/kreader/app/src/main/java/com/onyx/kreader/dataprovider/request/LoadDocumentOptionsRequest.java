package com.onyx.kreader.dataprovider.request;

import com.onyx.kreader.dataprovider.DocumentOptionsProvider;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.utils.FileUtils;
import com.onyx.kreader.utils.StringUtils;

import java.io.File;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class LoadDocumentOptionsRequest extends BaseDataProviderRequest {

    private String documentPath;
    private volatile String md5;
    private BaseOptions documentOptions;

    public LoadDocumentOptionsRequest(final String path, final String md5Value) {
        documentPath = path;
        md5 = md5Value;
    }

    public void execute() throws Exception {
        documentOptions = DocumentOptionsProvider.loadDocumentOptions(getContext(), documentPath, md5);
    }

    public final BaseOptions getDocumentOptions() {
        return documentOptions;
    }
}
