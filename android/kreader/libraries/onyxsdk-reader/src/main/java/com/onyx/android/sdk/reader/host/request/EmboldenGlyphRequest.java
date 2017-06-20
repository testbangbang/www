package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/5/15.
 * redraw request
 */
public class EmboldenGlyphRequest extends BaseReaderRequest {
    private int emboldenLevel = 0;

    public EmboldenGlyphRequest(int emboldenLevel) {
        this.emboldenLevel = emboldenLevel;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getDocumentOptions().setEmboldenLevel(emboldenLevel);
        drawVisiblePages(reader);
    }

}
