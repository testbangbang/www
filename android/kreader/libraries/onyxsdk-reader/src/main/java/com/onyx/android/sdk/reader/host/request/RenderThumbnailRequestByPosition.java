package com.onyx.android.sdk.reader.host.request;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.reader.cache.ReaderBitmapImpl;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zengzhu on 3/11/16.
 * Render thumbnail without using layout manager.
 */
public class RenderThumbnailRequestByPosition extends RenderThumbnailRequest {

    private boolean nextPage = false;
    private String pagePosition;

    public RenderThumbnailRequestByPosition(final String p, final String pagePosition, final ReaderBitmap bmp) {
        super(p, bmp);
        this.pagePosition = pagePosition;
    }

    public RenderThumbnailRequestByPosition(final String p, final String pagePosition, final ReaderBitmap bmp, final boolean nextPage) {
        super(p, bmp);
        this.pagePosition = pagePosition;
        this.nextPage = nextPage;
    }

    public static RenderThumbnailRequestByPosition nextPageThumbnailRequest(final String p, final String pagePosition, final ReaderBitmap bmp) {
        return new RenderThumbnailRequestByPosition(p, pagePosition, bmp, true);
    }

    public static RenderThumbnailRequestByPosition pageThumbnailRequest(final String page, final String pagePosition, final ReaderBitmap bmp) {
        return new RenderThumbnailRequestByPosition(page, pagePosition, bmp);
    }

    @Override
    protected void locationThumbnailRange(Reader reader) {
        if (isNextPage()) {
            reader.getNavigator().nextScreen(pagePosition);
        }else {
            reader.getNavigator().gotoPosition(pagePosition);
        }
    }

    public boolean isNextPage() {
        return nextPage;
    }


}
