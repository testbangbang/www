package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.navigation.NavigationArgs;
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

    public boolean drawVisiblePages(ReaderBitmapImpl bitmap) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(getLayoutManager(), bitmap);
        return true;
    }

    public boolean setScale(float scale, float left, float top) throws ReaderException {
        getPageManager().setScale(scale);
        getPageManager().setViewportPosition(left, top);
        getPageManager().collectVisiblePages();
        return true;
    }

    public void scaleToPage(final String pageName) throws ReaderException {
        getPageManager().scaleToPage(pageName);
    }

    public void scaleToWidth(final String pageName) throws ReaderException {
        getPageManager().scaleToWidth(pageName);
    }

    public boolean changeScaleWithDelta(final String pageName, float delta) throws ReaderException {
        getPageManager().scaleWithDelta(pageName, delta);
        return true;
    }

    public boolean changeScaleByRect(final String pageName, final RectF rect) throws ReaderException  {
        getPageManager().scaleByRect(pageName, rect);
        return true;
    }

    public boolean gotoPosition(final String location) throws ReaderException {
        if (StringUtils.isNullOrEmpty(location)) {
            return false;
        }
        LayoutProviderUtils.addNewSinglePage(getLayoutManager(), location);
        return getPageManager().gotoPage(location);
    }

    public boolean pan(int dx, int dy) throws ReaderException {
        LayoutProviderUtils.pan(getLayoutManager(), dx, dy);
        return true;
    }

    public boolean supportPreRender() throws ReaderException {
        return false;
    }

    public boolean supportSubScreenNavigation() {
        return false;
    }

    public boolean setFontSize(float fontSize) throws ReaderException {
        return false;
    }

    public boolean setTypeface(final String typeface) throws ReaderException {
        return false;
    }

    public void save(int delta) throws ReaderException {

    }

    public void restore() throws ReaderException {

    }

    public String renderingString() throws ReaderException {
        return null;
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
}
