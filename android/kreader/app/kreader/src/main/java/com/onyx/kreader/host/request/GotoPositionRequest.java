package com.onyx.kreader.host.request;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class GotoPositionRequest extends BaseReaderRequest {

    private int page;
    private String persistentPosition;

    public GotoPositionRequest(int p) {
        super();
        page = p;
    }

    public GotoPositionRequest(final String p) {
        persistentPosition = p;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getReaderLayoutManager().setSavePosition(true);
        String documentPosition;
        if (StringUtils.isNotBlank(persistentPosition)) {
            documentPosition = persistentPosition;
        } else {
            documentPosition = reader.getReaderHelper().getNavigator().getPositionByPageNumber(page);
        }
        if (!reader.getReaderHelper().getReaderLayoutManager().gotoPosition(documentPosition)) {
            throw ReaderException.outOfRange();
        }
        drawVisiblePages(reader);
    }

}
