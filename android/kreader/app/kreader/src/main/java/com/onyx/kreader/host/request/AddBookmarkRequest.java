package com.onyx.kreader.host.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.dataprovider.Bookmark;
import com.onyx.kreader.dataprovider.BookmarkProvider;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.PagePositionUtils;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class AddBookmarkRequest extends BaseReaderRequest {

    private PageInfo pageInfo;

    public AddBookmarkRequest(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public void execute(final Reader reader) throws Exception {
        BookmarkProvider.addBookmark(createBookmark(reader));
        LayoutProviderUtils.updateReaderViewInfo(createReaderViewInfo(), reader.getReaderLayoutManager());
    }

    private Bookmark createBookmark(final Reader reader) {
        Bookmark bookmark = new Bookmark();
        bookmark.setMd5(reader.getDocumentMd5());
        bookmark.setApplication(reader.getPlugin().displayName());
        bookmark.setPosition(pageInfo.getName());
        bookmark.setPageNumber(PagePositionUtils.getPageNumber(pageInfo.getName()));
        bookmark.setQuote(getQuote(reader.getDocument().getPageText(pageInfo.getName())));
        return bookmark;
    }

    private String getQuote(String pageText) {
        if (pageText.length() <= 50) {
            return pageText;
        } else {
            return pageText.substring(0, 50);
        }
    }
}
