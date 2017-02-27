package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by ming on 2017/2/24.
 */

public class GetDocumentPositionRequest extends BaseReaderRequest {

    private String documentPosition;
    private int pageNumber;

    public GetDocumentPositionRequest(int pageNumbers) {
        this.pageNumber = pageNumbers;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        documentPosition = reader.getReaderHelper().getNavigator().getPositionByPageNumber(pageNumber);

    }

    public String getDocumentPosition() {
        return documentPosition;
    }
}
