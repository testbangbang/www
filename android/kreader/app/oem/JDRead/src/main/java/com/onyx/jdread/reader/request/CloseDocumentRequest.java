package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.SettingInfo;

/**
 * Created by huxiaomao on 2018/1/29.
 */

public class CloseDocumentRequest extends ReaderBaseRequest {
    private Reader reader;
    private boolean saveOption;

    public CloseDocumentRequest(Reader reader, boolean saveOption) {
        this.reader = reader;
        this.saveOption = saveOption;
    }

    @Override
    public CloseDocumentRequest call() throws Exception {
        if (reader == null || reader.getReaderHelper().getDocument() == null) {
            return this;
        }
        if (saveOption) {
            saveReaderOptions(reader);
        }
        reader.getReaderHelper().closeDocument();
        return this;
    }
}
