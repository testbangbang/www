package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderPagePosition;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.host.navigation.NavigationArgs;

/**
 * Created by zhuzeng on 10/7/15.
 * For reflow stream document.
 */
public class LayoutReflowProvider  implements LayoutProvider {

    private ReaderLayoutManager layoutManager;

    public ReaderPositionHolder getPositionHolder() {
        return layoutManager.getPositionHolder();
    }

    public LayoutReflowProvider(final ReaderLayoutManager lm) {
        layoutManager = lm;
    }

    public void activate(final ReaderLayoutManager manager) throws ReaderException {
        layoutManager = manager;
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    public boolean prevScreen() throws ReaderException {
        return getPositionHolder().prevScreen();
    }

    public boolean nextScreen() throws ReaderException {
        return getPositionHolder().nextScreen();
    }

    public boolean prevPage() throws ReaderException {
        return getPositionHolder().prevPage();
    }

    public boolean nextPage() throws ReaderException {
        return getPositionHolder().nextPage();
    }

    public boolean firstPage() throws ReaderException {
        return getPositionHolder().firstPage();
    }

    public boolean lastPage() throws ReaderException {
        return getPositionHolder().lastPage();
    }

    public boolean drawVisiblePages(ReaderBitmap bitmap) throws ReaderException {
        return layoutManager.getReader().getRenderer().draw(null, -1.0f, bitmap);
    }

    public boolean setScale(float scale, float left, float top) throws ReaderException {
        return false;
    }

    public boolean changeScaleWithDelta(float delta) throws ReaderException {
        return false;
    }

    public boolean changeScaleByRect(final ReaderPagePosition position, final RectF rect) throws ReaderException  {
        return false;
    }

    public boolean gotoPosition(final ReaderPagePosition position) throws ReaderException {
        return getPositionHolder().gotoPosition(position);
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

    public RectF getPageRect(final ReaderPagePosition position) throws ReaderException {
        return null;
    }

    public float getActualScale() throws ReaderException {
        return 0.0f;
    }

    public RectF getHostRect() throws ReaderException {
        return getViewportRect();
    }

    public RectF getViewportRect() throws ReaderException {
        return new RectF(0, 0, layoutManager.getReader().getViewOptions().getViewWidth(),
                layoutManager.getReader().getViewOptions().getViewHeight());
    }

    public void scaleToPage() throws ReaderException {

    }

    public void scaleToWidth() throws ReaderException {

    }

    public void scaleByRect(final RectF child) throws ReaderException {

    }


}
