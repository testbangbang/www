package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.SettingInfo;

/**
 * Created by huxiaomao on 2018/1/29.
 */

public class CloseDocumentRequest extends ReaderBaseRequest {
    private Reader reader;
    private SettingInfo settingInfo;

    public CloseDocumentRequest(Reader reader,SettingInfo settingInfo) {
        this.reader = reader;
        this.settingInfo = settingInfo;
    }

    @Override
    public CloseDocumentRequest call() throws Exception {
        if (reader == null || reader.getReaderHelper().getDocument() == null) {
            return this;
        }
        saveReaderOptions(reader,settingInfo);
        reader.getReaderHelper().getDocument().close();
        reader.getReaderHelper().onDocumentClosed();
        return this;
    }
}
