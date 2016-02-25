package com.onyx.kreader.api;

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
     * draw content.
     * @param page the page position.
     * @param rotation the rotation.
     * @param bitmap the target bitmap to draw page.
     * @return false if not supported.
     */
    public boolean draw(final String page, final float scale, final int rotation, final ReaderBitmap bitmap);

    /**
     * draw content. There are two coordinates system.
     * host coordinates system, the viewportInPage is specified in host coordinates system
     * the bitmapx, bitmapy, width and height can be regarded as viewportInPage coordinates system, whereas viewportInPage is the
     * origin point(0, 0)
     * @param page the page position.
     * @param scale the actual scale used to render page.
     * @param rotation the rotation.
     * @param bitmap the target bitmap to draw content. Caller may use this method to draw part of content.
     * @param xInBitmap the position x in bitmap to draw.
     * @param yInBitmap the position y in bitmap to draw.
     * @param widthInBitmap the width of content to draw.
     * @param heightInBitmap the height of content to draw.
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
    public boolean draw(final String page, final float scale, final int rotation, final ReaderBitmap bitmap, int xInBitmap, int yInBitmap, int widthInBitmap, int heightInBitmap);

}
