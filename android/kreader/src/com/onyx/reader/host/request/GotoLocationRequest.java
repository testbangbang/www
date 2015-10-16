package com.onyx.reader.host.request;

import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderException;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;
import com.onyx.reader.utils.StringUtils;

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
        ReaderDocumentPosition documentPosition;
        if (StringUtils.isNonBlank(persistentPosition)) {
            documentPosition = reader.getReaderHelper().getNavigator().createPositionFromString(persistentPosition);
        } else {
            documentPosition = reader.getReaderHelper().getNavigator().getPositionByPageNumber(page);
        }
        if (!reader.getReaderHelper().getReaderLayoutManager().gotoPosition(documentPosition)) {
            throw ReaderException.outOfRange();
        }
    }

}
