package com.onyx.android.sdk.reader.host.layout;

import android.graphics.RectF;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/22/15.
 */
public class LayoutContinuousProvider extends LayoutProvider {


    public LayoutContinuousProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public String getProviderName() {
        return PageConstants.CONTINUOUS_PAGE;
    }

    public void activate() throws ReaderException {
        LayoutProviderUtils.addAllPage(getLayoutManager());
        getPageManager().setPageRepeat(100);
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    @Override
    public boolean canPrevScreen() throws ReaderException {
        return getPageManager().canPrevViewport();
    }

    public boolean prevScreen() throws ReaderException {
        return getPageManager().prevViewport();
    }

    @Override
    public boolean canNextScreen() throws ReaderException {
        return getPageManager().canNextViewport();
    }

    public boolean nextScreen() throws ReaderException {
        return getPageManager().nextViewport();
    }

    public boolean prevPage() throws ReaderException {
        final String newPosition = LayoutProviderUtils.prevPage(getLayoutManager());
        return gotoPosition(newPosition);
    }

    public boolean nextPage() throws ReaderException {
        final String newPosition = LayoutProviderUtils.nextPage(getLayoutManager());
        return gotoPosition(newPosition);
    }

    public boolean firstPage() throws ReaderException {
        final String newPosition = LayoutProviderUtils.firstPage(getLayoutManager());
        return gotoPosition(newPosition);
    }

    public boolean lastPage() throws ReaderException {
        final String newPosition = LayoutProviderUtils.lastPage(getLayoutManager());
        return gotoPosition(newPosition);
    }

    public boolean drawVisiblePages(final Reader reader, final ReaderDrawContext drawContext, final ReaderViewInfo readerViewInfo) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), drawContext, readerViewInfo);
        return true;
    }

    public boolean setScale(final String pageName, float scale, float left, float top) throws ReaderException {
        getPageManager().setScale(pageName, scale);
        getPageManager().panViewportPosition(pageName, left, top);
        return true;
    }

    public void scaleToPage(final String pageName) throws ReaderException {
        getPageManager().scaleToPage(pageName);
    }

    public void scaleToWidth(final String pageName) throws ReaderException {
        getPageManager().scaleToWidth(pageName);
    }

    public boolean changeScaleWithDelta(final String pageName, float delta) throws ReaderException {
        getPageManager().scaleWithDelta(pageName, delta);
        return true;
    }

    public boolean changeScaleByRect(final String position, final RectF rect) throws ReaderException  {
        return false;
    }

    public boolean gotoPosition(final String position) throws ReaderException {
        if (StringUtils.isNullOrEmpty(position)) {
            return false;
        }
        return getPageManager().gotoPage(position);
    }

    public boolean pan(int dx, int dy) throws ReaderException {
        LayoutProviderUtils.pan(getLayoutManager(), dx, dy);
        return true;
    }

    public boolean supportPreRender() throws ReaderException {
        return true;
    }

    @Override
    public boolean supportScale() throws ReaderException {
        return true;
    }

    public boolean supportSubScreenNavigation() {
        return false;
    }

    public boolean setStyle(final ReaderTextStyle style) throws ReaderException {
        return false;
    }

    public RectF getPageRectOnViewport(final String position) throws ReaderException {
        return null;
    }

    public float getActualScale() throws ReaderException {
        return getPageManager().getActualScale();
    }

    public RectF getPageBoundingRect() throws ReaderException {
        return getPageManager().getPagesBoundingRect();
    }

    public RectF getViewportRect() throws ReaderException {
        return getPageManager().getViewportRect();
    }

    public void scaleByRect(final String pageName, final RectF child) throws ReaderException {
        getPageManager().scaleToViewport(pageName, child);
    }
}