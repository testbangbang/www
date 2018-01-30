package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/30.
 */

public class GotoPositionRequest extends ReaderBaseRequest {
    private Reader reader;
    private String persistentPosition;
    private int page;

    public GotoPositionRequest(Reader reader,int page) {
        this.reader = reader;
        this.page = page;
    }

    public GotoPositionRequest(Reader reader, String persistentPosition) {
        this.reader = reader;
        this.persistentPosition = persistentPosition;
    }

    @Override
    public GotoPositionRequest call() throws Exception {
        reader.getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        String documentPosition;
        if (StringUtils.isNotBlank(persistentPosition)) {
            documentPosition = persistentPosition;
        } else {
            documentPosition = reader.getReaderHelper().getNavigator().getPositionByPageNumber(page);
        }
        if (!reader.getReaderHelper().getReaderLayoutManager().gotoPosition(documentPosition)) {
            throw ReaderException.outOfRange();
        }
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(reader);
        return this;
    }
}
