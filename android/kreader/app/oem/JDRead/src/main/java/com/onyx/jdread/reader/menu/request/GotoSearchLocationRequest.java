package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.jdread.reader.data.Reader;
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

    public GotoSearchLocationRequest(List<ReaderSelection> results, Reader reader, String position) {
        super(reader, position, false);
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
        getReaderUserDataInfo().saveSearchResults(translateToScreen(reader, searchResults));
        reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(), getReaderViewInfo());
        return this;
    }

    private List<ReaderSelection> translateToScreen(final Reader reader, final List<ReaderSelection> list) {
        for (ReaderSelection searchResult : list) {
            PageInfo pageInfo = getReaderViewInfo().getPageInfo(searchResult.getPagePosition());
            if (pageInfo == null) {
                continue;
            }
            if (reader.getReaderHelper().getRendererFeatures().supportScale()) {
                for (int i = 0; i < searchResult.getRectangles().size(); i++) {
                    PageUtils.translate(pageInfo.getDisplayRect().left,
                            pageInfo.getDisplayRect().top,
                            pageInfo.getActualScale(),
                            searchResult.getRectangles().get(i));
                }
            } else {
                ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
                ReaderSelection sel = hitTestManager.selectOnScreen(pageInfo.getPosition(),
                        searchResult.getStartPosition(), searchResult.getEndPosition());
                searchResult.getRectangles().clear();
                if (sel != null) {
                    searchResult.getRectangles().addAll(sel.getRectangles());
                }
            }
        }
        return list;
    }
}
