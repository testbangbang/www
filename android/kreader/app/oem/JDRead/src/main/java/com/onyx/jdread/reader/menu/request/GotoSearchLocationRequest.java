package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.SettingInfo;
import com.onyx.jdread.reader.layout.LayoutProviderUtils;
import com.onyx.jdread.reader.request.GotoPositionRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2018/2/1.
 */

public class GotoSearchLocationRequest extends GotoPositionRequest {
    private List<ReaderSelection> searchResults;
    private Reader reader;

    public GotoSearchLocationRequest(List<ReaderSelection> results, Reader reader, String position, SettingInfo settingInfo) {
        super(reader, position, false,true,settingInfo);
        searchResults = new ArrayList<>();
        for (ReaderSelection searchResult : results) {
            this.searchResults.add(searchResult.clone());
        }
        this.reader = reader;
    }

    @Override
    public GotoSearchLocationRequest call() throws Exception {
        super.call();
        LayoutProviderUtils.updateReaderViewInfo(reader, getReaderViewInfo(), reader.getReaderHelper().getReaderLayoutManager());
        reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(), getReaderViewInfo(), null, searchResults);
        return this;
    }
}
