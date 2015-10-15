package com.onyx.reader.host.math;

import android.graphics.RectF;

/**
 * Created by zhuzeng on 10/9/15.
 */
public class EntryUtils {

    static public float scaleToPage(final RectF entry, final RectF viewport) {
        float scale = Math.min(viewport.width() / entry.width(), viewport.height() / entry.height());
        return scale;
    }

    static public float scaleToWidth(final RectF entry, final RectF viewport) {
        float scale = viewport.width() / entry.width();
        return scale;
    }

    /**
     * Scale the child to parent and adjust both child and parent position and size. Steps
     * 1. calculate the delta scale
     * 2. calculate the child new position and size, by the parent center point
     * 3. adjust parent new position and size since the distance between the two rects should be the delta * distance.
     * @param child
     * @param parent
     * @return
     */
    static public float scaleByRect(final RectF child, final RectF parent) {
        // delta scale
        float xScale = parent.width() / child.width();
        float yScale = parent.height() / child.height();
        float deltaScale = Math.min(xScale, yScale);

        // the center point of child should be moved to center of parent.
        // so we use the parent center x as new child center x to caluclate top, left
        float newChildLeft = parent.centerX() - child.width() / 2 * deltaScale;
        float newChildTop = parent.centerY() - child.height() / 2 * deltaScale;


        // adjust parent by the distance between top left
        float newParentLeft = newChildLeft - (child.left - parent.left) * deltaScale;
        float newParentTop = newChildTop - (child.top - parent.top) * deltaScale;

        child.set(newChildLeft, newChildTop, newChildLeft + child.width() * deltaScale, newChildTop + child.height() * deltaScale);
        parent.set(newParentLeft, newParentTop, newParentLeft + parent.width() * deltaScale, newParentTop + parent.height() * deltaScale);
        return deltaScale;
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
            child.offsetTo((parent.width() - child.width()) / 2, child.top);
        }
        if (child.height() < parent.height()) {
            if (child.top < parent.top) {
                child.offsetTo(child.left, parent.top);
            }
            if (child.bottom > parent.bottom) {
                child.offsetTo(child.left, parent.bottom - child.height());
            }
        } else {
            child.offsetTo(child.left, (parent.height() - child.height()) / 2);
        }
        return true;
    }
}
