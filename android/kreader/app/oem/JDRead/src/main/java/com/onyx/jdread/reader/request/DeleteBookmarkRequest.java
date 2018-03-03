package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/9.
 */

public class DeleteBookmarkRequest extends ReaderBaseRequest {
    private Bookmark bookmark;

    public DeleteBookmarkRequest(Reader reader, Bookmark bookmark) {
        super(reader);
        this.bookmark = bookmark;
    }

    @Override
    public DeleteBookmarkRequest call() throws Exception {
        if (bookmark != null) {
            ContentSdkDataUtils.getDataProvider().deleteBookmark(bookmark);
        }
        getReader().getReaderViewHelper().updatePageView(getReader(),getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(getReader());
        return this;
    }
}
