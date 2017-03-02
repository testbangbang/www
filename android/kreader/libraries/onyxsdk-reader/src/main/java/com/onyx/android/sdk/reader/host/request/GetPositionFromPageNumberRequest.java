package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/2/24.
 */

public class GetPositionFromPageNumberRequest extends BaseReaderRequest {

    private List<String> documentPositions;
    private List<Integer> pageNumbers;

    public GetPositionFromPageNumberRequest(List<Integer> pageNumbers) {
        this.pageNumbers = pageNumbers;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        if (pageNumbers == null || pageNumbers.size() <=0 ) {
            return;
        }
        documentPositions = new ArrayList<>();
        for (Integer pageNumber : pageNumbers) {
            documentPositions.add(reader.getReaderHelper().getNavigator().getPositionByPageNumber(pageNumber));
        }

    }

    public List<String> getDocumentPositions() {
        return documentPositions;
    }
}
