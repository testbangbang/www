package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class AddBookmarkRequest extends BaseReaderRequest {

    private PageInfo pageInfo;

    public AddBookmarkRequest(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public void execute(final Reader reader) throws Exception {
        ContentSdkDataUtils.getDataProvider().addBookmark(createBookmark(reader));
        LayoutProviderUtils.updateReaderViewInfo(reader, createReaderViewInfo(), reader.getReaderLayoutManager());
    }

    private Bookmark createBookmark(final Reader reader) {
        Bookmark bookmark = new Bookmark();
        bookmark.setIdString(reader.getDocumentMd5());
        bookmark.setApplication(reader.getPlugin().displayName());
        bookmark.setPosition(pageInfo.getPosition());
        bookmark.setPageNumber(PagePositionUtils.getPageNumber(pageInfo.getName()));
        bookmark.setQuote(getQuote(reader.getDocument().getPageText(pageInfo.getPosition())));
        return bookmark;
    }

    private String getQuote(String pageText) {
        if (StringUtils.isNullOrEmpty(pageText)) {
            return "";
        }
        if (pageText.length() <= 50) {
            return pageText;
        } else {
            return pageText.substring(0, 50);
        }
    }
}
