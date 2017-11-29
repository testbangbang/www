package com.onyx.android.sdk.reader.host.math;

import android.graphics.PointF;
import android.graphics.RectF;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderHitTestArgs;

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

    static public RectF scaleRect(final RectF rect, final float scale) {
        rect.left *= scale;
        rect.top *= scale;
        rect.right *= scale;
        rect.bottom *= scale;
        return rect;
    }

    static public float scaleToPage(final float pageWidth, final float pageHeight, final float viewportWidth, final float viewportHeight) {
        float scale = Math.min(viewportWidth / pageWidth, viewportHeight / pageHeight);
        return scale;
    }

    static public float scaleToWidth(final float pageWidth, final float viewportWidth) {
        float scale = viewportWidth / pageWidth;
        return scale;
    }

    static public float scaleToHeight(final float pageHeight, final float viewportHeight) {
        float scale = viewportHeight / pageHeight;
        return scale;
    }

    /**
     * All in document coordinates system.
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
        boolean changed = false;
        if (child.width() < parent.width()) {
            if (child.left < parent.left) {
                child.offsetTo(parent.left, child.top);
                changed = true;
            }
            if (child.right > parent.right) {
                child.offsetTo(parent.right - child.width(), child.top);
                changed = true;
            }
        } else {
            child.offsetTo(parent.left + (parent.width() - child.width()) / 2.0f, child.top);
            changed = true;
        }
        if (child.height() < parent.height()) {
            if (child.top < parent.top) {
                child.offsetTo(child.left, parent.top);
                changed = true;
            }
            if (child.bottom > parent.bottom) {
                child.offsetTo(child.left, parent.bottom - child.height());
                changed = true;
            }
        } else {
            child.offsetTo(child.left, parent.top + (parent.height() - child.height()) / 2.0f);
            changed = true;
        }
        return changed;
    }

    public static PointF translate(final float originX, final float originY, final float scale, final PointF point) {
        point.set(originX + point.x * scale, originY + point.y * scale);
        return point;
    }

    public static RectF translate(final float originX, final float originY, final float scale, final RectF rect) {
        rect.set(originX + rect.left * scale,
                 originY + rect.top * scale,
                 originX + rect.right * scale,
                 originY + rect.bottom * scale);
        return rect;
    }

    public static RectF alignToLeft(final RectF child, final RectF parent) {
        float delta = parent.left - child.left;
        child.offset(delta, 0);
        return child;
    }

    public static RectF alignToRight(final RectF child, final RectF parent) {
        float delta = parent.right - child.right;
        child.offset(delta, 0);
        return child;
    }

    public static RectF alignToTop(final RectF child, final RectF parent) {
        float delta = parent.top - child.top;
        child.offset(0, delta);
        return child;
    }

    public static RectF alignToBottom(final RectF child, final RectF parent) {
        float delta = parent.bottom - child.bottom;
        child.offset(0, delta);
        return child;
    }

    public static boolean hitTest(final PageInfo pageInfo, final PointF point, final ReaderHitTestArgs args) {
        if (pageInfo.getDisplayRect().contains(point.x, point.y)) {
            args.pageName = pageInfo.getName();
            args.pageDisplayRect = pageInfo.getDisplayRect();
            PageUtils.translateCoordinates(point, pageInfo.getDisplayRect());
            return true;
        }
        return false;
    }

    public static RectF updateDisplayRect(final PageInfo pageInfo, final RectF viewport) {
        RectF rect = new RectF(pageInfo.getPositionRect());
        PageUtils.translateCoordinates(rect, viewport);
        return pageInfo.updateDisplayRect(rect);
    }

    public static RectF updateVisibleRect(final PageInfo pageInfo, final RectF viewport) {
        RectF rect = new RectF(pageInfo.getDisplayRect());
        rect.intersect(new RectF(0, 0, viewport.width(), viewport.height()));
        return pageInfo.updateVisibleRect(rect);
    }

    /**
     * Retrieve viewport in document coordinates system. it's viewport relate to current page.
     * @param viewport
     * @return
     */
    public static RectF viewportInPage(final PageInfo pageInfo, final RectF viewport) {
        RectF vp = new RectF(viewport);
        PageUtils.translateCoordinates(vp, pageInfo.getPositionRect());
        return vp;
    }

    public static RectF translateToDocument(final PageInfo pageInfo, final RectF rectInScreen) {
        PageUtils.translateCoordinates(rectInScreen, pageInfo.getDisplayRect());
        PageUtils.scaleRect(rectInScreen, 1 / pageInfo.getActualScale());
        return rectInScreen;
    }

    public static int countSubPagesRegardingPageRepeat(int totalHeight, int subHeight, int pageRepeat) {
        int pageCount = 1;
        int left = totalHeight - subHeight;
        if (left > 0) {
            int h = subHeight - pageRepeat;
            pageCount += left / h + (left % h != 0 ? 1 : 0);
        }
        return pageCount;
    }

    public static int getSubPageTopRegardingPageRepeat(int subHeight, int pageRepeat, int subIndex) {
        return subIndex * (subHeight - pageRepeat);
    }

}
