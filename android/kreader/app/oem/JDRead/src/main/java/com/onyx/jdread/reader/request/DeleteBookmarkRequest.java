package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.layout.LayoutProviderUtils;

/**
 * Created by huxiaomao on 2018/1/9.
 */

public class DeleteBookmarkRequest extends ReaderBaseRequest {
    private Bookmark bookmark;
    private ReaderDataHolder readerDataHolder;

    public DeleteBookmarkRequest(ReaderDataHolder readerDataHolder, Bookmark bookmark) {
        this.bookmark = bookmark;
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public DeleteBookmarkRequest call() throws Exception {
        if (bookmark != null) {
            ContentSdkDataUtils.getDataProvider().deleteBookmark(bookmark);
        }
        LayoutProviderUtils.updateReaderViewInfo(readerDataHolder.getReader(), getReaderViewInfo(), readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager());
        return this;
    }
}
