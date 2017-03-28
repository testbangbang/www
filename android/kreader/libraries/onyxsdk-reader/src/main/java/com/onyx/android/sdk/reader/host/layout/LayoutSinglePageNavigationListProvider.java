package com.onyx.android.sdk.reader.host.layout;

import android.graphics.RectF;
import android.util.Log;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.host.math.PositionSnapshot;
import com.onyx.android.sdk.reader.host.navigation.NavigationList;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

import java.util.HashMap;

/**
 * Created by zhuzeng on 10/19/15.
 * Single hard page with sub screen navigation list support.
 */
public class LayoutSinglePageNavigationListProvider extends LayoutProvider {

    private NavigationArgs navigationArgs;

    // rect in percentage unit
    private HashMap<String, RectF> autoCropRatioRegions = new HashMap<>();

    public LayoutSinglePageNavigationListProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public String getProviderName() {
        return PageConstants.SINGLE_PAGE_NAVIGATION_LIST;
    }

    public void activate()  {
        getPageManager().setPageRepeat(0);
    }

    public NavigationArgs getNavigationArgs() {
        return navigationArgs;
    }

    private NavigationArgs.Type getPageType(int pageIndex) {
        return pageIndex % 2 == 0 ? NavigationArgs.Type.EVEN : NavigationArgs.Type.ODD;
    }

    private NavigationList getNavigationList(int pageIndex) {
        return getNavigationArgs().getListByType(getPageType(pageIndex));
    }

    private RectF autoCrop(RectF rect) {
        if (navigationArgs.isAutoCropForEachBlock()) {
            RectF cropRegion = autoCropRatioRegions.get(getCurrentPagePosition());
            if (cropRegion == null) {
                RectF limitedRect = getNavigationList(getCurrentPageNumber()).getLimitedRect();
                cropRegion = cropTargetRegion(getPageManager().getFirstVisiblePage(), limitedRect);
                autoCropRatioRegions.put(getCurrentPagePosition(), cropRegion);
            }
            rect.intersect(cropRegion);
        }
        return rect;
    }

    // ratio rect in percentage unit
    private RectF cropTargetRegion(PageInfo pageInfo, RectF targetRatioRect) {
        RectF cropRect = getPageManager().getCropProvider().cropPage(pageInfo, targetRatioRect);
        cropRect.set(cropRect.left / pageInfo.getOriginWidth(),
                cropRect.top / pageInfo.getOriginHeight(),
                cropRect.right / pageInfo.getOriginWidth(),
                cropRect.bottom / pageInfo.getOriginHeight());
        return cropRect;
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        navigationArgs = args;
        if (getLayoutManager().getCurrentPageInfo() != null) {
            RectF subScreen = autoCrop(getNavigationList(getCurrentPageNumber()).first());
            getPageManager().scaleByRatioRect(getCurrentPagePosition(), subScreen);
        }
        return true;
    }

    @Override
    public boolean canPrevScreen() throws ReaderException {
        return getNavigationList(getCurrentPageNumber()).hasPrevious() || !atFirstPage();
    }

    public boolean prevScreen() throws ReaderException {
        if (getNavigationList(getCurrentPageNumber()).hasPrevious()) {
            RectF subScreen = autoCrop(getNavigationList(getCurrentPageNumber()).previous());
            getPageManager().scaleByRatioRect(getCurrentPagePosition(), subScreen);
            return true;
        }
        return prevPage();
    }

    @Override
    public boolean canNextScreen() throws ReaderException {
        return getNavigationList(getCurrentPageNumber()).hasNext() || !atLastPage();
    }

    public boolean nextScreen() throws ReaderException {
        if (getNavigationList(getCurrentPageNumber()).hasNext()) {
            RectF subScreen = autoCrop(getNavigationList(getCurrentPageNumber()).next());
            getPageManager().scaleByRatioRect(getCurrentPagePosition(), subScreen);
            return true;
        }
        return nextPage();
    }

    public boolean prevPage() throws ReaderException {
        return gotoPositionImpl(LayoutProviderUtils.prevPage(getLayoutManager()), getNavigationList(getCurrentPageNumber()).getLastSubScreenIndex());
    }

    public boolean nextPage() throws ReaderException {
        return gotoPosition(LayoutProviderUtils.nextPage(getLayoutManager()));
    }

    public boolean firstPage() throws ReaderException {
        return gotoPosition(LayoutProviderUtils.firstPage(getLayoutManager()));
    }

    public boolean lastPage() throws ReaderException {
        return gotoPosition(LayoutProviderUtils.lastPage(getLayoutManager()));
    }

    public boolean drawVisiblePages(final Reader reader, final ReaderDrawContext drawContext, final ReaderViewInfo readerViewInfo) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), drawContext, readerViewInfo);
        return true;
    }

    public boolean setScale(final String pageName, float scale, float left, float top) throws ReaderException {
        getPageManager().setScale(pageName, scale);
        getPageManager().panViewportPosition(pageName, left, top);
        return true;
    }

    public void scaleToPage(final String pageName) throws ReaderException {
        // when scale to page, restore to single page mode?
        getPageManager().scaleToPage(pageName);
    }

    public void scaleToWidth(final String pageName) throws ReaderException {
        getPageManager().scaleToWidth(pageName);
    }

    public boolean changeScaleWithDelta(float delta) throws ReaderException {
        return false;
    }

    public boolean changeScaleByRect(final String position, final RectF rect) throws ReaderException  {
        return false;
    }

    public boolean gotoPosition(final String position) throws ReaderException {
        return gotoPositionImpl(position, 0);
    }

    private boolean gotoPositionImpl(final String position, int index) throws ReaderException {
        if (StringUtils.isNullOrEmpty(position)) {
            return false;
        }
        LayoutProviderUtils.addSinglePage(getLayoutManager(), position);
        if (!getPageManager().gotoPage(position)) {
            return false;
        }

        RectF subScreen = autoCrop(getNavigationList(getCurrentPageNumber()).gotoSubScreen(index));
        getPageManager().scaleByRatioRect(getCurrentPagePosition(), subScreen);
        return true;
    }

    public boolean pan(int dx, int dy) throws ReaderException {
        LayoutProviderUtils.pan(getLayoutManager(), dx, dy);
        return true;
    }

    public boolean supportPreRender() throws ReaderException {
        return true;
    }

    @Override
    public boolean supportScale() throws ReaderException {
        return true;
    }

    public boolean supportSubScreenNavigation() {
        return true;
    }


    public boolean setStyle(final ReaderTextStyle style) throws ReaderException {
        return false;
    }

    public RectF getPageRectOnViewport(final String position) throws ReaderException {
        return null;
    }

    public float getActualScale() throws ReaderException {
        return getPageManager().getActualScale();
    }

    public RectF getPageBoundingRect() throws ReaderException {
        return getPageManager().getPagesBoundingRect();
    }

    public RectF getViewportRect() throws ReaderException {
        return getPageManager().getViewportRect();
    }

    @Override
    public void updateViewportRect(RectF rect) throws ReaderException {
        getPageManager().setViewportRect(rect);
        RectF subScreen = autoCrop(getNavigationList(getCurrentPageNumber()).getCurrent());
        getPageManager().scaleByRatioRect(getCurrentPagePosition(), subScreen);
    }

    @Override
    public RectF getCropRect() throws ReaderException {
        return getPageManager().getChildRectFromRatio(getCurrentPagePosition(),
                getNavigationList(getCurrentPageNumber()).getCurrent());
    }

    public void scaleByRect(final String pageName, final RectF child) throws ReaderException {
        getPageManager().scaleToViewport(pageName, child);
    }

    public PositionSnapshot saveSnapshot() throws ReaderException {
        if (getPageManager().getFirstVisiblePage() == null) {
            return null;
        }
        return PositionSnapshot.snapshot(getProviderName(),
                getPageManager().getFirstVisiblePage(),
                getPageManager().getViewportRect(),
                getPageManager().getSpecialScale(),
                getNavigationList(getCurrentPageNumber()).getCurrentIndex());
    }

    public boolean restoreBySnapshot(final PositionSnapshot snapshot) throws ReaderException {
        super.restoreBySnapshot(snapshot);
        getNavigationList(getCurrentPageNumber()).setCurrentIndex(snapshot.subScreenIndex);
        return true;
    }
}
