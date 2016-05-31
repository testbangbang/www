package com.onyx.kreader.host.math;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.utils.StringUtils;

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
    static private final String TAG = PageManager.class.getSimpleName();
    private int specialScale = ReaderConstants.SCALE_TO_PAGE;
    private float actualScale = 1.0f;
    private float topMargin = 0;
    private float leftMargin = 0;
    private float rightMargin = 0;
    private float bottomMargin = 0;
    private float spacing;
    private String firstVisiblePageName;
    private int pageRepeat = 0;

    private RectF pagesBoundingRect = new RectF();
    private RectF viewportRect = new RectF();   // screen viewport rectangle in document coordinates system.

    private List<PageInfo> visible = new ArrayList<PageInfo>();
    private List<PageInfo> pageInfoList = new ArrayList<PageInfo>();
    private Map<String, PageInfo> pageInfoMap = new HashMap<String, PageInfo>();
    private PageCropProvider cropProvider;

    static public abstract class PageCropProvider {

        public abstract float cropPage(final float displayWidth, final float displayHeight, final PageInfo pageInfo);

    }

    public void clear() {
        visible.clear();
        pageInfoList.clear();
        pagesBoundingRect.set(0, 0, 0, 0);
        firstVisiblePageName = null;
    }

    public void setCropProvider(final PageCropProvider provider) {
        cropProvider = provider;
    }

    public void setViewportRect(final RectF rect) {
        viewportRect.set(rect);
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

    /**
     * set viewport to page with specified offset in document coordinates system.
     * @param pageName
     * @param dx x offset inside page with document coordinates system.
     * @param dy y offset inside page with document coordinates system.
     */
    public void panViewportPosition(final String pageName, final float dx, final float dy) {
        if (!gotoPage(pageName)) {
            return;
        }
        panViewportPosition(dx, dy);
    }

    /**
     * set absolute viewport position.
     * @param absoluteLeft the absolute left.
     * @param absoluteTop the absolute top.
     */
    public void setAbsoluteViewportPosition(final float absoluteLeft, final float absoluteTop) {
        viewportRect.offsetTo(absoluteLeft, absoluteTop);
        onViewportChanged();
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
        setSpecialScale(pageInfo.getName(), specialScale);
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

    public int getSpecialScale() {
        return specialScale;
    }

    public void setSpecialScale(final String pageName, int scale) {
        if (ReaderConstants.isSpecialScale(scale)) {
            specialScale = scale;
        }
        if (isScaleToPage()) {
            scaleToPage(pageName);
        } else if (isScaleToWidth()) {
            scaleToWidth(pageName);
        } else if (isScaleToHeight()) {
            scaleToHeight(pageName);
        } else if (isScaleToPageContent()) {
            scaleToPageContent(pageName);
        }
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

    public boolean scaleToPageContent(final String pageName) {
        specialScale = ReaderConstants.SCALE_TO_PAGE_CONTENT;
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }

        PageInfo pageInfo = getPageInfo(pageName);
        if (pageInfo.getAutoCropContentRegion() == null || pageInfo.getAutoCropContentRegion().isEmpty()) {
            if (cropProvider == null) {
                Log.w(TAG, "Crop provider is null, use scale to page instead.");
            }
            if (cropProvider != null) {
                cropProvider.cropPage(viewportRect.width(), viewportRect.height(), pageInfo);
            }
        }

        // crop region is in origin size, so just use scaleByRect to viewport.
        RectF region = new RectF(pageInfo.getAutoCropContentRegion());
        float scale = PageUtils.scaleByRect(region, viewportRect);
        setScaleImpl(pageName, scale);

        // make crop region center in center of viewport to make it looks nice
        float delta = (viewportRect.width() - region.width()) / 2;
        panViewportPosition(region.left - delta, region.top);
        return true;
    }

    /**
     * Scale the child rect to viewport.
     * @param child The user selected rect in document coordinates system.
     * @return true if succeed.
     */
    public boolean scaleToViewport(final String pageName, final RectF child) {
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pageName);
        float deltaScale = PageUtils.scaleByRect(child, viewportRect);
        float newScale = pageInfo.getActualScale() * deltaScale;
        setScale(pageName, newScale);

        // adjust viewport, since viewport is changed in setScale.
        float viewportLeft  = child.centerX() - viewportRect.width() / 2;
        float viewportTop = child.centerY() - viewportRect.height() / 2;
        viewportRect.offsetTo(viewportLeft, viewportTop);
        onViewportChanged();
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

    public boolean scaleByRatioRect(final String pageName, final RectF ratio) {
        if (!contains(pageName) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pageName);
        RectF child = new RectF(pageInfo.getPositionRect().width() * ratio.left,
                pageInfo.getPositionRect().height() * ratio.top,
                pageInfo.getPositionRect().width() * ratio.right,
                pageInfo.getPositionRect().height() * ratio.bottom);
        scaleToViewport(pageName, child);
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
        if (StringUtils.isNotBlank(firstVisiblePageName)) {
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
        y += bottomMargin;
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

    public void moveViewportToEndOfPage() {
        PageUtils.alignToBottom(viewportRect, pagesBoundingRect);
        onViewportChanged();
    }

    public boolean nextViewport() {
        if ((int)viewportRect.bottom >= (int)pagesBoundingRect.bottom) {
            return false;
        }
        viewportRect.offset(0, viewportRect.height() - pageRepeat);
        onViewportChanged();
        return true;
    }

    public boolean prevViewport() {
        if ((int)viewportRect.top <= (int)pagesBoundingRect.top) {
            return false;
        }
        viewportRect.offset(0, -viewportRect.height() + pageRepeat);
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

    public boolean isScaleToPageContent() {
        return specialScale == ReaderConstants.SCALE_TO_PAGE_CONTENT;
    }

    public boolean isWidthCrop() {
        return specialScale == ReaderConstants.SCALE_TO_WIDTH_CONTENT;
    }

    public void setPageRepeat(int value) {
        pageRepeat = value;
    }

}
