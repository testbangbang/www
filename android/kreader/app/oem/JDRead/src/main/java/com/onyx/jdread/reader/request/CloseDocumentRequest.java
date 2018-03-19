package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

/**
 * Created by huxiaomao on 2018/1/29.
 */

public class CloseDocumentRequest extends ReaderBaseRequest {
    private long startTime;
    private boolean saveOption;
    private long readingTime;

    public CloseDocumentRequest(Reader reader, boolean saveOption, long readingTime, long startTime) {
        super(reader);
        this.saveOption = saveOption;
        this.readingTime = readingTime;
        this.startTime = startTime;
    }

    @Override
    public CloseDocumentRequest call() throws Exception {
        if (getReader() == null || getReader().getReaderHelper().getDocument() == null) {
            return this;
        }
        ReaderViewUtil.updateReadingTime(getAppContext(),getReader().getReaderHelper().getDocumentMd5(),
                readingTime,startTime, getReader().getReaderHelper().getDocumentInfo().getCloudId());
        if (saveOption) {
            getReader().getReaderHelper().getDocumentOptions().setReadProgress(readingTime);
            saveReaderOptions(getReader());
        }
        getReader().getReaderHelper().closeDocument();
        return this;
    }
}
