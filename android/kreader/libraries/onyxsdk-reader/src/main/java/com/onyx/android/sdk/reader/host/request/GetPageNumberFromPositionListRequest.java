package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ming on 2017/2/24.
 */

public class GetPageNumberFromPositionListRequest extends BaseReaderRequest {

    private List<String> positionList = new ArrayList<>();
    private HashMap<String, Integer> positionNumberMap = new HashMap<>();

    public GetPageNumberFromPositionListRequest(List<String> positionList) {
        this.positionList.addAll(positionList);
    }

    @Override
    public void execute(Reader reader) throws Exception {
        for (String position : positionList) {
            positionNumberMap.put(position, reader.getReaderHelper().getNavigator().getPageNumberByPosition(position));
        }
    }

    public HashMap<String, Integer> getPositionNumberMap() {
        return positionNumberMap;
    }
    
}
