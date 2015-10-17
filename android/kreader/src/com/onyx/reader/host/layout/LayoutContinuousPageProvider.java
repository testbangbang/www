package com.onyx.reader.host.layout;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderBitmap;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderException;
import com.onyx.reader.host.math.EntryInfo;

import java.util.List;

/**
 * Created by zhuzeng on 10/7/15.
 */
public class LayoutContinuousPageProvider implements LayoutProvider {

    private ReaderLayoutManager layoutManager;

    public LayoutContinuousPageProvider(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public void activate(final ReaderLayoutManager layoutManager) throws ReaderException {
        int total = layoutManager.getReaderHelper().getNavigator().getTotalPage();
        LayoutProviderUtils.clear(layoutManager);
        for(int i = 0; i < total; ++i) {
            final ReaderDocumentPosition position = layoutManager.getReaderHelper().getNavigator().getPositionByPageNumber(i);
            LayoutProviderUtils.addEntry(layoutManager, position);
        }
        LayoutProviderUtils.update(layoutManager);
    }

    public void setSubScreenNavigation(final float scale, final List<RectF> list) throws ReaderException {

    }

    public boolean prevScreen() throws ReaderException {
        return layoutManager.getEntryManager().prevViewport();
    }

    public boolean nextScreen() throws ReaderException {
        return layoutManager.getEntryManager().nextViewport();
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

    public boolean drawVisiblePages(ReaderBitmap bitmap) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(layoutManager, bitmap);
        return true;
    }

    public boolean setScale(float scale, float left, float top) throws ReaderException {
        return false;
    }

    public boolean changeScaleWithDelta(float delta) throws ReaderException {
        return false;
    }

    public boolean changeScaleByRect(final ReaderDocumentPosition position, final RectF rect) throws ReaderException  {
        return false;
    }

    public boolean gotoPosition(final ReaderDocumentPosition location) throws ReaderException {
        return layoutManager.getEntryManager().moveViewportByPosition(location.save());
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

    public void save(int delta) throws ReaderException {

    }

    public void restore() throws ReaderException {

    }

    public String renderingString() throws ReaderException {
        return null;
    }

    public RectF getPageRect(final ReaderDocumentPosition position) throws ReaderException {
        final EntryInfo entryInfo = layoutManager.getEntryManager().getEntryInfo(position);
        if (entryInfo != null) {
            return entryInfo.getDisplayRect();
        }
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

    public void scaleToPage() throws ReaderException  {

    }

    public void scaleToWidth() throws ReaderException {

    }

    public void scaleByRect(final RectF child) throws ReaderException {
        layoutManager.getEntryManager().scaleToViewport(child);
    }


}
