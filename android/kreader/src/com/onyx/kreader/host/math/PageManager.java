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

    private RectF pagesBoundingRect = new RectF();

    /**
     * screen viewport rectangle in page coordinates system.
     */
    private RectF viewportRect = new RectF();

    private List<PageInfo> visible = new ArrayList<PageInfo>();
    private List<PageInfo> pageInfoList = new ArrayList<PageInfo>();
    private Map<String, PageInfo> pageInfoMap = new HashMap<String, PageInfo>();

    public void clear() {
        pageInfoList.clear();
        pagesBoundingRect.set(0, 0, 0, 0);
    }

    public void setViewportPosition(final float x, final float y) {
        viewportRect.offsetTo(x, y);
        collectVisiblePages();
        reboundViewport();
    }

    public void setViewportRect(final float left, final float top, final float right, final float bottom) {
        viewportRect.set(left, top, right, bottom);
        collectVisiblePages();
        reboundViewport();
    }

    public final RectF getViewportRect() {
        return viewportRect;
    }

    public final RectF getPagesBoundingRect() {
        return pagesBoundingRect;
    }

    public void panViewportPosition(final float dx, final float dy) {
        viewportRect.offset(dx, dy);
        collectVisiblePages();
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

    public boolean gotoPage(final String name) {
        PageInfo pageInfo = pageInfoMap.get(name);
        if (pageInfo == null) {
            return false;
        }
        updateForSpecialScale(pageInfo);
        setViewportPosition(pageInfo.getPositionRect().left, pageInfo.getPositionRect().top);
        return true;
    }

    private boolean updateForSpecialScale(final PageInfo pageInfo) {
        if (!isSpecialScale()) {
            return false;
        }
        if (isScaleToPage()) {
            scaleToPage(pageInfo.getName());
        } else if (isScaleToWidth()) {
            scaleToWidth(pageInfo.getName());
        } else if (isScaleToHeight()) {
            scaleToHeight(pageInfo.getName());
        }
        return true;
    }

    public final PageInfo getPageInfo(final String position) {
        return pageInfoMap.get(position);
    }

    public final List<PageInfo> getPageInfoList() {
        return pageInfoList;
    }

    public void setScale(final float scale) {
        specialScale = ReaderConstants.SCALE_INVALID;
        setScaleImpl(scale);
    }

    private void setScaleImpl(final float scale) {
        if (scale < 0) {
            return;
        }
        actualScale = scale;
        updatePagesBoundingRect();
        reboundViewport();
        collectVisiblePages();
    }


    public final float getActualScale() {
        return actualScale;
    }

    public boolean hasValidViewport() {
        return (viewportRect.width() > 0 && viewportRect.height() > 0);
    }

    public boolean scaleToPage(final String pageName) {
        specialScale = ReaderConstants.SCALE_TO_PAGE;
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pageName);
        setScaleImpl(PageUtils.scaleToPage(pageInfo.getOriginWidth(), pageInfo.getOriginHeight(), viewportRect.width(), viewportRect.height()));
        return true;
    }

    public boolean scaleToWidth(final String pageName) {
        specialScale = ReaderConstants.SCALE_TO_WIDTH;
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pageName);
        setScaleImpl(PageUtils.scaleToWidth(pageInfo.getOriginWidth(), viewportRect.width()));
        return true;
    }

    public boolean scaleToHeight(final String pageName) {
        specialScale = ReaderConstants.SCALE_TO_HEIGHT;
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pageName);
        setScaleImpl(PageUtils.scaleToHeight(pageInfo.getOriginHeight(), viewportRect.height()));
        return true;
    }

    /**
     * Scale the child rect to viewport.
     * @param child The user selected rect in host coordinates system.
     * @return true if succeed.
     */
    public boolean scaleToViewport(final String pageName, final RectF child) {
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pageName);
        setScale(pageInfo.getActualScale() * PageUtils.scaleByRect(child, viewportRect));
        return true;
    }

    public boolean scaleWithDelta(final String pageName, final float delta) {
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pageName);
        float newScale = pageInfo.getActualScale() + PageUtils.scaleWithDelta(pageInfo.getPositionRect(), getViewportRect(), delta);
        setScale(newScale);
        return true;
    }


    public boolean scaleByRect(final String pageName, final RectF ratio) {
        specialScale = ReaderConstants.SCALE_INVALID;
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pageName);
        setScale(PageUtils.scaleByRatio(ratio, pageInfo.getOriginWidth(), pageInfo.getOriginHeight(), viewportRect));
        return true;
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
     * collect visible page list in page list.
     */
    public List<PageInfo> collectVisiblePages() {
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
        collectVisiblePages();
        return true;
    }

    public boolean prevViewport() {
        if (viewportRect.top <= pagesBoundingRect.top) {
            return false;
        }
        viewportRect.offset(0, -viewportRect.height());
        reboundViewport();
        collectVisiblePages();
        return true;
    }

    public boolean isSpecialScale() {
        return specialScale < ReaderConstants.SCALE_INVALID;
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
