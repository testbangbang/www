package com.onyx.kreader.host.math;

import android.graphics.RectF;

/**
 * Created by zhuzeng on 10/8/15.
 * Represent each page, could be scalable and reflowable.
 */
public class PageInfo {

    private String name;

    private int pageOrientation;      // degree 0, 90, 180, 270.
    private float originWidth;
    private float originHeight;

    private RectF autoCropContentRegion;    // content region with auto crop scale.
    private float autoCropScale;

    private RectF positionRect = new RectF(); // page position rect in pages bounding rectangle coordinates system with actual scale.
    private RectF displayRect = new RectF(); // page display rect in view port coordinates system with actual scale.
    private float actualScale = 1.0f;

    public PageInfo(final String string, final float nw, final float nh) {
        name = string;
        originWidth = nw;
        originHeight = nh;
        positionRect.set(0, 0, nw, nh);
    }

    public final float getOriginWidth() {
        return originWidth;
    }

    public final float getOriginHeight() {
        return originHeight;
    }

    public final RectF getAutoCropContentRegion() {
        return autoCropContentRegion;
    }

    public final RectF getPositionRect() {
        return positionRect;
    }

    public final float getDisplayHeight() {
        return positionRect.height();
    }

    public final float getDisplayWidth() {
        return positionRect.width();
    }

    public final float getActualScale() {
        return actualScale;
    }

    public void update(final float newScale, final float x, final float y) {
        setScale(newScale);
        setPosition(x, y);
    }

    public void setPosition(final float x, final float y) {
        positionRect.offsetTo(x, y);
    }

    public float getX() {
        return positionRect.left;
    }

    public float getY() {
        return positionRect.top;
    }

    public void setX(final float x) {
        positionRect.offsetTo(x, positionRect.top);
    }

    public void setY(final float y) {
        positionRect.offsetTo(positionRect.left, y);
    }

    public void setScale(final float newScale) {
        actualScale = newScale;
        positionRect.set(positionRect.left,
                positionRect.top,
                positionRect.left + originWidth * actualScale,
                positionRect.top + originHeight * actualScale);
    }

    public RectF updateDisplayRect(final RectF viewport) {
        displayRect.set(positionRect);
        PageUtils.translateCoordinates(displayRect, viewport);
        return displayRect;
    }

    public RectF getDisplayRect() {
        return displayRect;
    }

    public RectF visibleRectInViewport(final RectF viewport) {
        RectF visible = new RectF(positionRect);
        visible.intersect(viewport);
        PageUtils.translateCoordinates(visible, viewport);
        return visible;
    }

    /**
     * Retrieve viewport in page coordinates system. it's viewport relate to current page.
     * @param viewport
     * @return
     */
    public RectF viewportInPage(final RectF viewport) {
        RectF vp = new RectF(viewport);
        PageUtils.translateCoordinates(vp, positionRect);
        return vp;
    }

    public void setName(final String n) {
        name = n;
    }

    public final String getName() {
        return name;
    }

    // TODO: use json
    public String uniqueId() {
        return name + actualScale + displayRect.toString();
    }


}
