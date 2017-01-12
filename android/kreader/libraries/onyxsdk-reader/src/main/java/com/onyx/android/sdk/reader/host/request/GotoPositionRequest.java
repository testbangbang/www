package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;

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
