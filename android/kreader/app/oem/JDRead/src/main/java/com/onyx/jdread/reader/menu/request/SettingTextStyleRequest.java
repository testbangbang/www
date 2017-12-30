package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingTextStyleRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;

    public SettingTextStyleRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public SettingTextStyleRequest call() throws Exception {
        ReaderTextStyle style = readerDataHolder.getReader().getReaderHelper().getTextStyleManager().getStyle();
        readerDataHolder.getReader().getReaderHelper().getTextStyleManager().setStyle(style);
        readerDataHolder.getReaderViewHelper().updatePageView(readerDataHolder);
        return this;
    }
}
