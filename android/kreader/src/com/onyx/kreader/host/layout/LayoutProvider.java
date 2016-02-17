package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.math.PageManager;
import com.onyx.kreader.host.navigation.NavigationArgs;

/**
 * Created by zhuzeng on 10/7/15.
 */
public class LayoutProvider {

    private ReaderLayoutManager layoutManager;

    public LayoutProvider(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public void activate() {
    }

    public ReaderLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public PageManager getPageManager() {
        return getLayoutManager().getPageManager();
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }
    
    public boolean prevScreen() throws ReaderException {
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

    public boolean drawVisiblePages(ReaderBitmapImpl bitmap) throws ReaderException {
        return false;
    }

    public boolean setScale(float scale, float left, float top) throws ReaderException {
        return false;
    }

    public void scaleToPage(final String pageName) throws ReaderException {
    }

    public void scaleToWidth(final String pageName) throws ReaderException {
    }

    public void scaleByRect(final String pageName, final RectF child) throws ReaderException {
    }

    public boolean changeScaleWithDelta(final String pageName, float delta) throws ReaderException {
        return false;
    }

    public boolean changeScaleByRect(final String pageName, final RectF rect) throws ReaderException {
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

    public RectF getPageRectOnViewport(final String position) throws ReaderException {
        return null;
    }

    public boolean pan(int dx, int dy) throws ReaderException {
        return false;
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

    public void save(int delta) throws ReaderException {}
    public void restore() throws ReaderException {}

    public String renderingString() throws ReaderException {
        return null;
    }


}
