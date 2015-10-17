package com.onyx.reader.host.layout;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderBitmap;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderException;

import java.util.List;

/**
 * Created by zhuzeng on 10/7/15.
 * forward to sub screen navigation
 */
public class LayoutSinglePageProvider implements LayoutProvider {
    private ReaderLayoutManager layoutManager;

    public LayoutSinglePageProvider(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public void activate(final ReaderLayoutManager lm) throws ReaderException {
        layoutManager = lm;
    }

    public void setSubScreenNavigation(final float scale, final List<RectF> list) throws ReaderException {
        layoutManager.getSubScreenNavigator().setActualScale(scale);
        layoutManager.getSubScreenNavigator().addAll(list);
        RectF subScreen = layoutManager.getSubScreenNavigator().getCurrent();
        layoutManager.getEntryManager().scaleByRatio(subScreen);
    }

    public boolean prevScreen() throws ReaderException {
        return layoutManager.getSubScreenNavigator().prev();
    }

    public boolean nextScreen() throws ReaderException {
         if (layoutManager.getSubScreenNavigator().next()) {
             RectF subScreen = layoutManager.getSubScreenNavigator().getCurrent();
             layoutManager.getEntryManager().scaleByRatio(subScreen);
             return true;
         }
        return false;
    }

    public boolean prevPage() throws ReaderException {
        return false;
    }
    public boolean nextPage() throws ReaderException {
        if (layoutManager.getReader().getNavigator().nextPage()) {

        }
        return false;
    }

    public boolean firstPage() throws ReaderException {
        return false;
    }

    public boolean lastPage() throws ReaderException {
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
        return false;
    }

    public boolean changeScaleByRect(final ReaderDocumentPosition position, final RectF rect) throws ReaderException  {
        return false;
    }

    public boolean gotoPosition(final ReaderDocumentPosition location) throws ReaderException {
        if (!layoutManager.getReaderHelper().getNavigator().gotoPosition(location)) {
            return false;
        }
        LayoutProviderUtils.clear(layoutManager);
        LayoutProviderUtils.addEntry(layoutManager, location);
        LayoutProviderUtils.update(layoutManager);
        LayoutProviderUtils.moveViewportByPosition(layoutManager, location);
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
