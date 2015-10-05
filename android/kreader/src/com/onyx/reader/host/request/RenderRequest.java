package com.onyx.reader.host.request;

import com.onyx.reader.api.ReaderBitmap;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class RenderRequest extends BaseRequest {

    private ReaderBitmap bitmap;

    public RenderRequest() {
        super();
    }

    public void execute(final Reader reader) throws Exception {
        bitmap = reader.getReaderHelper().renderBitmap;
        reader.getReaderHelper().renderToBitmap();
    }

    public ReaderBitmap getReaderBitmap() {
        return bitmap;
    }
}
