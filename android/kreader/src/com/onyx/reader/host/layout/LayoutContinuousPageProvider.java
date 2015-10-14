package com.onyx.reader.host.layout;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderBitmap;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderException;

/**
 * Created by zhuzeng on 10/7/15.
 */
public class LayoutContinuousPageProvider implements LayoutProvider {

    private ReaderLayoutManager layoutManager;

    public LayoutContinuousPageProvider(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public void activate(final ReaderLayoutManager layoutManager) throws ReaderException {}

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


    public boolean drawVisiblePages(ReaderBitmap bitmap) throws ReaderException {
        return false;
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
        return false;
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
        return null;
    }

    public float getActualScale() throws ReaderException {
        return 0.0f;
    }

    public RectF getHostRect() throws ReaderException {
        return null;
    }

    public RectF getViewportRect() throws ReaderException {
        return null;
    }

}
