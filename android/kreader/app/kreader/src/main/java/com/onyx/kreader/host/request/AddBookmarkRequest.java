package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.dataprovider.Bookmark;
import com.onyx.kreader.dataprovider.BookmarkProvider;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class AddBookmarkRequest extends BaseReaderRequest {

    private Bookmark bookmark;

    public AddBookmarkRequest(Bookmark bookmark) {
        this.bookmark = bookmark;
    }

    public void execute(final Reader reader) throws Exception {
        BookmarkProvider.addBookmark(bookmark);
        LayoutProviderUtils.updateReaderViewInfo(createReaderViewInfo(), reader.getReaderLayoutManager());
    }
}
