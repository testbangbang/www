package com.onyx.reader.host.layout;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderBitmap;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderException;
import com.onyx.reader.host.impl.ReaderDocumentOptionsImpl;

import java.util.List;

/**
 * Created by zhuzeng on 10/7/15.
 */
public interface LayoutProvider {

    public void activate(final ReaderLayoutManager layoutManager) throws ReaderException;

    public void setSubScreenNavigation(final float scale, final List<RectF> list) throws ReaderException;
    public boolean prevScreen() throws ReaderException;
    public boolean nextScreen() throws ReaderException;

    public boolean prevPage() throws ReaderException;
    public boolean nextPage() throws ReaderException;

    public boolean firstPage() throws ReaderException;
    public boolean lastPage() throws ReaderException;

    public boolean drawVisiblePages(ReaderBitmap bitmap) throws ReaderException;

    public boolean setScale(float scale, float left, float top) throws ReaderException;
    public void scaleToPage() throws ReaderException;
    public void scaleToWidth() throws ReaderException;
    public void scaleByRect(final RectF child) throws ReaderException;

    public float getActualScale() throws ReaderException;
    public RectF getHostRect() throws ReaderException;
    public RectF getViewportRect() throws ReaderException;

    public boolean changeScaleWithDelta(float delta) throws ReaderException;

    public boolean changeScaleByRect(final ReaderDocumentPosition position, final RectF rect) throws ReaderException ;

    public boolean gotoPosition(final ReaderDocumentPosition location) throws ReaderException;
    public RectF getPageRect(final ReaderDocumentPosition position) throws ReaderException;

    public boolean pan(int dx, int dy) throws ReaderException;

    public boolean supportPreRender() throws ReaderException;
    public boolean supportSubScreenNavigation();

    public boolean setFontSize(float fontSize) throws ReaderException;
    public boolean setTypeface(final String typeface) throws ReaderException;

    public void save(int delta) throws ReaderException;
    public void restore() throws ReaderException;

    public String renderingString() throws ReaderException;


}
