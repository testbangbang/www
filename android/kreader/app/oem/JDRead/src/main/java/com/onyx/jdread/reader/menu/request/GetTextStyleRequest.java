package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class GetTextStyleRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private ReaderTextStyle style;

    public GetTextStyleRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public GetTextStyleRequest call() throws Exception {
        ReaderTextStyle srcStyle = readerDataHolder.getReader().getReaderHelper().getTextStyleManager().getStyle();
        style = ReaderTextStyle.copy(srcStyle);
        return this;
    }

    public ReaderTextStyle getStyle() {
        return style;
    }
}
