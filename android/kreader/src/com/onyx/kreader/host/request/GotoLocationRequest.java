package com.onyx.kreader.host.request;

import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.StringUtils;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class GotoLocationRequest extends BaseRequest {

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
        String documentPosition;
        if (StringUtils.isNonBlank(persistentPosition)) {
            documentPosition = persistentPosition;
        } else {
            documentPosition = reader.getReaderHelper().getNavigator().getPositionByPageNumber(page);
        }
        if (!reader.getReaderHelper().getReaderLayoutManager().gotoPosition(documentPosition)) {
            throw ReaderException.outOfRange();
        }
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }

}
