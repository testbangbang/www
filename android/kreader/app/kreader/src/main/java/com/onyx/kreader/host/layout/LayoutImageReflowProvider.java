package com.onyx.kreader.host.layout;

import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.cache.ReaderBitmapImpl;
import com.onyx.kreader.common.ReaderDrawContext;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.reflow.ImageReflowManager;

/**
 * Created by zhuzeng on 10/7/15.
 * For reflow stream document.
 */
public class LayoutImageReflowProvider extends LayoutProvider {

    private boolean reverseOrder;

    public LayoutImageReflowProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public String getProviderName() {
        return PageConstants.IMAGE_REFLOW_PAGE;
    }

    public void activate() {
        getPageManager().setPageRepeat(0);
        getPageManager().scaleToPage(getCurrentPageName());
    }

    @Override
    public boolean canHitTest() {
        return false;
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    public boolean prevScreen() throws ReaderException {
        if (atFirstSubPage()) {
            reverseOrder = true;
            return prevPage();
        }
        previousSubPage();
        return true;
    }

    public boolean nextScreen() throws ReaderException {
        if (atLastSubPage()) {
            return nextPage();
        }
        nextSubPage();
        return true;
    }

    public boolean prevPage() throws ReaderException {
        if (gotoPosition(LayoutProviderUtils.prevPage(getLayoutManager()))) {
            moveToLastSubPage();
            return true;
        }
        return false;
    }

    public boolean nextPage() throws ReaderException {
        if (gotoPosition(LayoutProviderUtils.nextPage(getLayoutManager()))) {
            moveToFirstSubPage();
            return true;
        }
        return false;
    }

    public boolean firstPage() throws ReaderException {
        return gotoPosition(LayoutProviderUtils.firstPage(getLayoutManager()));
    }

    public boolean lastPage() throws ReaderException {
        return gotoPosition(LayoutProviderUtils.lastPage(getLayoutManager()));
    }

    public boolean drawVisiblePages(final Reader reader, final ReaderDrawContext drawContext, final ReaderViewInfo readerViewInfo) throws ReaderException {
        drawContext.renderingBitmap = new ReaderBitmapImpl();

        if (drawContext.asyncDraw) {
            if (getCurrentSubPageIndex() == 1) {
                // pre-render request of next sub page with index 1 means
                // we actually want to pre-render next page of document
                reflowNextPageInBackground(reader, drawContext, readerViewInfo);
            }
            return false;
        }

        if (!isCurrentSubPageReady()) {
            reflowFirstVisiblePageAsync(reader, drawContext, readerViewInfo, true);
        }

        if (reverseOrder) {
            moveToLastSubPage();
            reverseOrder = false;
        }

        String key = getCurrentSubPageKey();
        Bitmap bmp = getCurrentSubPageBitmap();
        if (bmp == null) {
            return false;
        }
        drawContext.renderingBitmap.attachWith(key, bmp);
        LayoutProviderUtils.updateReaderViewInfo(readerViewInfo, getLayoutManager());
        return true;
    }

    private void reflowFirstVisiblePageAsync(final Reader reader,
                                             final ReaderDrawContext drawContext,
                                             final ReaderViewInfo readerViewInfo,
                                             final boolean abortPendingTasks) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), drawContext, readerViewInfo);
        reader.getImageReflowManager().reflowBitmapAsync(drawContext.renderingBitmap.getBitmap(),
                getCurrentPageName(), abortPendingTasks);
    }

    private void reflowNextPageInBackground(final Reader reader,
                                            final ReaderDrawContext drawContext,
                                            final ReaderViewInfo readerViewInfo) throws ReaderException {
        if (gotoPosition(LayoutProviderUtils.nextPage(getLayoutManager()))) {
            if (!isCurrentSubPageReady()) {
                ReaderDrawContext reflowContext = ReaderDrawContext.copy(drawContext);
                reflowContext.renderingBitmap = new ReaderBitmapImpl();
                reflowFirstVisiblePageAsync(reader, reflowContext, readerViewInfo, false);
            }
            gotoPosition(LayoutProviderUtils.prevPage(getLayoutManager()));
        }
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
        if (StringUtils.isNullOrEmpty(position)) {
            return false;
        }
        LayoutProviderUtils.addSinglePage(getLayoutManager(), position);
        return getPageManager().gotoPage(position);
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

    public PositionSnapshot saveSnapshot() throws ReaderException {
        if (getPageManager().getFirstVisiblePage() == null) {
            return null;
        }
        return PositionSnapshot.snapshot(getProviderName(),
                getPageManager().getFirstVisiblePage(),
                getPageManager().getViewportRect(),
                getPageManager().getSpecialScale(),
                getCurrentSubPageIndex());
    }

    public boolean restoreBySnapshot(final PositionSnapshot snapshot) throws ReaderException {
        super.restoreBySnapshot(snapshot);
        moveToSubSPage(snapshot.subScreenIndex);
        return true;
    }

    private ImageReflowManager getReflowManager() {
        return getLayoutManager().getImageReflowManager();
    }

    private String getCurrentSubPageKey() {
        return getLayoutManager().getImageReflowManager().getSubPageKey(getCurrentPageName(), getCurrentSubPageIndex());
    }

    private Bitmap getCurrentSubPageBitmap() {
        return getLayoutManager().getImageReflowManager().getSubPageBitmap(getCurrentPageName(), getCurrentSubPageIndex());
    }

    private boolean isCurrentSubPageReady() {
        return getLayoutManager().getImageReflowManager().isSubPageReady(getCurrentPageName(), getCurrentSubPageIndex());
    }

    private int getCurrentSubPageIndex() {
        return getReflowManager().getCurrentSubPageIndex(getCurrentPageName());
    }

    private boolean atFirstSubPage() {
        return getReflowManager().atFirstSubPage(getCurrentPageName());
    }

    private boolean atLastSubPage() {
        return getReflowManager().atLastSubPage(getCurrentPageName());
    }

    private void moveToFirstSubPage() {
        getReflowManager().moveToFirstSubPage(getCurrentPageName());
    }

    private void moveToLastSubPage() {
        getReflowManager().moveToLastSubPage(getCurrentPageName());
    }

    private void previousSubPage() {
        getReflowManager().previousSubPage(getCurrentPageName());
    }

    private void nextSubPage() {
        getReflowManager().nextSubPage(getCurrentPageName());
    }

    private void moveToSubSPage(final int index) {
        getReflowManager().moveToSubSPage(getCurrentPageName(), index);
    }

}
