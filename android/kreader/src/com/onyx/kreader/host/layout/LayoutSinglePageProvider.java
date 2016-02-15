package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.host.math.PageManager;
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
        getPageManager().scaleToPage();
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    public boolean prevScreen() throws ReaderException {
        if (!getPageManager().prevViewport()) {
            return prevPage();
        }
        getPageManager().updateVisiblePages();
        return true;
    }

    public boolean nextScreen() throws ReaderException {
        if (!getPageManager().nextViewport()) {
            return nextPage();
        }
        getPageManager().updateVisiblePages();
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

    public boolean drawVisiblePages(ReaderBitmap bitmap) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(getLayoutManager(), bitmap);
        return true;
    }

    public boolean setScale(float scale, float left, float top) throws ReaderException {
        getPageManager().setScale(scale);
        getPageManager().setViewport(left, top);
        getPageManager().updateVisiblePages();
        return true;
    }

    public void scaleToPage() throws ReaderException {
        getPageManager().scaleToPage();
    }

    public void scaleToWidth() throws ReaderException {
        getPageManager().scaleToWidth();
    }

    public boolean changeScaleWithDelta(float delta) throws ReaderException {
        getPageManager().scaleWithDelta(delta);
        return true;
    }

    public boolean changeScaleByRect(final String position, final RectF rect) throws ReaderException  {
        getPageManager().scaleByRatio(rect);
        return true;
    }

    public boolean gotoPosition(final String location) throws ReaderException {
        if (StringUtils.isNullOrEmpty(location)) {
            return false;
        }
        LayoutProviderUtils.addNewSinglePage(getLayoutManager(), location);
        return getPageManager().moveViewportByPosition(location);
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

    public void scaleByRect(final RectF child) throws ReaderException {
        getPageManager().scaleToViewport(child);
    }
}
