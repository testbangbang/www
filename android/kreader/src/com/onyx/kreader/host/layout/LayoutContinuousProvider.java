package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderDocumentPosition;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.host.math.EntryManager;
import com.onyx.kreader.host.navigation.NavigationArgs;

/**
 * Created by zhuzeng on 10/22/15.
 */
public class LayoutContinuousProvider implements LayoutProvider {
    private ReaderLayoutManager layoutManager;

    public LayoutContinuousProvider(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public void activate(final ReaderLayoutManager lm) throws ReaderException {
        layoutManager = lm;
        LayoutProviderUtils.addAllEntry(layoutManager);
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    private EntryManager getEntryManager() {
        return layoutManager.getEntryManager();
    }

    public boolean prevScreen() throws ReaderException {
        if (!getEntryManager().prevViewport()) {
            return prevPage();
        }
        return true;
    }

    public boolean nextScreen() throws ReaderException {
        if (!getEntryManager().nextViewport()) {
            return nextPage();
        }
        return true;
    }

    private void onPageChanged(boolean first) {

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
        layoutManager.getEntryManager().setScale(scale);
        layoutManager.getEntryManager().setViewport(left, top);
        return true;
    }

    public void scaleToPage() throws ReaderException {
        layoutManager.getEntryManager().scaleToPage();
    }

    public void scaleToWidth() throws ReaderException {
        layoutManager.getEntryManager().scaleToWidth();
    }

    public boolean changeScaleWithDelta(float delta) throws ReaderException {
        layoutManager.getEntryManager().scaleWithDelta(delta);
        return true;
    }

    public boolean changeScaleByRect(final ReaderDocumentPosition position, final RectF rect) throws ReaderException  {
        return false;
    }

    public boolean gotoPosition(final ReaderDocumentPosition location) throws ReaderException {
        if (!layoutManager.getPositionHolder().gotoPosition(location)) {
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

    public RectF getPageRect(final ReaderDocumentPosition position) throws ReaderException {
        return null;
    }

    public float getActualScale() throws ReaderException {
        return layoutManager.getEntryManager().getActualScale();
    }

    public RectF getHostRect() throws ReaderException {
        return layoutManager.getEntryManager().getHostRect();
    }

    public RectF getViewportRect() throws ReaderException {
        return layoutManager.getEntryManager().getViewportRect();
    }

    public void scaleByRect(final RectF child) throws ReaderException {
        layoutManager.getEntryManager().scaleToViewport(child);
    }
}