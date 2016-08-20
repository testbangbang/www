package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.cache.ReaderBitmapImpl;
import com.onyx.kreader.common.ReaderDrawContext;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.ObjectHolder;

/**
 * Created by zhuzeng on 10/7/15.
 * For reflow stream document.
 */
public class LayoutTextReflowProvider extends LayoutProvider {


    public LayoutTextReflowProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public String getProviderName() {
        return PageConstants.IMAGE_REFLOW_PAGE;
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

    public boolean drawVisiblePages(final Reader reader, final ReaderDrawContext drawContext, final ObjectHolder<ReaderBitmapImpl> bitmap, final ReaderViewInfo readerViewInfo) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), drawContext, bitmap, readerViewInfo);
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

    public boolean setStyle(final ReaderStyle style) throws ReaderException {
        return false;
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
        return new RectF(0, 0,
                getLayoutManager().getReaderViewOptions().getViewWidth(),
                getLayoutManager().getReaderViewOptions().getViewHeight());
    }

    public void scaleToPage() throws ReaderException {

    }

    public void scaleToWidth() throws ReaderException {

    }

    public void scaleByRect(final RectF child) throws ReaderException {

    }


}
