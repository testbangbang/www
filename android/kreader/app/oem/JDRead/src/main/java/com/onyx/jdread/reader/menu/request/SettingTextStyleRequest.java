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
    private ReaderTextStyle inputStyle;

    public SettingTextStyleRequest(Reader reader,ReaderTextStyle inputStyle) {
        this.reader = reader;
        this.inputStyle = inputStyle;
    }

    @Override
    public SettingTextStyleRequest call() throws Exception {
        reader.getReaderHelper().getTextStyleManager().setStyle(inputStyle);
        reader.getReaderHelper().getBitmapCache().clear();
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());

        updateSetting(reader);
        saveReaderOptions(reader);
        return this;
    }
}
