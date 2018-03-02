package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.impl.ReaderSearchOptionsImpl;
import com.onyx.android.sdk.reader.utils.ChapterInfo;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.layout.LayoutProviderUtils;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

import java.util.List;

/**
 * Created by huxiaomao on 2018/1/31.
 */

public class SearchRequest extends ReaderBaseRequest {
    private ReaderSearchOptionsImpl searchOptions;
    private String currentPage;
    private Reader reader;

    public SearchRequest(final String currentPage, final String text, boolean caseSensitive, boolean match, int contentLength, Reader reader) {
        super(reader);
        searchOptions = new ReaderSearchOptionsImpl(currentPage, text, caseSensitive, match);
        searchOptions.setContextLength(contentLength);
        this.currentPage = currentPage;
        this.reader = reader;
    }

    @Override
    public SearchRequest call() throws Exception {
        getReaderViewInfo();
        reader.getReaderHelper().getReaderLayoutManager().getPageManager().collectVisiblePages();
        reader.getReaderHelper().getSearchManager().searchInPage(PagePositionUtils.getPageNumber(currentPage), searchOptions, true);
        if (reader.getReaderHelper().getSearchManager().searchResults().size() > 0) {
            LayoutProviderUtils.updateReaderViewInfo(reader, getReaderViewInfo(), reader.getReaderHelper().getReaderLayoutManager());
            setSearchResultChapterName();
            getReaderUserDataInfo().saveSearchResults(reader.getReaderHelper().getSearchManager().searchResults());
        }
        return this;
    }

    private void setSearchResultChapterName() {
        List<ReaderSelection> selections = reader.getReaderHelper().getSearchManager().searchResults();
        for (ReaderSelection selection : selections) {
            String startPosition = selection.getStartPosition();
            int position = PagePositionUtils.getPosition(startPosition);
            ChapterInfo chapterInfo = LayoutProviderUtils.getChapterInfoByPage(position, getReaderViewInfo().getReadTocChapterNodeList());
            if (chapterInfo != null) {
                selection.chapterName = chapterInfo.getTitle();
            } else {
                selection.chapterName = reader.getDocumentInfo().getBookName();
            }
        }
    }
}
