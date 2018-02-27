package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.layout.LayoutProviderUtils;

/**
 * Created by huxiaomao on 2018/1/9.
 */

public class DeleteBookmarkRequest extends ReaderBaseRequest {
    private Bookmark bookmark;
    private Reader reader;

    public DeleteBookmarkRequest(Reader reader, Bookmark bookmark) {
        this.bookmark = bookmark;
        this.reader = reader;
    }

    @Override
    public DeleteBookmarkRequest call() throws Exception {
        if (bookmark != null) {
            ContentSdkDataUtils.getDataProvider().deleteBookmark(bookmark);
        }
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(reader);
        return this;
    }
}
