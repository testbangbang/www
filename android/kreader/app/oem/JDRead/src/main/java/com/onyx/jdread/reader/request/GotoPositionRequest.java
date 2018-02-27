package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderNavigator;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/30.
 */

public class GotoPositionRequest extends ReaderBaseRequest {
    private Reader reader;
    private String persistentPosition;
    private int page;
    private boolean isDrawPage = true;
    private boolean autoOffset = false;

    public GotoPositionRequest(Reader reader, int page, boolean isDrawPage) {
        this.reader = reader;
        this.page = page;
        this.isDrawPage = isDrawPage;
    }

    public GotoPositionRequest(Reader reader, int page) {
        this.reader = reader;
        this.page = page;
    }

    public GotoPositionRequest(Reader reader, String persistentPosition) {
        this.reader = reader;
        this.persistentPosition = persistentPosition;
    }

    public GotoPositionRequest(Reader reader, String persistentPosition, boolean isDrawPage, boolean autoOffset) {
        this.reader = reader;
        this.persistentPosition = persistentPosition;
        this.isDrawPage = isDrawPage;
        this.autoOffset = autoOffset;
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

        if (autoOffset && !reader.getReaderHelper().getRendererFeatures().supportScale()) {
            if (!gotoPositionWithAutoOffset()) {
                throw ReaderException.outOfRange();
            }
        } else {
            if (!reader.getReaderHelper().getReaderLayoutManager().gotoPosition(documentPosition)) {
                throw ReaderException.outOfRange();
            }
        }

        if (isDrawPage) {
            reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(), getReaderViewInfo());
        }
        updateSetting(reader);
        return this;
    }

    private boolean gotoPositionWithAutoOffset() throws ReaderException {
        int page = reader.getReaderHelper().getNavigator().getPageNumberByPosition(persistentPosition);
        if (!reader.getReaderHelper().getReaderLayoutManager().gotoPage(page)) {
            return false;
        }

        ReaderNavigator navigator = reader.getReaderHelper().getNavigator();
        while (true) {
            String screenBegin = navigator.getScreenStartPosition();
            String screenEnd = navigator.getScreenEndPosition();
            if (navigator.comparePosition(persistentPosition, screenBegin) >= 0 &&
                    navigator.comparePosition(persistentPosition, screenEnd) <= 0) {
                return true;
            } else if (navigator.comparePosition(persistentPosition, screenEnd) > 0) {
                if (!reader.getReaderHelper().nextScreen()) {
                    return false;
                }
                continue;
            } else if (navigator.comparePosition(persistentPosition, screenBegin) < 0) {
                return false;
            }

            return false;
        }
    }
}
