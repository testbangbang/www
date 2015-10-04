package com.onyx.reader.plugin;

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

    public void setScaleToWidth();

    public boolean isScaleToWidth();

    public void setScaleToHeight();

    public boolean isScaleToHeight();

    public boolean isCropPage();

    public void setCropPage();

    public boolean isCropWidth();

    public void setCropWidth();

    public double getActualScale();

    public void setActualScale(double scale);

    /**
     * Set viewport.
     * @param viewport
     */
    public boolean setViewport(final RectF viewport);

    /**
     * Retrieve current viewport.
     * @return the current viewport.
     */
    public RectF getViewport();

}
