package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by ming on 2017/2/24.
 */

public class GetPageNumberFromPositionRequest extends BaseReaderRequest {

    private String documentPosition;

    private int pageNumber;

    public GetPageNumberFromPositionRequest(String documentPosition) {
        this.documentPosition = documentPosition;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        pageNumber = reader.getReaderHelper().getNavigator().getPageNumberByPosition(documentPosition);
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
