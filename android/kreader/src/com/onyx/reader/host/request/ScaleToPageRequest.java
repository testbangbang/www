package com.onyx.reader.host.request;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.math.EntryInfo;
import com.onyx.reader.host.math.EntryManager;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class ScaleToPageRequest extends BaseRequest {

    public ScaleToPageRequest() {
    }

    // in document coordinates system. forward to layout manager to scale
    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().scaleToPage();
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }

}
