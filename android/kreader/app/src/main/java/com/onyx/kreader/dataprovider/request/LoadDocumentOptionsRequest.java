package com.onyx.kreader.dataprovider.request;

import com.onyx.kreader.dataprovider.DocumentOptionsProvider;
import com.onyx.kreader.host.options.BaseOptions;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class LoadDocumentOptionsRequest extends BaseDataProviderRequest {

    private String documentPath;
    private BaseOptions documentOptions;

    public LoadDocumentOptionsRequest(final String path) {
        documentPath = path;
    }

    public void execute() {
        documentOptions = DocumentOptionsProvider.loadDocumentOptions(getContext(), documentPath);
    }

    public final BaseOptions getDocumentOptions() {
        return documentOptions;
    }
}
