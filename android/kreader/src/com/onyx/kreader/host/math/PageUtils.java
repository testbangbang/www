package com.onyx.kreader.host.math;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by zhuzeng on 10/9/15.
 */
public class PageUtils {

    /**
     * Translate to parent coordinates.
     * @param child
     * @param parent
     */
    static public void translateCoordinates(final RectF child, final RectF parent) {
        child.offset(-parent.left, -parent.top);
    }

    static public void translateCoordinates(final PointF child, final RectF parent) {
        child.offset(-parent.left, -parent.top);
    }

    static public void translateCoordinates(final PointF child, final PointF parent) {
        child.offset(-parent.x, -parent.y);
    }

    static public float scaleToPage(final float pageWidth, final float pageHeight, final float viewportWidth, final float viewportHeight) {
        float scale = Math.min(viewportWidth / pageWidth, viewportHeight / pageHeight);
        return scale;
    }

    static public float scaleToWidth(final float pageWidth, final RectF viewport) {
        float scale = viewport.width() / pageWidth;
        return scale;
    }

    /**
     * Scale the child within parent and adjust child size and parent position. Steps
     * 1. calculate the delta scale, by scale to page (child to parent)
     * 2. calculate the child new position and size, by origin size, simply by delta scale.
     * 3. adjust parent position, the center point is the child center point, by using center point to calculate
     *    left and top.
     * @param child the child rect within parent
     * @param parent the viewport rect.
     * @return delta scale
     */
    static public float scaleByRect(final RectF child, final RectF parent) {
        float deltaScale = scaleToPage(child.width(), child.height(), parent.width(), parent.height());
        return scaleWithDelta(child, parent, deltaScale);
    }

    static public float scaleWithDelta(final RectF child, final RectF parent, final float deltaScale) {
        // the center point of child should be moved to center of parent.
        // so we use the parent center x as new child center x to calculate top, left
        float newChildLeft = child.left * deltaScale;
        float newChildTop = child.top * deltaScale;
        child.set(newChildLeft, newChildTop, newChildLeft + child.width() * deltaScale, newChildTop + child.height() * deltaScale);

        // adjust parent position.
        float newParentLeft = child.centerX() - parent.width() / 2.0f;
        float newParentTop = child.centerY() - parent.height() / 2.0f;
        parent.offsetTo(newParentLeft, newParentTop);
        return deltaScale;
    }

    static public float scaleByRatio(final RectF ratio, final float childWidth, final float childHeight, final RectF parent) {
        RectF actualEntry = new RectF(childWidth * ratio.left,
                childHeight * ratio.top,
                childWidth * ratio.right,
                childHeight * ratio.bottom);
        return scaleByRect(actualEntry, parent);
    }

    // make sure child is fully inside parent.
    static public boolean rebound(final RectF child, final RectF parent) {
        if (child.width() < parent.width()) {
            if (child.left < parent.left) {
                child.offsetTo(parent.left, child.top);
            }
            if (child.right > parent.right) {
                child.offsetTo(parent.right - child.width(), child.top);
            }
        } else {
            child.offsetTo((parent.width() - child.width()) / 2.0f, child.top);
        }
        if (child.height() < parent.height()) {
            if (child.top < parent.top) {
                child.offsetTo(child.left, parent.top);
            }
            if (child.bottom > parent.bottom) {
                child.offsetTo(child.left, parent.bottom - child.height());
            }
        } else {
            child.offsetTo(child.left, (parent.height() - child.height()) / 2.0f);
        }
        return true;
    }
}
