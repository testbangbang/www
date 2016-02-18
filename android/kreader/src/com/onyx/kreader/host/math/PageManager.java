package com.onyx.kreader.host.math;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.utils.StringUtils;
import com.onyx.kreader.utils.TestUtils;

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
    private float topMargin = 0;
    private float leftMargin = 0;
    private float rightMargin = 0;
    private float bottomMargin = 0;
    private float spacing;
    private String firstVisiblePageName;

    private RectF pagesBoundingRect = new RectF();

    /**
     * screen viewport rectangle in page coordinates system.
     */
    private RectF viewportRect = new RectF();

    private List<PageInfo> visible = new ArrayList<PageInfo>();
    private List<PageInfo> pageInfoList = new ArrayList<PageInfo>();
    private Map<String, PageInfo> pageInfoMap = new HashMap<String, PageInfo>();

    public void clear() {
        pageInfoMap.clear();
        visible.clear();
        pageInfoList.clear();
        pagesBoundingRect.set(0, 0, 0, 0);
        firstVisiblePageName = null;
    }

    public void setViewportRect(final float left, final float top, final float right, final float bottom) {
        viewportRect.set(left, top, right, bottom);
        onViewportChanged();
    }

    public final RectF getViewportRect() {
        return viewportRect;
    }

    public final RectF getPagesBoundingRect() {
        return pagesBoundingRect;
    }

    public void panViewportPosition(final float dx, final float dy) {
        viewportRect.offset(dx, dy);
        onViewportChanged();
    }

    public boolean contains(final String name) {
        return pageInfoMap.containsKey(name);
    }

    public void add(final PageInfo pageInfo) {
        pageInfoMap.put(pageInfo.getName(), pageInfo);
        pageInfoList.add(pageInfo);
    }

    public void setViewportPosition(final String pageName, final float x, final float y) {
        if (!gotoPage(pageName)) {
            return;
        }
        panViewportPosition(x, y);
    }

    private void gotoPageImpl(final PageInfo pageInfo) {
        if (pageInfo == null) {
            return;
        }
        viewportRect.offsetTo(pageInfo.getPositionRect().left, pageInfo.getPositionRect().top);
        onViewportChanged();
    }

    public boolean gotoPage(final String name) {
        PageInfo pageInfo = pageInfoMap.get(name);
        if (pageInfo == null) {
            return false;
        }
        firstVisiblePageName = name;
        if (updateForSpecialScale(pageInfo)) {
            return true;
        }
        gotoPageImpl(pageInfo);
        return true;
    }

    private void onScaleChanged() {
        updatePagesBoundingRect();
    }

    /**
     * when viewport changed,
     * - we collect visible pages
     * - rebound viewport
     * - update visible pages display rectangle according to viewport
     */
    private void onViewportChanged() {
        collectVisiblePages();
        if (reboundViewport()) {
            collectVisiblePages();
        }
    }

    private boolean reboundViewport() {
        return PageUtils.rebound(viewportRect, pagesBoundingRect);
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

    public final PageInfo getPageInfo(final String pageName) {
        return pageInfoMap.get(pageName);
    }

    public final List<PageInfo> getPageInfoList() {
        return pageInfoList;
    }

    public void setScale(final String pageName, final float scale) {
        specialScale = ReaderConstants.SCALE_INVALID;
        setScaleImpl(pageName, scale);
    }

    private void setScaleImpl(final String pageName, final float scale) {
        if (scale < 0) {
            return;
        }
        firstVisiblePageName = pageName;
        actualScale = scale;
        onScaleChanged();
        gotoPageImpl(getPageInfo(pageName));
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
        setScaleImpl(pageName, PageUtils.scaleToPage(pageInfo.getOriginWidth(), pageInfo.getOriginHeight(), viewportRect.width(), viewportRect.height()));
        return true;
    }

    public boolean scaleToWidth(final String pageName) {
        specialScale = ReaderConstants.SCALE_TO_WIDTH;
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pageName);
        setScaleImpl(pageName, PageUtils.scaleToWidth(pageInfo.getOriginWidth(), viewportRect.width()));
        return true;
    }

    public boolean scaleToHeight(final String pageName) {
        specialScale = ReaderConstants.SCALE_TO_HEIGHT;
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pageName);
        setScaleImpl(pageName, PageUtils.scaleToHeight(pageInfo.getOriginHeight(), viewportRect.height()));
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
        setScale(pageName, pageInfo.getActualScale() * PageUtils.scaleByRect(child, viewportRect));
        return true;
    }

    public boolean scaleWithDelta(final String pageName, final float delta) {
        specialScale = ReaderConstants.SCALE_INVALID;
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pageName);
        float newScale = pageInfo.getActualScale() + PageUtils.scaleWithDelta(pageInfo.getPositionRect(), getViewportRect(), delta);
        setScale(pageName, newScale);
        return true;
    }

    public boolean scaleByRect(final String pageName, final RectF ratio) {
        specialScale = ReaderConstants.SCALE_INVALID;
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pageName);
        setScale(pageName, PageUtils.scaleByRatio(ratio, pageInfo.getOriginWidth(), pageInfo.getOriginHeight(), viewportRect));
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

    public PageInfo getFirstVisiblePage() {
        if (StringUtils.isNonBlank(firstVisiblePageName)) {
            return pageInfoMap.get(firstVisiblePageName);
        }
        return null;
    }

    public String getFirstVisiblePageName() {
        return firstVisiblePageName;
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

    public boolean nextViewport() {
        if (viewportRect.bottom >= pagesBoundingRect.bottom) {
            return false;
        }
        viewportRect.offset(0, viewportRect.height());
        onViewportChanged();
        return true;
    }

    public boolean prevViewport() {
        if (viewportRect.top <= pagesBoundingRect.top) {
            return false;
        }
        viewportRect.offset(0, -viewportRect.height());
        onViewportChanged();
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
