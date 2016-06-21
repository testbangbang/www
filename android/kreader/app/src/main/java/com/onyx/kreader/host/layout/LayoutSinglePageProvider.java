package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.ReaderDrawContext;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.host.wrapper.Reader;


/**
 * Created by zhuzeng on 10/7/15.
 * forward to navigation manager. in hard page mode, the page is defined by document, instead of rendering.
 * In hard page mode, the navigation manager can still support single hard page and continuous page mode.
 *
 * Normal single hard page provider, it could be scaled.
 *
 */
public class LayoutSinglePageProvider extends LayoutProvider {

    public LayoutSinglePageProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public String getProviderName() {
        return PageConstants.SINGLE_PAGE;
    }

    public void activate()  {
        getPageManager().setPageRepeat(0);
        getPageManager().scaleToPage(null);
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    public boolean prevScreen() throws ReaderException {
        if (viewportAsPage()) {
            return prevPage();
        }

        if (!getPageManager().prevViewport()) {
            if (!prevPage()) {
                return false;
            }
            getPageManager().moveViewportToEndOfPage();
            return true;
        }
        getPageManager().collectVisiblePages();
        return true;
    }

    public boolean nextScreen() throws ReaderException {
        if (viewportAsPage()) {
            return nextPage();
        }

        if (!getPageManager().nextViewport()) {
            return nextPage();
        }
        getPageManager().collectVisiblePages();
        return true;
    }

    public boolean prevPage() throws ReaderException {
        return gotoPosition(LayoutProviderUtils.prevPage(getLayoutManager()));
    }

    public boolean nextPage() throws ReaderException {
        return gotoPosition(LayoutProviderUtils.nextPage(getLayoutManager()));
    }

    public boolean firstPage() throws ReaderException {
        return gotoPosition(LayoutProviderUtils.firstPage(getLayoutManager()));
    }

    public boolean lastPage() throws ReaderException {
        return gotoPosition(LayoutProviderUtils.lastPage(getLayoutManager()));
    }

    public boolean drawVisiblePages(final Reader reader, final ReaderDrawContext drawContext, final ReaderBitmapImpl bitmap, final ReaderViewInfo readerViewInfo) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), drawContext, bitmap, readerViewInfo);
        return true;
    }

    public boolean setScale(final String pageName, float scale, float left, float top) throws ReaderException {
        LayoutProviderUtils.addSinglePage(getLayoutManager(), pageName);
        getPageManager().setScale(pageName, scale);
        getPageManager().panViewportPosition(pageName, left, top);
        getPageManager().collectVisiblePages();
        return true;
    }

    public void scaleToPage(final String pageName) throws ReaderException {
        LayoutProviderUtils.addSinglePage(getLayoutManager(), pageName);
        getPageManager().scaleToPage(pageName);
    }

    public void scaleToPageContent(final String pageName) throws ReaderException {
        LayoutProviderUtils.addSinglePage(getLayoutManager(), pageName);
        getPageManager().scaleToPageContent(pageName);
    }

    public void scaleToWidth(final String pageName) throws ReaderException {
        LayoutProviderUtils.addSinglePage(getLayoutManager(), pageName);
        getPageManager().scaleToWidth(pageName);
    }

    public boolean changeScaleWithDelta(final String pageName, float delta) throws ReaderException {
        LayoutProviderUtils.addSinglePage(getLayoutManager(), pageName);
        getPageManager().scaleWithDelta(pageName, delta);
        return true;
    }

    public boolean gotoPosition(final String location) throws ReaderException {
        if (StringUtils.isNullOrEmpty(location)) {
            return false;
        }

        final RectF viewportBeforeChange = new RectF(getPageManager().getViewportRect());
        LayoutProviderUtils.addSinglePage(getLayoutManager(), location);
        if (!getPageManager().gotoPage(location)) {
            return false;
        }

        onPageChanged(viewportBeforeChange);
        return true;
    }

    private void onPageChanged(final RectF viewportBeforeChange) {
        if (PageConstants.isSpecialScale(getLayoutManager().getSpecialScale())) {
            return;
        }
        getPageManager().setAbsoluteViewportPosition(viewportBeforeChange.left,
                getPageManager().getViewportRect().top);
    }

    public boolean pan(int dx, int dy) throws ReaderException {
        LayoutProviderUtils.pan(getLayoutManager(), dx, dy);
        return true;
    }

    public boolean supportPreRender() throws ReaderException {
        return true;
    }

    public boolean supportSubScreenNavigation() {
        return false;
    }

    public boolean setStyle(final ReaderStyle style) throws ReaderException {
        return false;
    }

    public RectF getPageRectOnViewport(final String position) throws ReaderException {
        final PageInfo pageInfo = getPageManager().getPageInfo(position);
        return pageInfo.getDisplayRect();
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
        LayoutProviderUtils.addSinglePage(getLayoutManager(), pageName);
        getPageManager().scaleToViewport(pageName, child);
    }

    // return true when provider wants to regard viewport as page, ignore the other content.
    // When it's true, provider turn to next page or prev page directly instead of moving viewport.
    private boolean viewportAsPage() {
        return getPageManager().isScaleToPageContent() || getPageManager().isScaleToPage();
    }
}
