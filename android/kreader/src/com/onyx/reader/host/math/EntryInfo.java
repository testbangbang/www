package com.onyx.reader.host.math;

import android.graphics.RectF;

/**
 * Created by zhuzeng on 10/8/15.
 */
public class EntryInfo {

    private int naturalOrientation;      // degree 0, 90, 180, 270.
    private RectF naturalRect = new RectF();

    private RectF autoCropContentRegion;    // content region with auto crop scale.
    private float autoCropScale;


    private RectF displayRect = new RectF(); // page rect in host with actual scale.
    private float actualScale = 1.0f;

    public EntryInfo(final float nw, final float nh) {
        naturalRect.set(0, 0, nw, nh);
    }

    public final RectF getNaturalRect() {
        return naturalRect;
    }

    public final RectF getAutoCropContentRegion() {
        return autoCropContentRegion;
    }

    public final RectF getDisplayRect() {
        return displayRect;
    }

    public final float getDisplayHeight() {
        return displayRect.height();
    }

    public final float getDisplayWidth() {
        return displayRect.width();
    }

    public final float getActualScale() {
        return actualScale;
    }

    public void update(final float newScale, final float x, final float y) {
        setScale(newScale);
        setPosition(x, y);
    }

    public void setPosition(final float x, final float y) {
        displayRect.offsetTo(x, y);
    }

    public float getX() {
        return displayRect.left;
    }

    public float getY() {
        return displayRect.top;
    }

    public void setX(final float x) {
        displayRect.offsetTo(x, displayRect.top);
    }

    public void setY(final float y) {
        displayRect.offsetTo(displayRect.left, y);
    }

    public void setScale(final float newScale) {
        actualScale = newScale;
        displayRect.set(displayRect.left,
                displayRect.top,
                displayRect.left + naturalRect.width() * actualScale,
                displayRect.top + naturalRect.height() * actualScale);
    }





}
