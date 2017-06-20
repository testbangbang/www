package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by ming on 16/7/15.
 */
public class GetAnnotationRequest extends BaseReaderRequest {

    public GetAnnotationRequest() {
    }

    public void execute(final Reader reader) throws Exception {
        getReaderUserDataInfo().loadDocumentAnnotations(getContext(), reader);
    }
}
