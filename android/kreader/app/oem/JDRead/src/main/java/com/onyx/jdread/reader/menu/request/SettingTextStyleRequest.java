package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingTextStyleRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private ReaderTextStyle style;

    public SettingTextStyleRequest(ReaderDataHolder readerDataHolder,ReaderTextStyle style) {
        this.readerDataHolder = readerDataHolder;
        this.style = style;
    }

    @Override
    public SettingTextStyleRequest call() throws Exception {
        readerDataHolder.getReader().getReaderHelper().getTextStyleManager().setStyle(style);
        readerDataHolder.getReaderViewHelper().updatePageView(readerDataHolder,createReaderViewInfo());
        return this;
    }
}
