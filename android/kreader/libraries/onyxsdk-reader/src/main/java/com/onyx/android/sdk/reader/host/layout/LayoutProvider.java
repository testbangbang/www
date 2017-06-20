package com.onyx.android.sdk.reader.host.layout;

import android.graphics.RectF;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.math.PageManager;
import com.onyx.android.sdk.reader.host.math.PositionSnapshot;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

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

    public void deactivate() {

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
        return getCurrentPagePosition().equals(LayoutProviderUtils.firstPage(getLayoutManager()));
    }

    public boolean atLastPage() throws ReaderException {
        return getCurrentPagePosition().equals(LayoutProviderUtils.lastPage(getLayoutManager()));
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

    public boolean gotoPage(final int page) throws ReaderException {
        return gotoPosition(PagePositionUtils.fromPageNumber(page));
    }

    public boolean gotoPosition(final String position) throws ReaderException {
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

    public RectF getCropRect() throws ReaderException {
        return null;
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

    public boolean setStyle(final ReaderTextStyle style) throws ReaderException {
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
        if (!getLayoutManager().getReaderRendererFeatures().supportScale()) {
            gotoPosition(snapshot.pagePosition);
        } else {
            LayoutProviderUtils.addSinglePage(getLayoutManager(), snapshot.pagePosition);
            if (PageConstants.isSpecialScale(snapshot.specialScale)) {
                getPageManager().setSpecialScale(snapshot.pagePosition, snapshot.specialScale);
            } else {
                getPageManager().setScale(snapshot.pagePosition, snapshot.actualScale);
            }
            getPageManager().setAbsoluteViewportPosition(snapshot.viewport.left, snapshot.viewport.top);
        }
        return true;
    }

    public final String getCurrentPagePosition() {
        return getLayoutManager().getCurrentPagePosition();
    }

    public final int getCurrentPageNumber() {
        return getLayoutManager().getCurrentPageNumber();
    }

}
