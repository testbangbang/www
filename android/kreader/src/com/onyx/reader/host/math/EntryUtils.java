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
