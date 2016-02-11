package com.onyx.kreader.host.math;

import android.graphics.PointF;
import android.graphics.RectF;
import com.onyx.kreader.api.ReaderPagePosition;

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

    public static final int SCALE_TO_PAGE = -1;
    public static final int SCALE_TO_WIDTH = -2;
    public static final int ZOOM_TO_HEIGHT = -3;
    public static final int ZOOM_TO_PAGE_AUTO_CONTENT = -4;
    public static final int ZOOM_TO_WIDTH_AUTO_CONTENT = -5;
    public static final int ZOOM_TO_SCAN_REFLOW = -6;
    public static final int ZOOM_TO_REFLOW = -7;
    public static final int ZOOM_TO_COMICE = -8;
    public static final int ZOOM_TO_PAPER = -9;

    private int specialScale = 0;
    private float actualScale = 1.0f;
    private float topMargin, leftMargin, rightMargin, bottomMargin;
    private float spacing;

    private RectF pagesBoundingRect = new RectF();
    private RectF viewportRect = new RectF();

    private List<PageInfo> visible = new ArrayList<PageInfo>();
    private List<PageInfo> pageInfoList = new ArrayList<PageInfo>();
    private Map<String, PageInfo> pageInfoMap = new HashMap();

    public void clear() {
        pageInfoList.clear();
        pagesBoundingRect.set(0, 0, 0, 0);
    }

    public void setViewport(final float x, final float y) {
        viewportRect.offsetTo(x, y);
        reboundViewport();
    }

    public void setViewportRect(final float left, final float top, final float right, final float bottom) {
        viewportRect.set(left, top, right, bottom);
        reboundViewport();
    }

    public final RectF getViewportRect() {
        return viewportRect;
    }

    public final RectF getPagesBoundingRect() {
        return pagesBoundingRect;
    }

    public void panViewport(final float dx, final float dy) {
        viewportRect.offset(dx, dy);
        reboundViewport();
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
    }

    public boolean moveViewportByPosition(final String name) {
        PageInfo pageInfo = pageInfoMap.get(name);
        if (pageInfo == null) {
            return false;
        }
        setViewport(pageInfo.getPositionRect().left, pageInfo.getPositionRect().top);
        return true;
    }

    public final PageInfo getPageInfo(final ReaderPagePosition position) {
        return pageInfoMap.get(position);
    }

    public final List<PageInfo> getPageInfoList() {
        return pageInfoList;
    }

    public void setScale(final float scale) {
        actualScale = scale;
        updatePagesBoundingRect();
    }

    public boolean isSpecialScale() {
        return specialScale < 0;
    }

    public final float getActualScale() {
        return actualScale;
    }

    public boolean scaleToPage() {
        specialScale = SCALE_TO_PAGE;
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
        return true;
    }

    public boolean scaleToWidth() {
        specialScale = SCALE_TO_WIDTH;
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
        return true;
    }


    public boolean scaleByRatio(final RectF ratio) {
        PageInfo pageInfo = getFirstVisiblePage();
        if (pageInfo == null) {
            return false;
        }
        if (viewportRect.width() <= 0 || viewportRect.height() <= 0) {
            return false;
        }

        setScale(PageUtils.scaleByRatio(ratio, pageInfo.getOriginWidth(), pageInfo.getOriginHeight(), viewportRect));
        reboundViewport();
        return false;
    }

    public PageInfo hitTest(final float x, final float y) {
        for(PageInfo pageInfo : visible) {
            if (pageInfo.getPositionRect().contains(x, y)) {
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

    public List<PageInfo> getVisiblePages() {
        List<PageInfo> list = visible;
        if (list == null || list.size() <= 0) {
            return null;
        }
        for(PageInfo pageInfo : list) {
            pageInfo.updateDisplayRect(viewportRect);
        }
        return list;
    }


    /**
     * calculate the page bounding rectangle
     */
    public void updatePagesBoundingRect() {
        float y = topMargin, maxWidth = 0;
        for(PageInfo pageInfo : pageInfoList) {
            pageInfo.update(actualScale, 0, y);
            y += pageInfo.getDisplayHeight();
            if (maxWidth < pageInfo.getDisplayWidth()) {
                maxWidth = pageInfo.getDisplayWidth();
            }
            y += spacing;
        }
        maxWidth += leftMargin + rightMargin;
        pagesBoundingRect.set(0, 0, maxWidth, y);
        for(PageInfo pageInfo : pageInfoList) {
            float x = (maxWidth - pageInfo.getDisplayWidth()) / 2;
            pageInfo.setX(x);
        }
    }

    public boolean nextViewport() {
        if (viewportRect.bottom >= pagesBoundingRect.bottom) {
            return false;
        }
        viewportRect.offset(0, viewportRect.height());
        reboundViewport();
        return true;
    }

    public boolean prevViewport() {
        if (viewportRect.top <= pagesBoundingRect.top) {
            return false;
        }
        viewportRect.offset(0, -viewportRect.height());
        reboundViewport();
        return true;
    }


}
