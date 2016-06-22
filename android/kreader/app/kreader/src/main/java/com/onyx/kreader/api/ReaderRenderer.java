package com.onyx.kreader.api;

import android.graphics.RectF;
import com.onyx.android.sdk.api.ReaderBitmap;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderRenderer {

    /**
     * Get renderer features.
     * @return renderer features interface.
     */
    public ReaderRendererFeatures getRendererFeatures();

    /**
     * draw content. There are two coordinates system.
     * host coordinates system, the viewportInPage is specified in host coordinates system
     * the bitmapx, bitmapy, width and height can be regarded as viewportInPage coordinates system, whereas viewportInPage is the
     * origin point(0, 0)
     * @param page the page position.
     * @param scale the actual scale used to render page.
     * @param rotation the rotation.
     * @param bitmap the target bitmap to draw content. Caller may use this method to draw part of content.
     * @param displayRect the display rect in screen coordinate system.
     * @param pageRect the page rect in doc coordinate system.
     * @param visibleRect the visible rect in doc coordinate system.
     *
     *        bitmap  matrix
     *          (viewportX, viewportY)
     *                |--------------|
     *                |              |
     *                | (x,y)        |
     *                |  |------|    |
     *                |  |      |    |
     *                |  |      |    |
     *                |  |------|    |
     *                |        (w,h) |
     *                |--------------|
     *
     * @return
     */
    public boolean draw(final String page, final float scale, final int rotation, final ReaderBitmap bitmap, final RectF displayRect, final RectF pageRect, final RectF visibleRect);

}
