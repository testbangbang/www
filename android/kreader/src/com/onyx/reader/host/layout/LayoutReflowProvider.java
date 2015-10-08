package com.onyx.reader.host.layout;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderBitmap;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderException;

/**
 * Created by zhuzeng on 10/7/15.
 */
public class LayoutReflowProvider  implements LayoutProvider {

    private ReaderLayoutManager layoutManager;

    public void activate(final ReaderLayoutManager manager) throws ReaderException {
        layoutManager = manager;
    }

    public boolean prevScreen() throws ReaderException {
        return layoutManager.getReader().getReaderHelper().navigator.prevScreen();
    }

    public boolean nextScreen() throws ReaderException {
        return layoutManager.getReader().getReaderHelper().navigator.nextScreen();
    }

    public boolean prevPage() throws ReaderException {
        return layoutManager.getReader().getReaderHelper().navigator.prevPage();
    }
    public boolean nextPage() throws ReaderException {
        return layoutManager.getReader().getReaderHelper().navigator.nextPage();
    }

    public boolean firstPage() throws ReaderException {
        return layoutManager.getReader().getReaderHelper().navigator.firstPage();
    }

    public boolean lastPage() throws ReaderException {
        return layoutManager.getReader().getReaderHelper().navigator.lastPage();
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

    public boolean gotoLocation(final ReaderDocumentPosition location) throws ReaderException {
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
}
