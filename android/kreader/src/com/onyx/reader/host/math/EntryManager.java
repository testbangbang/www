package com.onyx.reader.host.math;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 10/8/15.
 */
public class EntryManager {

    private float actualScale = 1.0f;
    private RectF hostRect = new RectF();
    private RectF viewportRect = new RectF();
    private float topMargin, leftMargin, rightMargin, bottomMargin;
    private float spacing;
    private List<EntryInfo> visible = new ArrayList<EntryInfo>();
    private List<EntryInfo> entryInfoList = new ArrayList<EntryInfo>();
    private Map<String, EntryInfo> entryInfoMap = new HashMap();

    public void clear() {
        entryInfoList.clear();
        hostRect.set(0, 0, 0, 0);
    }

    public void setViewport(final float x, final float y) {
        viewportRect.offsetTo(x, y);
    }

    public void setViewportRect(final float left, final float top, final float right, final float bottom) {
        viewportRect.set(left, top, right, bottom);
        reboundViewport();
    }

    public final RectF getViewportRect() {
        return viewportRect;
    }

    public final RectF getHostRect() {
        return hostRect;
    }

    public void panViewport(final float dx, final float dy) {
        viewportRect.offset(dx, dy);
        reboundViewport();
    }

    private void reboundViewport() {
        EntryUtils.rebound(viewportRect, hostRect);
    }

    public boolean contains(final String name) {
        return entryInfoMap.containsKey(name);
    }

    public void add(final String name, final EntryInfo entryInfo) {
        entryInfoMap.put(name, entryInfo);
        entryInfoList.add(entryInfo);
    }

    public final List<EntryInfo> getEntryInfoList() {
        return entryInfoList;
    }

    public void setScale(final float scale) {
        actualScale = scale;
        update();
    }

    public final float getActualScale() {
        return actualScale;
    }

    public boolean scaleToPage() {
        updateVisiblePages();
        if (visible.size() <= 0) {
            return false;
        }
        if (viewportRect.width() <= 0 || viewportRect.height() <= 0) {
            return false;
        }
        EntryInfo current = visible.get(0);
        setScale(EntryUtils.scaleToPage(current.getDisplayRect(), viewportRect));
        reboundViewport();
        return true;
    }

    public boolean scaleToWidth() {
        updateVisiblePages();
        if (visible.size() <= 0) {
            return false;
        }
        if (viewportRect.width() <= 0 || viewportRect.height() <= 0) {
            return false;
        }
        EntryInfo current = visible.get(0);
        setScale(EntryUtils.scaleToWidth(current.getDisplayRect(), viewportRect));
        reboundViewport();
        return true;
    }

    public boolean scaleToViewport(final RectF child) {
        updateVisiblePages();
        if (visible.size() <= 0) {
            return false;
        }
        if (viewportRect.width() <= 0 || viewportRect.height() <= 0) {
            return false;
        }
        setScale(actualScale * EntryUtils.scaleToPage(child, viewportRect));
        reboundViewport();
        return true;
    }

    /**
     * search in entry list to get
     */
    public List<EntryInfo> updateVisiblePages() {
        visible.clear();
        boolean found = false;
        for(EntryInfo entryInfo : entryInfoList) {
            if (RectF.intersects(viewportRect, entryInfo.getDisplayRect())) {
                visible.add(entryInfo);
                found = true;
            } else if (found) {
                break;
            }
        }
        return visible;
    }

    /**
     * calculate the host rectangle
     */
    public void update() {
        float y = topMargin, maxWidth = 0;
        for(EntryInfo entryInfo : entryInfoList) {
            entryInfo.update(actualScale, 0, y);
            y += entryInfo.getDisplayHeight();
            if (maxWidth < entryInfo.getDisplayWidth()) {
                maxWidth = entryInfo.getDisplayWidth();
            }
            y += spacing;
        }
        maxWidth += leftMargin + rightMargin;
        hostRect.set(0, 0, maxWidth, y);
        for(EntryInfo entryInfo : entryInfoList) {
            float x = (maxWidth - entryInfo.getDisplayWidth()) / 2;
            entryInfo.setX(x);
        }
    }

    public boolean nextViewport() {
        if (viewportRect.bottom >= hostRect.bottom) {
            return false;
        }
        viewportRect.offset(0, viewportRect.height());
        reboundViewport();
        return true;
    }

    public boolean prevViewport() {
        if (viewportRect.top <= hostRect.top) {
            return false;
        }
        viewportRect.offset(0, -viewportRect.height());
        reboundViewport();
        return true;
    }


}
