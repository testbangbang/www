package com.onyx.kreader.host.request;

import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zengzhu on 3/11/16.
 * Render thumbnail without using layout manager.
 */
public class RenderThumbnailRequest extends BaseRequest {

    private String page;
    private ReaderBitmap bitmap;

    public RenderThumbnailRequest(final String p, final ReaderBitmap bmp) {
        super();
        page = p;
        bitmap = bmp;
    }

    public void execute(final Reader reader) throws Exception {
        final RectF origin = reader.getDocument().getPageOriginSize(page);
        final RectF displayRect = new RectF(0, 0, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight());
        final float scale = PageUtils.scaleToPage(origin.width(), origin.height(), displayRect.width(), displayRect.height());
        final RectF positionRect = new RectF(origin);
        final RectF visibleRect = new RectF(positionRect);
        reader.getRenderer().draw(page, scale, 0, bitmap, displayRect, positionRect, visibleRect);
    }
}
