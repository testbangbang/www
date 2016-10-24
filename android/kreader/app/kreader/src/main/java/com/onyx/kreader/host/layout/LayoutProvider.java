package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.ReaderDrawContext;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.math.PageManager;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/7/15.
 */
public class LayoutProvider {

    private ReaderLayoutManager layoutManager;

    public LayoutProvider(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public String getProviderName() {
        return null;
    }

    public void activate() {
    }

    public boolean canHitTest() {
        return true;
    }

    public ReaderLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public PageManager getPageManager() {
        return getLayoutManager().getPageManager();
    }

    public NavigationArgs getNavigationArgs() {
        return null;
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    public boolean atFirstPage() throws ReaderException {
        return getCurrentPageName().equals(LayoutProviderUtils.firstPage(getLayoutManager()));
    }

    public boolean atLastPage() throws ReaderException {
        return getCurrentPageName().equals(LayoutProviderUtils.lastPage(getLayoutManager()));
    }

    public boolean canPrevScreen() throws ReaderException {
        return false;
    }

    public boolean prevScreen() throws ReaderException {
        return false;
    }

    public boolean canNextScreen() throws ReaderException {
        return false;
    }

    public boolean nextScreen() throws ReaderException {
        return false;
    }

    public boolean prevPage() throws ReaderException {
        return false;
    }

    public boolean nextPage() throws ReaderException {
        return false;
    }

    public boolean firstPage() throws ReaderException {
        return false;
    }

    public boolean lastPage() throws ReaderException {
        return false;
    }

    public boolean gotoPosition(final String location) throws ReaderException {
        return false;
    }

    public boolean setScale(final String pageName, float scale, float left, float top) throws ReaderException {
        return false;
    }

    public void scaleToPage(final String pageName) throws ReaderException {
    }

    public void scaleToWidth(final String pageName) throws ReaderException {
    }

    public void scaleToPageContent(final String pageName) throws ReaderException {
    }

    public void scaleToWidthContent(final String pageName) throws ReaderException {
    }

    public void scaleByRect(final String pageName, final RectF child) throws ReaderException {
    }

    public boolean changeScaleWithDelta(final String pageName, float delta) throws ReaderException {
        return false;
    }

    public boolean drawVisiblePages(final Reader reader, final ReaderDrawContext drawContext, final ReaderViewInfo readerViewInfo) throws ReaderException {
        return false;
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

    public void updateViewportRect(RectF rect) throws ReaderException {
        getPageManager().setViewportRect(rect);
    }

    public RectF getPageRectOnViewport(final String position) throws ReaderException {
        return null;
    }

    public boolean pan(int dx, int dy) throws ReaderException {
        return false;
    }

    public boolean supportPreRender() throws ReaderException {
        return false;
    }

    public boolean supportScale() throws ReaderException {
        return false;
    }

    public boolean supportSubScreenNavigation() {
        return false;
    }

    public boolean setStyle(final ReaderStyle style) throws ReaderException {
        return false;
    }

    public PositionSnapshot saveSnapshot() throws ReaderException {
        if (getPageManager().getFirstVisiblePage() == null) {
            return null;
        }
        return PositionSnapshot.snapshot(getProviderName(),
                getPageManager().getFirstVisiblePage(),
                getPageManager().getViewportRect(),
                getPageManager().getSpecialScale());
    }

    public boolean restoreBySnapshot(final PositionSnapshot snapshot) throws ReaderException {
        LayoutProviderUtils.addSinglePage(getLayoutManager(), snapshot.pageName);
        if (PageConstants.isSpecialScale(snapshot.specialScale)) {
            getPageManager().setSpecialScale(snapshot.pageName, snapshot.specialScale);
        } else {
            getPageManager().setScale(snapshot.pageName, snapshot.actualScale);
        }
        getPageManager().setAbsoluteViewportPosition(snapshot.viewport.left, snapshot.viewport.top);
        return true;
    }

    public final String getCurrentPageName() {
        return getLayoutManager().getCurrentPageName();
    }

}
