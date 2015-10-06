package com.onyx.reader.api;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderRenderer {

    /**
     * draw content.
     * @param bitmap the target bitmap to draw page.
     * @return false if not supported.
     */
    public boolean draw(final ReaderBitmap bitmap);

    /**
     * draw content.
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
