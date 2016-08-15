package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/5/15.
 * redraw request
 */
public class GammaCorrectionRequest extends BaseReaderRequest {

    private int gamma = PageConstants.DEFAULT_GAMMA;
    public GammaCorrectionRequest(int value) {
        gamma = value;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getDocumentOptions().setGamma(gamma);
        drawVisiblePages(reader);
    }

}
