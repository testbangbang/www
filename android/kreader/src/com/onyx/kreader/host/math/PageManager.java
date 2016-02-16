package com.onyx.kreader.host.math;

import android.graphics.PointF;
import android.graphics.RectF;
import com.onyx.kreader.host.options.ReaderConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 10/8/15.
 * All pages are arranged in page bounding coordinates system. The plugin needs to translate that
 * into its own coordinates system, for example, document coordinates system.
 */
public class PageManager {

    private int specialScale = ReaderConstants.SCALE_TO_PAGE;
    private float actualScale = 1.0f;
    private float topMargin, leftMargin, rightMargin, bottomMargin;
    private float spacing;
    private boolean dirty = false;

    private RectF pagesBoundingRect = new RectF();

    /**
     * screen viewport rectangle in page coordinates system.
     */
    private RectF viewportRect = new RectF();

    private List<PageInfo> visible = new ArrayList<PageInfo>();
    private List<PageInfo> pageInfoList = new ArrayList<PageInfo>();
    private Map<String, PageInfo> pageInfoMap = new HashMap<String, PageInfo>();

    private void setDirty() {
        dirty = true;
    }

    private void clearDirty() {
        dirty = false;
    }

    public void clear() {
        pageInfoList.clear();
        pagesBoundingRect.set(0, 0, 0, 0);
    }

    public void setViewportPosition(final float x, final float y) {
        viewportRect.offsetTo(x, y);
        setDirty();
    }

    public void setViewportRect(final float left, final float top, final float right, final float bottom) {
        viewportRect.set(left, top, right, bottom);
        setDirty();
    }

    public final RectF getViewportRect() {
        return viewportRect;
    }

    public final RectF getPagesBoundingRect() {
        return pagesBoundingRect;
    }

    public void panViewportPosition(final float dx, final float dy) {
        viewportRect.offset(dx, dy);
        setDirty();
    }

    private void reboundViewport() {
        PageUtils.rebound(viewportRect, pagesBoundingRect);
    }

    public boolean contains(final String name) {
        return pageInfoMap.containsKey(name);
    }

    public void add(final PageInfo pageInfo) {
        pageInfoMap.put(pageInfo.getName(), pageInfo);
        pageInfoList.add(pageInfo);
        setDirty();
    }

    public boolean gotoPage(final String name) {
        PageInfo pageInfo = pageInfoMap.get(name);
        if (pageInfo == null) {
            return false;
        }
        setViewportPosition(pageInfo.getPositionRect().left, pageInfo.getPositionRect().top);
        return true;
    }

    public final PageInfo getPageInfo(final String position) {
        return pageInfoMap.get(position);
    }

    public final List<PageInfo> getPageInfoList() {
        return pageInfoList;
    }

    public void setScale(final float scale) {
        if (scale < 0) {
            return;
        }
        actualScale = scale;
        specialScale = 0;
        setDirty();
    }

    public final float getActualScale() {
        return actualScale;
    }

    public boolean scaleToPage() {
        specialScale = ReaderConstants.SCALE_TO_PAGE;
        setDirty();
        updateVisiblePages();
        if (visible.size() <= 0) {
            return false;
        }
        if (viewportRect.width() <= 0 || viewportRect.height() <= 0) {
            return false;
        }
        PageInfo current = visible.get(0);
        setScale(PageUtils.scaleToPage(current.getOriginWidth(), current.getOriginHeight(), viewportRect.width(), viewportRect.height()));
        reboundViewport();
        updateVisiblePages();
        return true;
    }

    public boolean scaleToWidth() {
        specialScale = ReaderConstants.SCALE_TO_WIDTH;
        updateVisiblePages();
        if (visible.size() <= 0) {
            return false;
        }
        if (viewportRect.width() <= 0 || viewportRect.height() <= 0) {
            return false;
        }
        PageInfo current = visible.get(0);
        setScale(PageUtils.scaleToWidth(current.getOriginWidth(), viewportRect));
        reboundViewport();
        updateVisiblePages();
        return true;
    }

    /**
     * Scale the child rect to viewport.
     * @param child The user selected rect in host coordinates system.
     * @return true if succeed.
     */
    public boolean scaleToViewport(final RectF child) {
        updateVisiblePages();
        if (visible.size() <= 0) {
            return false;
        }
        if (viewportRect.width() <= 0 || viewportRect.height() <= 0) {
            return false;
        }

        setScale(actualScale * PageUtils.scaleByRect(child, viewportRect));
        reboundViewport();
        updateVisiblePages();
        return true;
    }

    public boolean scaleWithDelta(final float delta) {
        updateVisiblePages();
        if (visible.size() <= 0) {
            return false;
        }
        if (viewportRect.width() <= 0 || viewportRect.height() <= 0) {
            return false;
        }

        actualScale += PageUtils.scaleWithDelta(getFirstVisiblePage().getPositionRect(), getViewportRect(), delta);
        setScale(actualScale);
        reboundViewport();
        updateVisiblePages();
        return true;
    }


    public boolean scaleByRect(final RectF ratio) {
        PageInfo pageInfo = getFirstVisiblePage();
        if (pageInfo == null) {
            return false;
        }
        if (viewportRect.width() <= 0 || viewportRect.height() <= 0) {
            return false;
        }

        setScale(PageUtils.scaleByRatio(ratio, pageInfo.getOriginWidth(), pageInfo.getOriginHeight(), viewportRect));
        reboundViewport();
        updateVisiblePages();
        return false;
    }

    public PageInfo hitTest(final float x, final float y) {
        for(PageInfo pageInfo : visible) {
            if (pageInfo.getDisplayRect().contains(x, y)) {
                return pageInfo;
            }
        }
        return null;
    }

    /**
     * viewport to point in page coordinates system.
     * @param viewportPoint
     * @return
     */
    public PointF viewportToPage(final PointF viewportPoint) {
        for(PageInfo pageInfo : visible) {
            if (pageInfo.getPositionRect().contains(viewportPoint.x, viewportPoint.y)) {
                viewportPoint.offset(-pageInfo.getPositionRect().left, -pageInfo.getPositionRect().top);
                return viewportPoint;
            }
        }
        return null;
    }

    /**
     * search in page list to get visible pages.
     */
    public List<PageInfo> updateVisiblePages() {
        visible.clear();
        boolean found = false;
        for(PageInfo pageInfo : pageInfoList) {
            if (RectF.intersects(viewportRect, pageInfo.getPositionRect())) {
                visible.add(pageInfo);
                pageInfo.updateDisplayRect(viewportRect);
                found = true;
            } else if (found) {
                break;
            }
        }
        return visible;
    }

    public PageInfo getFirstVisiblePage() {
        List<PageInfo> list = getVisiblePages();
        if (list == null || list.size() <= 0) {
            return null;
        }
        return list.get(0);
    }

    public String getFirstVisiblePageName() {
        PageInfo pageInfo = getFirstVisiblePage();
        if (pageInfo == null) {
            return null;
        }
        return pageInfo.getName();
    }

    public List<PageInfo> getVisiblePages() {
        return visible;
    }

    /**
     * calculate the page bounding rectangle
     */
    public void updatePagesBoundingRect() {
        float y = topMargin, maxWidth = 0;
        for(PageInfo pageInfo : pageInfoList) {
            pageInfo.update(actualScale, 0, y);
            y += pageInfo.getScaledHeight();
            if (maxWidth < pageInfo.getScaledWidth()) {
                maxWidth = pageInfo.getScaledWidth();
            }
            y += spacing;
        }
        maxWidth += leftMargin + rightMargin;
        pagesBoundingRect.set(0, 0, maxWidth, y);
        for(PageInfo pageInfo : pageInfoList) {
            float x = (maxWidth - pageInfo.getScaledWidth()) / 2;
            pageInfo.setX(x);
        }
    }

    public boolean nextViewport() {
        if (viewportRect.bottom >= pagesBoundingRect.bottom) {
            return false;
        }
        viewportRect.offset(0, viewportRect.height());
        reboundViewport();
        updateVisiblePages();
        return true;
    }

    public boolean prevViewport() {
        if (viewportRect.top <= pagesBoundingRect.top) {
            return false;
        }
        viewportRect.offset(0, -viewportRect.height());
        reboundViewport();
        updateVisiblePages();
        return true;
    }

    public boolean isSpecialScale() {
        return specialScale < 0;
    }

    public boolean isScaleToPage() {
        return specialScale == ReaderConstants.SCALE_TO_PAGE;
    }

    public boolean isScaleToWidth() {
        return specialScale == ReaderConstants.SCALE_TO_WIDTH;
    }

    public boolean isScaleToHeight() {
        return specialScale == ReaderConstants.SCALE_TO_HEIGHT;
    }

    public boolean isPageCrop() {
        return specialScale == ReaderConstants.SCALE_TO_PAGE_CONTENT;
    }

    public boolean isWidthCrop() {
        return specialScale == ReaderConstants.SCALE_TO_WIDTH_CONTENT;
    }

}
