package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.StringUtils;


/**
 * Created by zhuzeng on 10/7/15.
 * forward to navigation manager. in hard page mode, the page is defined by document, instead of rendering.
 * In hard page mode, the navigation manager can still support single hard page and continuous page mode.
 *
 * Normal single hard page provider, it could be scaled.
 *
 */
public class LayoutSinglePageProvider extends LayoutProvider {

    public LayoutSinglePageProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public String getProviderName() {
        return ReaderConstants.SINGLE_PAGE;
    }

    public void activate()  {
        getPageManager().scaleToPage(null);
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    public boolean prevScreen() throws ReaderException {
        if (!getPageManager().prevViewport()) {
            return prevPage();
        }
        getPageManager().collectVisiblePages();
        return true;
    }

    public boolean nextScreen() throws ReaderException {
        if (!getPageManager().nextViewport()) {
            return nextPage();
        }
        getPageManager().collectVisiblePages();
        return true;
    }

    public boolean prevPage() throws ReaderException {
        return gotoPosition(LayoutProviderUtils.prevPage(getLayoutManager()));
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

    public boolean drawVisiblePages(final Reader reader, ReaderBitmapImpl bitmap) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), bitmap);
        return true;
    }

    public boolean setScale(final String pageName, float scale, float left, float top) throws ReaderException {
        LayoutProviderUtils.addSinglePage(getLayoutManager(), pageName);
        getPageManager().setScale(pageName, scale);
        getPageManager().setViewportPosition(pageName, left, top);
        getPageManager().collectVisiblePages();
        return true;
    }

    public void scaleToPage(final String pageName) throws ReaderException {
        LayoutProviderUtils.addSinglePage(getLayoutManager(), pageName);
        getPageManager().scaleToPage(pageName);
    }

    public void scaleToWidth(final String pageName) throws ReaderException {
        LayoutProviderUtils.addSinglePage(getLayoutManager(), pageName);
        getPageManager().scaleToWidth(pageName);
    }

    public boolean changeScaleWithDelta(final String pageName, float delta) throws ReaderException {
        LayoutProviderUtils.addSinglePage(getLayoutManager(), pageName);
        getPageManager().scaleWithDelta(pageName, delta);
        return true;
    }

    public boolean gotoPosition(final String location) throws ReaderException {
        if (StringUtils.isNullOrEmpty(location)) {
            return false;
        }
        LayoutProviderUtils.addSinglePage(getLayoutManager(), location);
        return getPageManager().gotoPage(location);
    }

    public boolean pan(int dx, int dy) throws ReaderException {
        LayoutProviderUtils.pan(getLayoutManager(), dx, dy);
        return true;
    }

    public boolean supportPreRender() throws ReaderException {
        return true;
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
        return PositionSnapshot.snapshot(getProviderName(), getPageManager().getFirstVisiblePage(), getPageManager().getSpecialScale());
    }

    public boolean restoreBySnapshot(final PositionSnapshot snapshot) throws ReaderException {
        if (ReaderConstants.isSpecialScale(snapshot.specialScale)) {
            getPageManager().setSpecialScale(snapshot.pageName, snapshot.specialScale);
        } else {
            getPageManager().setScale(snapshot.pageName, snapshot.actualScale);
        }
        getPageManager().setViewportPosition(snapshot.pageName, snapshot.displayRect.left, snapshot.displayRect.top);
        return true;
    }

    public RectF getPageRectOnViewport(final String position) throws ReaderException {
        final PageInfo pageInfo = getPageManager().getPageInfo(position);
        return pageInfo.getDisplayRect();
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
        LayoutProviderUtils.addSinglePage(getLayoutManager(), pageName);
        getPageManager().scaleToViewport(pageName, child);
    }
}
