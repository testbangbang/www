package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingTextStyleRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderTextStyle style;

    public SettingTextStyleRequest(Reader reader,ReaderTextStyle style) {
        this.reader = reader;
        this.style = style;
    }

    @Override
    public SettingTextStyleRequest call() throws Exception {
        reader.getReaderHelper().getTextStyleManager().setStyle(style);
        reader.getReaderHelper().getBitmapCache().clear();
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());

        ReaderTextStyle s = reader.getReaderHelper().getTextStyleManager().getStyle();
        style = ReaderTextStyle.copy(s);
        return this;
    }

    public ReaderTextStyle getStyle() {
        return style;
    }
}
