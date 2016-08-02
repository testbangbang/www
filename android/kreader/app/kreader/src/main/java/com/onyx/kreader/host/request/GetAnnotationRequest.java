package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

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
