package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.utils.StringUtils;

/**
 * Created by zhuzeng on 10/22/15.
 */
public class LayoutContinuousProvider extends LayoutProvider {


    public LayoutContinuousProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public void activate() {
        LayoutProviderUtils.addAllPage(getLayoutManager());
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    public boolean prevScreen() throws ReaderException {
        if (!getPageManager().prevViewport()) {
            return prevPage();
        }
        return true;
    }

    public boolean nextScreen() throws ReaderException {
        if (!getPageManager().nextViewport()) {
            return nextPage();
        }
        return true;
    }

    private void onPageChanged(boolean first) {

    }

    public boolean prevPage() throws ReaderException {
        final String newPosition = LayoutProviderUtils.prevPage(getLayoutManager());
        return gotoPosition(newPosition);
    }

    public boolean nextPage() throws ReaderException {
        final String newPosition = LayoutProviderUtils.nextPage(getLayoutManager());
        return gotoPosition(newPosition);
    }

    public boolean firstPage() throws ReaderException {
        final String newPosition = LayoutProviderUtils.firstPage(getLayoutManager());
        return gotoPosition(newPosition);
    }

    public boolean lastPage() throws ReaderException {
        final String newPosition = LayoutProviderUtils.lastPage(getLayoutManager());
        return gotoPosition(newPosition);
    }

    public boolean drawVisiblePages(ReaderBitmapImpl bitmap) throws ReaderException {
        getPageManager().collectVisiblePages();
        LayoutProviderUtils.drawVisiblePages(getLayoutManager(), bitmap);
        return true;
    }

    public boolean setScale(final String pageName, float scale, float left, float top) throws ReaderException {
        getPageManager().setScale(pageName, scale);
        getPageManager().setViewportPosition(pageName, left, top);
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

    public boolean changeScaleByRect(final String position, final RectF rect) throws ReaderException  {
        return false;
    }

    public boolean gotoPosition(final String location) throws ReaderException {
        if (StringUtils.isNonBlank(location)) {
            return false;
        }
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