package com.onyx.android.sdk.reader.host.math;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.utils.StringUtils;

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

    public final static int PAGE_REPEAT = 50;

    private int specialScale = PageConstants.SCALE_TO_PAGE;
    private float actualScale = 1.0f;
    private int specialScaleBackup = specialScale;
    private float actualScaleBackup = actualScale;
    private float topMargin = 0;
    private float leftMargin = 0;
    private float rightMargin = 0;
    private float bottomMargin = 0;
    private float spacing;
    private String firstVisiblePagePosition;
    private int pageRepeat = 0;

    private ArrayList<RectF> manualBoundingRegionList = new ArrayList<>();

    private RectF pagesBoundingRect = new RectF();
    private RectF pageCropRegion = new RectF();
    private RectF viewportRect = new RectF();   // screen viewport rectangle in document coordinates system.

    private List<PageInfo> visible = new ArrayList<PageInfo>();
    private List<PageInfo> pageInfoList = new ArrayList<PageInfo>();
    private Map<String, PageInfo> pageInfoMap = new HashMap<String, PageInfo>();
    private PageCropProvider cropProvider;

    static public abstract class PageCropProvider {

        public abstract RectF cropPage(final PageInfo pageInfo);

        public abstract RectF cropPage(final PageInfo pageInfo, final RectF targetRatioRegion);

        public abstract RectF cropWidth(final PageInfo pageInfo);
    }

    public void clear() {
        visible.clear();
        pageInfoList.clear();
        pageInfoMap.clear();
        pagesBoundingRect.set(0, 0, 0, 0);
        firstVisiblePagePosition = null;
    }

    public void setCropProvider(final PageCropProvider provider) {
        cropProvider = provider;
    }

    public PageCropProvider getCropProvider() {
        return cropProvider;
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

    public boolean contains(final String position) {
        return pageInfoMap.containsKey(position);
    }

    public void add(final PageInfo pageInfo) {
        PageInfo page = pageInfoMap.get(pageInfo.getPositionSafely());
        if (page == null) {
            pageInfoMap.put(pageInfo.getPositionSafely(), pageInfo);
            page = pageInfo;
        }
        pageInfoList.add(page);
    }

    /**
     * set viewport to page with specified offset in document coordinates system.
     * @param pagePosition
     * @param dx x offset inside page with document coordinates system.
     * @param dy y offset inside page with document coordinates system.
     */
    public void panViewportPosition(final String pagePosition, final float dx, final float dy) {
        if (!gotoPage(pagePosition)) {
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

    public boolean gotoPage(final String position) {
        PageInfo pageInfo = pageInfoMap.get(position);
        if (pageInfo == null) {
            return false;
        }
        firstVisiblePagePosition = position;
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
        setSpecialScale(pageInfo.getPositionSafely(), specialScale);
        return true;
    }

    public final PageInfo getPageInfo(final String pagePosition) {
        return pageInfoMap.get(pagePosition);
    }

    public final List<PageInfo> getPageInfoList() {
        return pageInfoList;
    }

    public void setScale(final String pagePosition, final float scale) {
        specialScale = PageConstants.SCALE_INVALID;
        setScaleImpl(pagePosition, scale);
    }

    private void setScaleImpl(final String pagePosition, final float scale) {
        // even scale is invalid, we need to update page position to maintain visible page data
        firstVisiblePagePosition = pagePosition;
        if (scale > 0) {
            actualScale = scale;
            onScaleChanged();
        }
        gotoPageImpl(getPageInfo(pagePosition));
    }

    public final float getActualScale() {
        return actualScale;
    }

    public int getSpecialScale() {
        return specialScale;
    }

    public void setSpecialScale(final String pagePosition, int scale) {
        if (PageConstants.isSpecialScale(scale)) {
            specialScale = scale;
        }
        if (isScaleToPage()) {
            scaleToPage(pagePosition);
        } else if (isScaleToWidth()) {
            scaleToWidth(pagePosition);
        } else if (isScaleToHeight()) {
            scaleToHeight(pagePosition);
        } else if (isScaleToPageContent()) {
            scaleToPageContent(pagePosition);
        } else if (isWidthCrop()) {
            scaleToWidthContent(pagePosition);
        }
    }

    public void saveScale() {
        specialScaleBackup = specialScale;
        actualScaleBackup = actualScale;
    }

    public void restoreScale() {
        specialScale = specialScaleBackup;
        actualScale = actualScaleBackup;
    }

    public boolean hasValidViewport() {
        return (viewportRect.width() > 0 && viewportRect.height() > 0);
    }

    public boolean scaleToPage(final String pagePosition) {
        specialScale = PageConstants.SCALE_TO_PAGE;
        if (!contains(pagePosition) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pagePosition);
        setScaleImpl(pagePosition, PageUtils.scaleToPage(pageInfo.getOriginWidth(), pageInfo.getOriginHeight(), viewportRect.width(), viewportRect.height()));
        return true;
    }

    public boolean scaleToWidth(final String pagePosition) {
        specialScale = PageConstants.SCALE_TO_WIDTH;
        if (!contains(pagePosition) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pagePosition);
        setScaleImpl(pagePosition, PageUtils.scaleToWidth(pageInfo.getOriginWidth(), viewportRect.width()));
        return true;
    }

    public boolean scaleToHeight(final String pagePosition) {
        specialScale = PageConstants.SCALE_TO_HEIGHT;
        if (!contains(pagePosition) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pagePosition);
        setScaleImpl(pagePosition, PageUtils.scaleToHeight(pageInfo.getOriginHeight(), viewportRect.height()));
        return true;
    }

    public boolean scaleToPageContent(final String pagePosition) {
        specialScale = PageConstants.SCALE_TO_PAGE_CONTENT;
        if (!contains(pagePosition) || !hasValidViewport()) {
            return false;
        }

        RectF region = getPageCropRegion(pagePosition);
        // crop region is in origin size, so just use scaleByRect to viewport.
        float scale = PageUtils.scaleByRect(region, viewportRect);
        setScaleImpl(pagePosition, scale);

        pageCropRegion = region;
        adjustCropRegionInViewport(pageCropRegion);
        return true;
    }

    public boolean scaleToWidthContent(final String pagePosition) {
        specialScale = PageConstants.SCALE_TO_WIDTH_CONTENT;
        if (!contains(pagePosition) || !hasValidViewport()) {
            return false;
        }

        RectF region = getPageCropRegion(pagePosition);
        float scale = PageUtils.scaleToWidth(region.width(), viewportRect.width());
        setScaleImpl(pagePosition, scale);

        region.set(region.left * scale, region.top * scale, region.right * scale, region.bottom * scale);
        pageCropRegion = region;
        adjustCropRegionInViewport(pageCropRegion);
        return true;
    }

    private RectF getPageCropRegion(String pagePosition) {
        PageInfo pageInfo = getPageInfo(pagePosition);
        if (pageInfo.getAutoCropContentRegion() == null || pageInfo.getAutoCropContentRegion().isEmpty()) {
            if (cropProvider == null) {
                Log.w(TAG, "Crop provider is null, use scale to page instead.");
            }
            if (cropProvider != null) {
                cropProvider.cropPage(pageInfo);
            }
        }

        return new RectF(pageInfo.getAutoCropContentRegion());
    }

    /**
     * make crop region center in center of viewport to make it looks nice
     * @param region
     */
    private void adjustCropRegionInViewport(RectF region) {
        float delta = (viewportRect.width() - region.width()) / 2;
        panViewportPosition(region.left - delta, region.top);
    }

    private void reboundViewportInPageCropRegion() {
        if (isScaleToPageContent() || isWidthCrop()) {
            PageUtils.rebound(viewportRect, pageCropRegion);
        }
    }

    private RectF getActualBoundingRect() {
        RectF boundingRect = pagesBoundingRect;
        if (isScaleToPageContent() || isWidthCrop()) {
            boundingRect = pageCropRegion;
        }
        return boundingRect;
    }

    /**
     * Scale the child rect to viewport.
     * @param child The user selected rect in document coordinates system.
     * @return true if succeed.
     */
    public boolean scaleToViewport(final String pagePosition, final RectF child) {
        if (!contains(pagePosition) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pagePosition);
        float deltaScale = PageUtils.scaleByRect(child, viewportRect);
        float newScale = pageInfo.getActualScale() * deltaScale;
        setScale(pagePosition, newScale);

        // adjust viewport, since viewport is changed in setSpecialScale.
        float viewportLeft  = child.centerX() - viewportRect.width() / 2;
        float viewportTop = child.centerY() - viewportRect.height() / 2;
        viewportRect.offsetTo(viewportLeft, viewportTop);
        onViewportChanged();
        return true;
    }

    public boolean scaleWithDelta(final String pagePosition, final float delta) {
        specialScale = PageConstants.SCALE_INVALID;
        if (!contains(pagePosition) || !hasValidViewport()) {
            return false;
        }
        PageInfo pageInfo = getPageInfo(pagePosition);
        float newScale = pageInfo.getActualScale() + PageUtils.scaleWithDelta(pageInfo.getPositionRect(), getViewportRect(), delta);
        setScale(pagePosition, newScale);
        return true;
    }

    public RectF getChildRectFromRatio(final String pagePosition, final RectF ratio) {
        if (!contains(pagePosition) || !hasValidViewport()) {
            return null;
        }
        PageInfo pageInfo = getPageInfo(pagePosition);
        return new RectF(pageInfo.getPositionRect().width() * ratio.left,
                pageInfo.getPositionRect().height() * ratio.top,
                pageInfo.getPositionRect().width() * ratio.right,
                pageInfo.getPositionRect().height() * ratio.bottom);
    }

    public boolean scaleByRatioRect(final String pagePosition, final RectF ratio) {
        RectF child = getChildRectFromRatio(pagePosition, ratio);
        if (child == null) {
            return false;
        }
        scaleToViewport(pagePosition, child);
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
        if (StringUtils.isNotBlank(firstVisiblePagePosition)) {
            return pageInfoMap.get(firstVisiblePagePosition);
        }
        return null;
    }

    public String getFirstVisiblePagePosition() {
        return firstVisiblePagePosition;
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
                PageUtils.updateDisplayRect(pageInfo, viewportRect);
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
        reboundViewportInPageCropRegion();
    }

    public boolean canNextViewport() {
        RectF boundingRect = getActualBoundingRect();
        return (int)viewportRect.bottom < (int)boundingRect.bottom;
    }

    public boolean nextViewport() {
        RectF boundingRect = getActualBoundingRect();
        if ((int)viewportRect.bottom >= (int)boundingRect.bottom) {
            return false;
        }
        viewportRect.offset(0, viewportRect.height() - pageRepeat);
        onViewportChanged();
        reboundViewportInPageCropRegion();
        return true;
    }

    public boolean canPrevViewport() {
        RectF boundingRect = getActualBoundingRect();
        return (int)viewportRect.top > (int)boundingRect.top;
    }

    public boolean prevViewport() {
        RectF boundingRect = getActualBoundingRect();
        if ((int)viewportRect.top <= (int)boundingRect.top) {
            return false;
        }
        viewportRect.offset(0, -viewportRect.height() + pageRepeat);
        onViewportChanged();
        reboundViewportInPageCropRegion();
        return true;
    }

    public boolean isSpecialScale() {
        return PageConstants.isSpecialScale(specialScale);
    }

    public boolean isScaleToPage() {
        return PageConstants.isScaleToPage(specialScale);
    }

    public boolean isScaleToWidth() {
        return PageConstants.isScaleToWidth(specialScale);
    }

    public boolean isScaleToHeight() {
        return PageConstants.isScaleToHeight(specialScale);
    }

    public boolean isScaleToPageContent() {
        return PageConstants.isScaleToPageContent(specialScale);
    }

    public boolean isWidthCrop() {
        return PageConstants.isWidthCrop(specialScale);
    }

    public void setPageRepeat(int value) {
        pageRepeat = value;
    }

}
