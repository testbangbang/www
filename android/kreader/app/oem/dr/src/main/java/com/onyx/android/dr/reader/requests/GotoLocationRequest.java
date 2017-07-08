package com.onyx.android.dr.reader.requests;

import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class GotoLocationRequest extends BaseReaderRequest {

    private int page;
    private String persistentPosition;

    public GotoLocationRequest(int p) {
        super();
        page = p;
    }

    public GotoLocationRequest(final String p) {
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
