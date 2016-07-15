package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.ReaderDrawContext;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.navigation.NavigationList;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/19/15.
 * Single hard page with sub screen navigation list support.
 */
public class LayoutSinglePageNavigationListProvider extends LayoutProvider {

    private NavigationArgs navigationArgs;

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

    private NavigationList getNavigationList() {
        return getNavigationArgs().getList();
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        navigationArgs = args;
        RectF subScreen = getNavigationList().first();
        getPageManager().scaleByRatioRect(getCurrentPageName(), subScreen);
        return true;
    }

    public boolean prevScreen() throws ReaderException {
        if (getNavigationList().hasPrevious()) {
            RectF subScreen = getNavigationList().previous();
            getPageManager().scaleByRatioRect(getCurrentPageName(), subScreen);
            return true;
        }
        return prevPage();
    }

    public boolean nextScreen() throws ReaderException {
        if (getNavigationList().hasNext()) {
            RectF subScreen = getNavigationList().next();
            getPageManager().scaleByRatioRect(getCurrentPageName(), subScreen);
            return true;
        }
        return nextPage();
    }

    public boolean prevPage() throws ReaderException {
        return gotoPositionImpl(LayoutProviderUtils.prevPage(getLayoutManager()), getNavigationList().getLastSubScreenIndex());
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

    public boolean drawVisiblePages(final Reader reader, final ReaderDrawContext drawContext, final ReaderBitmapImpl bitmap, final ReaderViewInfo readerViewInfo) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), drawContext, bitmap, readerViewInfo);
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

        RectF subScreen = getNavigationList().gotoSubScreen(index);
        getPageManager().scaleByRatioRect(getCurrentPageName(), subScreen);
        return true;
    }

    public boolean pan(int dx, int dy) throws ReaderException {
        LayoutProviderUtils.pan(getLayoutManager(), dx, dy);
        return true;
    }

    public boolean supportPreRender() throws ReaderException {
        return false;
    }

    public boolean supportSubScreenNavigation() {
        return true;
    }


    public boolean setStyle(final ReaderStyle style) throws ReaderException {
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
                getNavigationList().getCurrentIndex());
    }

    public boolean restoreBySnapshot(final PositionSnapshot snapshot) throws ReaderException {
        super.restoreBySnapshot(snapshot);
        getNavigationList().setCurrentIndex(snapshot.subScreenIndex);
        return true;
    }
}
