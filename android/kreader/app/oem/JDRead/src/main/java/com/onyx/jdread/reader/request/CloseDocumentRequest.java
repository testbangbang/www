package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/29.
 */

public class CloseDocumentRequest extends ReaderBaseRequest {
    private boolean saveOption;

    public CloseDocumentRequest(Reader reader, boolean saveOption) {
        super(reader);
        this.saveOption = saveOption;
    }

    @Override
    public CloseDocumentRequest call() throws Exception {
        if (getReader() == null || getReader().getReaderHelper().getDocument() == null) {
            return this;
        }
        if (saveOption) {
            saveReaderOptions(getReader());
        }
        getReader().getReaderHelper().closeDocument();
        return this;
    }
}
