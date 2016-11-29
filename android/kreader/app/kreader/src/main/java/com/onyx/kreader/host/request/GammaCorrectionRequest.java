package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/5/15.
 * redraw request
 */
public class GammaCorrectionRequest extends BaseReaderRequest {

    private int gamma = BaseOptions.getGlobalDefaultGamma();
    private int emboldenLevel = 0;

    public GammaCorrectionRequest(final int gamma, final int emboldenLevel) {
        this.gamma = gamma;
        this.emboldenLevel = emboldenLevel;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getDocumentOptions().setGamma(gamma);
        if (emboldenLevel > 0) {
            reader.getDocumentOptions().setEmboldenLevel(emboldenLevel);
        }
        drawVisiblePages(reader);
    }

}
