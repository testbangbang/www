package com.onyx.android.dr.reader.requests;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class GotoSearchLocationRequest extends GotoLocationRequest {

    private List<ReaderSelection> searchResults;

    public GotoSearchLocationRequest(String p, List<ReaderSelection> searchResults) {
        super(p);
        this.searchResults = searchResults;
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
