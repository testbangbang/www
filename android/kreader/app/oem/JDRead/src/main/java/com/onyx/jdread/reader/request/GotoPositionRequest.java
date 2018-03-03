package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderNavigator;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/30.
 */

public class GotoPositionRequest extends ReaderBaseRequest {
    private String persistentPosition;
    private int page;
    private boolean isDrawPage = true;
    private boolean autoOffset = false;

    public GotoPositionRequest(Reader reader, int page, boolean isDrawPage) {
        super(reader);
        this.page = page;
        this.isDrawPage = isDrawPage;
    }

    public GotoPositionRequest(Reader reader, int page) {
        super(reader);
        this.page = page;
    }

    public GotoPositionRequest(Reader reader, String persistentPosition) {
        super(reader);
        this.persistentPosition = persistentPosition;
    }

    public GotoPositionRequest(Reader reader, String persistentPosition, boolean isDrawPage,boolean autoOffset) {
        super(reader);
        this.persistentPosition = persistentPosition;
        this.isDrawPage = isDrawPage;
        this.autoOffset = autoOffset;
    }

    @Override
    public GotoPositionRequest call() throws Exception {
        getReader().getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        String documentPosition;
        if (StringUtils.isNotBlank(persistentPosition)) {
            documentPosition = persistentPosition;
        } else {
            documentPosition = getReader().getReaderHelper().getNavigator().getPositionByPageNumber(page);
        }

        if (autoOffset && !getReader().getReaderHelper().getRendererFeatures().supportScale()) {
            if (!gotoPositionWithAutoOffset()) {
                throw ReaderException.outOfRange();
            }
        } else {
            if (!getReader().getReaderHelper().getReaderLayoutManager().gotoPosition(documentPosition)) {
                throw ReaderException.outOfRange();
            }
        }

        if (isDrawPage) {
            getReader().getReaderViewHelper().updatePageView(getReader(), getReaderUserDataInfo(), getReaderViewInfo());
        }
        updateSetting(getReader());
        saveReaderOptions(getReader());
        return this;
    }

    private boolean gotoPositionWithAutoOffset() throws ReaderException {
        int page = getReader().getReaderHelper().getNavigator().getPageNumberByPosition(persistentPosition);
        if (!getReader().getReaderHelper().getReaderLayoutManager().gotoPage(page)) {
            return false;
        }

        ReaderNavigator navigator = getReader().getReaderHelper().getNavigator();
        String screenBegin = navigator.getScreenStartPosition();

        while (page > 0 && navigator.comparePosition(persistentPosition, screenBegin) < 0) {
            // in some special cases, position of page may be larger than persistentPosition,
            // so we try to go previous page
            page--;
            if (!getReader().getReaderHelper().getReaderLayoutManager().gotoPage(page)) {
                return false;
            }
            screenBegin = navigator.getScreenStartPosition();
        }

        String screenEnd;
        while (true) {
            screenBegin = navigator.getScreenStartPosition();
            screenEnd = navigator.getScreenEndPosition();
            if (navigator.comparePosition(persistentPosition, screenBegin) >= 0 &&
                    navigator.comparePosition(persistentPosition, screenEnd) <= 0) {
                return true;
            } else if (navigator.comparePosition(persistentPosition, screenEnd) > 0) {
                if (!getReader().getReaderHelper().nextScreen()) {
                    return false;
                }
                continue;
            }

            return false;
        }
    }
}
