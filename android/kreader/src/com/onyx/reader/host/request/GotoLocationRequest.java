package com.onyx.reader.host.request;

import com.onyx.reader.api.ReaderDocumentOptions;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderPluginOptions;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class GotoLocationRequest extends BaseRequest {

    private int page;
    private String internalPosition;

    public GotoLocationRequest(int p) {
        super();
        page = p;
    }

    public GotoLocationRequest(final String p) {
        internalPosition = p;
    }

    public void execute(final Reader reader) throws Exception {
        ReaderDocumentPosition documentPosition = reader.getReaderHelper().navigator.getPositionByPageNumber(page);
        reader.getReaderHelper().navigator.gotoPosition(documentPosition);
    }

}
