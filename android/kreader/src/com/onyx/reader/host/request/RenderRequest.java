package com.onyx.reader.host.request;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderBitmap;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.math.EntryInfo;
import com.onyx.reader.host.wrapper.Reader;

import java.util.List;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class RenderRequest extends BaseRequest {

    public RenderRequest() {
        super();
    }

    public void execute(final Reader reader) throws Exception {
        reader.getReaderLayoutManager().drawVisiblePages(reader.getReaderHelper().getRenderBitmap());
    }

}
