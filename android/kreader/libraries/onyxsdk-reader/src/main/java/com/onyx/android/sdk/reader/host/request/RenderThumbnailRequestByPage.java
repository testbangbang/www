package com.onyx.android.sdk.reader.host.request;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.cache.ReaderBitmapImpl;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.utils.BitmapUtils;

/**
 * Created by zengzhu on 3/11/16.
 * Render thumbnail without using layout manager.
 */
public class RenderThumbnailRequestByPage extends RenderThumbnailRequest {


    public RenderThumbnailRequestByPage(String p, ReaderBitmap bmp) {
        super(p, bmp);
    }

    @Override
    protected void locationThumbnailRange(final Reader reader) {
        reader.getNavigator().gotoPage(PagePositionUtils.getPageNumber(getPage()));
    }
}
