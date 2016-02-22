package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

import java.util.prefs.BackingStoreException;

/**
 * Created by zengzhu on 2/22/16.
 */
public class BackwardRequest extends BaseRequest {

    public BackwardRequest() {
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().goBack();
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }

}
