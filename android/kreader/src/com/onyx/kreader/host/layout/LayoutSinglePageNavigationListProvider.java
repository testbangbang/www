package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.navigation.NavigationList;
import com.onyx.kreader.utils.StringUtils;

/**
 * Created by zhuzeng on 10/19/15.
 * Single hard page with sub screen navigation list support.
 */
public class LayoutSinglePageNavigationListProvider extends LayoutProvider {

    private NavigationArgs navigationArgs;

    public LayoutSinglePageNavigationListProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public void activate()  {

        //LayoutProviderUtils.addPage(layoutManager, layoutManager.getPositionHolder().getCurrentPosition());
    }

    private NavigationArgs getNavigationArgs() {
        if (navigationArgs == null) {
            navigationArgs = new NavigationArgs(null, null);
        }
        return navigationArgs;
    }

    private NavigationList getNavigationList() {
        return getNavigationArgs().getList();
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        navigationArgs = args;
        RectF subScreen = getNavigationList().first();
        getPageManager().scaleByRect(subScreen);
        return true;
    }

    public boolean prevScreen() throws ReaderException {
        if (getNavigationList().hasPrevious()) {
            RectF subScreen = getNavigationList().previous();
            getPageManager().scaleByRect(subScreen);
            return true;
        }
        return false;
    }

    public boolean nextScreen() throws ReaderException {
        if (getNavigationList().hasNext()) {
            RectF subScreen = getNavigationList().next();
            getPageManager().scaleByRect(subScreen);
            return true;
        }
        return false;
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
        getPageManager().setViewportPosition(left, top);
        return true;
    }

    public void scaleToPage() throws ReaderException {
        getPageManager().scaleToPage();
    }

    public void scaleToWidth() throws ReaderException {
        getPageManager().scaleToWidth();
    }

    public boolean changeScaleWithDelta(float delta) throws ReaderException {
        return false;
    }

    public boolean changeScaleByRect(final String position, final RectF rect) throws ReaderException  {
        return false;
    }

    public boolean gotoPosition(final String location) throws ReaderException {
        if (StringUtils.isNullOrEmpty(location)) {
            return false;
        }
        LayoutProviderUtils.addNewSinglePage(getLayoutManager(), location);
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
