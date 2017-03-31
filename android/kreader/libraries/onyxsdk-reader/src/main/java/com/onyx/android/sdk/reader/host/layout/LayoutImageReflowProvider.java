package com.onyx.android.sdk.reader.host.layout;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.reader.host.math.PageManager;
import com.onyx.android.sdk.reader.host.math.PositionSnapshot;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.cache.ReaderBitmapReferenceImpl;
import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.reflow.ImageReflowManager;

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

    @Override
    public void deactivate() {
        getImageReflowManager().releaseCache();
    }

    public void activate() {
        reverseOrder = false;

        getPageManager().setPageRepeat(PageManager.PAGE_REPEAT);
        getPageManager().scaleToPage(getCurrentPagePosition());

        getImageReflowManager().setPageRepeat(PageManager.PAGE_REPEAT);
    }

    @Override
    public boolean canHitTest() {
        return false;
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    @Override
    public boolean canPrevScreen() throws ReaderException {
        return !(atFirstSubPage() && atFirstPage());
    }

    public boolean prevScreen() throws ReaderException {
        if (atFirstSubPage()) {
            if (prevPage()) {
                reverseOrder = true;
            }
        }
        previousSubPage();
        return true;
    }

    @Override
    public boolean canNextScreen() throws ReaderException {
        return !(atLastSubPage() && atLastPage());
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
        drawContext.renderingBitmap = new ReaderBitmapReferenceImpl();

        if (drawContext.asyncDraw) {
            if (reverseOrder) {
                reverseOrder = false;
            }
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

        // always update reader view info, even we can't get reflowed bitmap
        LayoutProviderUtils.updateReaderViewInfo(reader, readerViewInfo, getLayoutManager());

        String key = getCurrentSubPageKey();
        CloseableReference<Bitmap> bmp = getCurrentSubPageBitmap();
        if (bmp == null) {
            Debug.e(getClass(), "drawVisiblePages: get bitmap failed!");
            return false;
        }
        try {
            drawContext.renderingBitmap.attachWith(key, bmp);
        } finally {
            bmp.close();
        }
        return true;
    }

    private ReaderBitmapReferenceImpl renderPageForReflow(final Reader reader) throws ReaderException {
        getPageManager().scaleToPage(getCurrentPagePosition());
        getPageManager().scaleWithDelta(getCurrentPagePosition(),
                (float)(getImageReflowManager().getSettings().zoom * getImageReflowManager().getSettings().columns));
        PageInfo pageInfo = getPageManager().getPageInfo(getCurrentPagePosition());
        ReaderBitmapReferenceImpl bitmap = reader.getBitmapCache().getFreeBitmap((int)pageInfo.getScaledWidth(),
                (int)pageInfo.getScaledHeight(), ReaderBitmapReferenceImpl.DEFAULT_CONFIG);
        bitmap.eraseColor(Color.WHITE);
        reader.getRenderer().draw(getCurrentPagePosition(), pageInfo.getActualScale(),
                pageInfo.getPageDisplayOrientation(), 1.0f,
                pageInfo.getPositionRect(), pageInfo.getPositionRect(),
                pageInfo.getPositionRect(), bitmap.getBitmap());
        getPageManager().scaleToPage(getCurrentPagePosition());
        return bitmap;
    }

    private void reflowFirstVisiblePageAsync(final Reader reader,
                                             final ReaderDrawContext drawContext,
                                             final ReaderViewInfo readerViewInfo,
                                             final boolean abortPendingTasks) throws ReaderException {
        ReaderBitmapReferenceImpl bitmap = renderPageForReflow(reader);
        getImageReflowManager().reflowBitmapAsync(bitmap, getCurrentPagePosition(), abortPendingTasks);
    }

    private void reflowNextPageInBackground(final Reader reader,
                                            final ReaderDrawContext drawContext,
                                            final ReaderViewInfo readerViewInfo) throws ReaderException {
        if (gotoPosition(LayoutProviderUtils.nextPage(getLayoutManager()))) {
            if (!isCurrentSubPageReady()) {
                ReaderDrawContext reflowContext = ReaderDrawContext.copy(drawContext);
                reflowContext.renderingBitmap = new ReaderBitmapReferenceImpl();
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

    public boolean setStyle(final ReaderTextStyle style) throws ReaderException {
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

    @Override
    public void updateViewportRect(RectF rect) throws ReaderException {
        super.updateViewportRect(rect);
        getPageManager().scaleToPage(getCurrentPagePosition());
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

    private ImageReflowManager getImageReflowManager() {
        return getLayoutManager().getImageReflowManager();
    }

    private String getCurrentSubPageKey() {
        return getImageReflowManager().getSubPageKey(getCurrentPagePosition(), getCurrentSubPageIndex());
    }

    private CloseableReference<Bitmap> getCurrentSubPageBitmap() {
        return getImageReflowManager().getSubPageBitmap(getCurrentPagePosition(), getCurrentSubPageIndex());
    }

    private boolean isCurrentSubPageReady() {
        return getImageReflowManager().isSubPageReady(getCurrentPagePosition(), getCurrentSubPageIndex());
    }

    private int getCurrentSubPageIndex() {
        return getImageReflowManager().getCurrentSubPageIndex(getCurrentPagePosition());
    }

    private boolean atFirstSubPage() {
        return getImageReflowManager().atFirstSubPage(getCurrentPagePosition());
    }

    private boolean atLastSubPage() {
        return getImageReflowManager().atLastSubPage(getCurrentPagePosition());
    }

    private void moveToFirstSubPage() {
        getImageReflowManager().moveToFirstSubPage(getCurrentPagePosition());
    }

    private void moveToLastSubPage() {
        getImageReflowManager().moveToLastSubPage(getCurrentPagePosition());
    }

    private void previousSubPage() {
        getImageReflowManager().previousSubPage(getCurrentPagePosition());
    }

    private void nextSubPage() {
        getImageReflowManager().nextSubPage(getCurrentPagePosition());
    }

    private void moveToSubSPage(final int index) {
        getImageReflowManager().moveToSubSPage(getCurrentPagePosition(), index);
    }

}
