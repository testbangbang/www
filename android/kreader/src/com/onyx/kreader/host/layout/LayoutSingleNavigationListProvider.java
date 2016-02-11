package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderPagePosition;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.navigation.NavigationList;

/**
 * Created by zhuzeng on 10/19/15.
 * Single hard page with sub screen navigation list support.
 */
public class LayoutSingleNavigationListProvider implements LayoutProvider {
    private ReaderLayoutManager layoutManager;
    private NavigationArgs navigationArgs;

    public LayoutSingleNavigationListProvider(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public void activate(final ReaderLayoutManager lm) throws ReaderException {
        layoutManager = lm;
        LayoutProviderUtils.addEntry(layoutManager, layoutManager.getPositionHolder().getCurrentPosition());
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
        layoutManager.getPageManager().scaleByRatio(subScreen);
        return true;
    }

    public boolean prevScreen() throws ReaderException {
        if (getNavigationList().hasPrevious()) {
            RectF subScreen = getNavigationList().previous();
            layoutManager.getPageManager().scaleByRatio(subScreen);
            return true;
        }
        return false;
    }

    public boolean nextScreen() throws ReaderException {
        if (getNavigationList().hasNext()) {
            RectF subScreen = getNavigationList().next();
            layoutManager.getPageManager().scaleByRatio(subScreen);
            return true;
        }
        return false;
    }

    private void onPageChanged(boolean first) {
        LayoutProviderUtils.clear(layoutManager);
        LayoutProviderUtils.addEntry(layoutManager, layoutManager.getPositionHolder().getCurrentPosition());
        if (first) {
            LayoutProviderUtils.firstSubScreen(layoutManager, getNavigationList());
        } else {
            LayoutProviderUtils.lastSubScreen(layoutManager, getNavigationList());
        }
    }

    public boolean prevPage() throws ReaderException {
        if (layoutManager.getPositionHolder().prevPage()) {
            onPageChanged(false);
            return true;
        }
        return false;
    }

    public boolean nextPage() throws ReaderException {
        if (layoutManager.getPositionHolder().nextPage()) {
            onPageChanged(true);
            return true;
        }
        return false;
    }

    public boolean firstPage() throws ReaderException {
        if (layoutManager.getPositionHolder().firstPage()) {
            onPageChanged(true);
            return true;
        }
        return false;
    }

    public boolean lastPage() throws ReaderException {
        if (layoutManager.getPositionHolder().lastPage()) {
            onPageChanged(true);
            return true;
        }
        return false;
    }


    public boolean drawVisiblePages(ReaderBitmap bitmap) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(layoutManager, bitmap);
        return true;
    }

    public boolean setScale(float scale, float left, float top) throws ReaderException {
        layoutManager.getPageManager().setScale(scale);
        layoutManager.getPageManager().setViewport(left, top);
        return true;
    }

    public void scaleToPage() throws ReaderException {
        layoutManager.getPageManager().scaleToPage();
    }

    public void scaleToWidth() throws ReaderException {
        layoutManager.getPageManager().scaleToWidth();
    }

    public boolean changeScaleWithDelta(float delta) throws ReaderException {
        return false;
    }

    public boolean changeScaleByRect(final ReaderPagePosition position, final RectF rect) throws ReaderException  {
        return false;
    }

    public boolean gotoPosition(final ReaderPagePosition location) throws ReaderException {
        if (!layoutManager.getNavigator().gotoPosition(location)) {
            return false;
        }
        onPageChanged(true);
        return true;
    }

    public boolean pan(int dx, int dy) throws ReaderException {
        LayoutProviderUtils.pan(layoutManager, dx, dy);
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

    public RectF getPageRect(final ReaderPagePosition position) throws ReaderException {
        return null;
    }

    public float getActualScale() throws ReaderException {
        return layoutManager.getPageManager().getActualScale();
    }

    public RectF getHostRect() throws ReaderException {
        return layoutManager.getPageManager().getPagesBoundingRect();
    }

    public RectF getViewportRect() throws ReaderException {
        return layoutManager.getPageManager().getViewportRect();
    }

    public void scaleByRect(final RectF child) throws ReaderException {
        layoutManager.getPageManager().scaleToViewport(child);
    }
}
