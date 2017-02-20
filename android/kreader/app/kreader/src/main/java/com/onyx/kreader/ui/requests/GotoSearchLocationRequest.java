package com.onyx.kreader.ui.requests;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.request.GotoPositionRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class GotoSearchLocationRequest extends GotoPositionRequest {

    private List<ReaderSelection> searchResults;
    private ReaderDataHolder readerDataHolder;

    public GotoSearchLocationRequest(String p, ReaderDataHolder readerDataHolder, List<ReaderSelection> results) {
        super(p);
        searchResults = new ArrayList<>();
        for (ReaderSelection searchResult : results) {
            this.searchResults.add(searchResult.clone());
        }
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        super.execute(reader);
        getReaderUserDataInfo().saveSearchResults(translateToScreen(reader, searchResults));
    }

    private List<ReaderSelection> translateToScreen(final Reader reader, final List<ReaderSelection> list) {
        for (ReaderSelection searchResult : list) {
            PageInfo pageInfo = getReaderViewInfo().getPageInfo(searchResult.getPagePosition());
            if (pageInfo == null) {
                continue;
            }
            if (reader.getRendererFeatures().supportScale()) {
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
