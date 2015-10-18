package com.onyx.reader.host.layout;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderBitmap;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderException;

import java.util.List;

/**
 * Created by zhuzeng on 10/7/15.
 */
public class LayoutReflowProvider  implements LayoutProvider {

    private ReaderLayoutManager layoutManager;
    private ReaderDocumentPosition current;

    public LayoutReflowProvider(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public void activate(final ReaderLayoutManager manager) throws ReaderException {
        layoutManager = manager;
    }

    public void setSubScreenNavigation(final float scale, final List<RectF> list) throws ReaderException {
    }

    public boolean prevScreen() throws ReaderException {
        ReaderDocumentPosition newPosition = layoutManager.getReader().getNavigator().prevScreen(current);
        if (newPosition != null) {
            current = newPosition;
            return true;
        }
        return false;
    }

    public boolean nextScreen() throws ReaderException {
        ReaderDocumentPosition newPosition = layoutManager.getReader().getNavigator().nextScreen(current);
        if (newPosition != null) {
            current = newPosition;
            return true;
        }
        return false;
    }

    public boolean prevPage() throws ReaderException {
        ReaderDocumentPosition newPosition = layoutManager.getReader().getNavigator().prevPage(current);
        if (newPosition != null) {
            current = newPosition;
            return true;
        }
        return false;
    }

    public boolean nextPage() throws ReaderException {
        ReaderDocumentPosition newPosition = layoutManager.getReader().getNavigator().nextPage(current);
        if (newPosition != null) {
            current = newPosition;
            return true;
        }
        return false;
    }

    public boolean firstPage() throws ReaderException {
        ReaderDocumentPosition newPosition = layoutManager.getReader().getNavigator().firstPage();
        if (newPosition != null) {
            current = newPosition;
            return true;
        }
        return false;
    }

    public boolean lastPage() throws ReaderException {
        ReaderDocumentPosition newPosition = layoutManager.getReader().getNavigator().lastPage();
        if (newPosition != null) {
            current = newPosition;
            return true;
        }
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

    public void scaleToPage() throws ReaderException {

    }

    public void scaleToWidth() throws ReaderException {

    }

    public void scaleByRect(final RectF child) throws ReaderException {

    }


}
