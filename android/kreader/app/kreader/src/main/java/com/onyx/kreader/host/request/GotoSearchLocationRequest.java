package com.onyx.kreader.host.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class GotoSearchLocationRequest extends GotoLocationRequest {

    private List<ReaderSelection> searchResults;
    private ReaderDataHolder readerDataHolder;

    public GotoSearchLocationRequest(String p, ReaderDataHolder readerDataHolder, List<ReaderSelection> searchResults) {
        super(p);
        this.searchResults = searchResults;
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        super.execute(reader);
        getReaderUserDataInfo().saveSearchResults(translateToScreen(searchResults));
    }

    private List<ReaderSelection> translateToScreen(final List<ReaderSelection> list) {
        for (ReaderSelection selection : list) {
            PageInfo pageInfo = getReaderViewInfo().getPageInfo(selection.getPagePosition());
            if (pageInfo == null) {
                continue;
            }
            for (int i = 0; i < selection.getRectangles().size(); i++) {
                PageUtils.translate(pageInfo.getDisplayRect().left,
                        pageInfo.getDisplayRect().top,
                        pageInfo.getActualScale(),
                        selection.getRectangles().get(i));
            }
        }
        return list;
    }
}
