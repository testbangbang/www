package com.onyx.reader.api;

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
     * Set scale.
     * @param actualScale the actual scale
     * @return false if not supported.
     */
    public boolean setScale(float actualScale);

    /**
     * set viewportInPage
     * @param x the viewportInPage x position
     * @param y the viewportInPage y position
     * @return false, if not supported.
     */
    public boolean setViewport(final float x, final float y);

    /**
     * Clear the bitmap.
     * @param bitmap
     * @return
     */
    public boolean clear(final ReaderBitmap bitmap);

    /**
     * draw content.
     * @param bitmap the target bitmap to draw page.
     * @return false if not supported.
     */
    public boolean draw(final ReaderBitmap bitmap);

    /**
     * draw content. There are two coordinates system.
     * host coordinates system, the viewportInPage is specified in host coordinates system
     * the bitmapx, bitmapy, width and height can be regarded as viewportInPage coordinates system, whereas viewportInPage is the
     * origin point(0, 0)
     * @param bitmap the target bitmap to draw content. Caller may use this method to draw part of content.
     * @param xInBitmap the position x in bitmap to draw.
     * @param yInBitmap the position y in bitmap to draw.
     * @param widthInBitmap the width of content to draw.
     * @param heightInBitmp the height of content to draw.
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
    public boolean draw(final ReaderBitmap bitmap, int xInBitmap, int yInBitmap, int widthInBitmap, int heightInBitmp);

}
