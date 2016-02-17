package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.navigation.NavigationArgs;

/**
 * Created by zhuzeng on 10/7/15.
 * For reflow stream document.
 */
public class LayoutReflowProvider  extends LayoutProvider {


    public LayoutReflowProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public void activate() {
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

    public boolean drawVisiblePages(ReaderBitmapImpl bitmap) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(getLayoutManager(), bitmap);
        return true;
    }

    public boolean setScale(float scale, float left, float top) throws ReaderException {
        return false;
    }

    public boolean changeScaleWithDelta(float delta) throws ReaderException {
        return false;
    }

    public boolean changeScaleByRect(final String position, final RectF rect) throws ReaderException  {
        return false;
    }

    public boolean gotoPosition(final String position) throws ReaderException {
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

    public RectF getPageRectOnViewport(final String position) throws ReaderException {
        return null;
    }

    public float getActualScale() throws ReaderException {
        return 0.0f;
    }

    public RectF getPageBoundingRect() throws ReaderException {
        return getViewportRect();
    }

    public RectF getViewportRect() throws ReaderException {
        return new RectF(0, 0, getLayoutManager().getReader().getViewOptions().getViewWidth(),
                getLayoutManager().getReader().getViewOptions().getViewHeight());
    }

    public void scaleToPage() throws ReaderException {

    }

    public void scaleToWidth() throws ReaderException {

    }

    public void scaleByRect(final RectF child) throws ReaderException {

    }


}
