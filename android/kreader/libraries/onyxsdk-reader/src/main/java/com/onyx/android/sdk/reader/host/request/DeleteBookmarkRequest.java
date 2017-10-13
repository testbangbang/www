package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class DeleteBookmarkRequest extends BaseReaderRequest {

    private Bookmark bookmark;

    public DeleteBookmarkRequest(Bookmark bookmark) {
        this.bookmark = bookmark;
    }

    public void execute(final Reader reader) throws Exception {
        if (bookmark != null) {
            ContentSdkDataUtils.getDataProvider().deleteBookmark(bookmark);
        }
        LayoutProviderUtils.updateReaderViewInfo(reader, createReaderViewInfo(), reader.getReaderLayoutManager());
    }
}
