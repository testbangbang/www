package com.onyx.reader.host.layout;

import android.graphics.RectF;
import android.os.DropBoxManager;
import com.onyx.reader.api.ReaderBitmap;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderException;
import com.onyx.reader.host.math.EntryManager;
import com.onyx.reader.host.navigation.NavigationManager;


/**
 * Created by zhuzeng on 10/7/15.
 * forward to navigation manager. in hard page mode, the page is defined by document, instead of rendering.
 * In hard page mode, the navigation manager can still support single hard page and continuous page mode.
 *
 * Normal single hard page provider, it could be scaled.
 *
 */
public class LayoutSingleHardPageProvider implements LayoutProvider {
    private ReaderLayoutManager layoutManager;

    public LayoutSingleHardPageProvider(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public void activate(final ReaderLayoutManager lm) throws ReaderException {
        layoutManager = lm;
    }

    public boolean setNavigationMode(final NavigationManager args) throws ReaderException {
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
        LayoutProviderUtils.addNewSingleEntry(layoutManager, layoutManager.getPositionHolder().getCurrentPosition());
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
        layoutManager.getEntryManager().setScaleWithDelta(delta);
        return true;
    }

    public boolean changeScaleByRect(final ReaderDocumentPosition position, final RectF rect) throws ReaderException  {
        return false;
    }

    public boolean gotoPosition(final ReaderDocumentPosition location) throws ReaderException {
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
