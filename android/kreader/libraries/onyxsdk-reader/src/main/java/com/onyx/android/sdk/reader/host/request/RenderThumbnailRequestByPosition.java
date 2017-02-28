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
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zengzhu on 3/11/16.
 * Render thumbnail without using layout manager.
 */
public class RenderThumbnailRequestByPosition extends RenderThumbnailRequest {

    private String pagePosition;

    public RenderThumbnailRequestByPosition(final String p, final String pagePosition, final ReaderBitmap bmp) {
        super(p, bmp);
        this.pagePosition = pagePosition;
    }

    public static RenderThumbnailRequestByPosition pageThumbnailRequest(final String page, final String pagePosition, final ReaderBitmap bmp) {
        return new RenderThumbnailRequestByPosition(page, pagePosition, bmp);
    }

    @Override
    protected void locationThumbnailRange(Reader reader) {
        if (StringUtils.isNullOrEmpty(pagePosition)) {
            return;
        }
        reader.getNavigator().gotoPosition(pagePosition);
    }


}
