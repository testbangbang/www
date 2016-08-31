package com.onyx.kreader.host.request;

import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.kreader.host.wrapper.Reader;

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
            DataProviderManager.getDataProvider().deleteBookmark(bookmark);
        }
        LayoutProviderUtils.updateReaderViewInfo(createReaderViewInfo(), reader.getReaderLayoutManager());
    }
}
