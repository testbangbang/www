package com.onyx.reader.api;

import android.graphics.RectF;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderScalingManager {

    /**
     * Scale to page.
     */
    public void setScaleToPage();

    /**
     * Check if scale to page.
     * @return
     */
    public boolean isScaleToPage();

    /**
     * Set scale to width.
     */
    public void setScaleToWidth();

    /**
     * Check if it's scale to width.
     * @return
     */
    public boolean isScaleToWidth();

    public void setScaleToHeight();

    public boolean isScaleToHeight();

    public boolean isCropPage();

    public void setCropPage();

    public boolean isCropWidth();

    public void setCropWidth();

    /**
     * Retrieve actual scale.
     * @return
     */
    public float getActualScale();

    /**
     * Change scale.
     * @param scale
     */
    public void setActualScale(final float scale);

    /**
     * Set viewport. The behavior is different on different page layout.
     * @param viewport
     */
    public boolean setViewport(final RectF viewport);

    /**
     * Retrieve current viewport.
     * @return the current viewport.
     */
    public RectF getViewport();


    /**
     * Convinent method to set scale and viewport directly.
     * @param actualScale the actual scale
     * @param x the viewport x position
     * @param y the viewport y position
     * @return
     */
    public boolean changeScale(float actualScale, float x, float y);

    /**
     * Return the page display rect on view coordinates.
     * @param position the page position.
     * @return
     */
    public RectF getPageDisplayRect(final ReaderDocumentPosition position);


}
