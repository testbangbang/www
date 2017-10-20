package com.onyx.android.sdk.reader.host.layout;

import android.graphics.RectF;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

/**
 * Created by zhuzeng on 10/7/15.
 * For reflow stream document.
 */
public class LayoutTextReflowProvider extends LayoutProvider {


    public LayoutTextReflowProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public String getProviderName() {
        return PageConstants.TEXT_REFLOW_PAGE;
    }

    public void activate() {
        getPageManager().setPageRepeat(0);
        getPageManager().scaleToPage(null);
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    @Override
    public boolean canPrevScreen() throws ReaderException {
        return !getLayoutManager().getNavigator().isFirstPage();
    }

    public boolean prevScreen() throws ReaderException {
        return prevPage();
    }

    @Override
    public boolean canNextScreen() throws ReaderException {
        return !getLayoutManager().getNavigator().isLastPage();
    }

    public boolean nextScreen() throws ReaderException {
        return nextPage();
    }

    public boolean prevPage() throws ReaderException {
        LayoutProviderUtils.prevPage(getLayoutManager());
        return gotoPosition(getLayoutManager().getNavigator().getScreenStartPosition());
    }

    public boolean nextPage() throws ReaderException {
        LayoutProviderUtils.nextPage(getLayoutManager());
        return gotoPosition(getLayoutManager().getNavigator().getScreenStartPosition());
    }

    public boolean firstPage() throws ReaderException {
        LayoutProviderUtils.firstPage(getLayoutManager());
        return gotoPosition(getLayoutManager().getNavigator().getScreenStartPosition());
    }

    public boolean lastPage() throws ReaderException {
        LayoutProviderUtils.lastPage(getLayoutManager());
        return gotoPosition(getLayoutManager().getNavigator().getScreenStartPosition());
    }

    public boolean drawVisiblePages(final Reader reader, final ReaderDrawContext drawContext, final ReaderViewInfo readerViewInfo) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), drawContext, readerViewInfo, true);
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

    @Override
    public boolean gotoPage(int page) throws ReaderException {
        if (!getLayoutManager().getNavigator().gotoPage(page)) {
            return false;
        }
        return gotoPosition(getLayoutManager().getNavigator().getScreenStartPosition());
    }

    public boolean gotoPosition(final String position) throws ReaderException {
        if (StringUtils.isNullOrEmpty(position)) {
            return false;
        }
        if (!getLayoutManager().getNavigator().gotoPosition(position)) {
            return false;
        }
        String page = PagePositionUtils.fromPageNumber(getLayoutManager().getNavigator().getScreenStartPageNumber());
        String endPosition = getLayoutManager().getNavigator().getScreenEndPosition();
        LayoutProviderUtils.addSinglePage(getLayoutManager(), page, position, endPosition);
        if (!getPageManager().gotoPage(position)) {
            return false;
        }

        final RectF viewportBeforeChange = new RectF(getPageManager().getViewportRect());
        onPageChanged(viewportBeforeChange);
        return true;
    }

    public boolean pan(int dx, int dy) throws ReaderException {
        return false;
    }

    public boolean supportPreRender() throws ReaderException {
        return true;
    }

    @Override
    public boolean supportScale() throws ReaderException {
        return false;
    }

    public boolean supportSubScreenNavigation() {
        return false;
    }

    public boolean setStyle(final ReaderTextStyle style) throws ReaderException {
        getLayoutManager().getTextStyleManager().setStyle(style);
        return true;
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

    @Override
    public void updateViewportRect(RectF rect) throws ReaderException {
        super.updateViewportRect(rect);
        getPageManager().clear();
        gotoPosition(getLayoutManager().getNavigator().getScreenStartPosition());
    }

    public void scaleToPage() throws ReaderException {

    }

    public void scaleToWidth() throws ReaderException {

    }

    public void scaleByRect(final RectF child) throws ReaderException {

    }

    private void onPageChanged(final RectF viewportBeforeChange) {
        if (PageConstants.isSpecialScale(getLayoutManager().getSpecialScale())) {
            return;
        }
        getPageManager().setAbsoluteViewportPosition(viewportBeforeChange.left,
                getPageManager().getViewportRect().top);
    }

}
