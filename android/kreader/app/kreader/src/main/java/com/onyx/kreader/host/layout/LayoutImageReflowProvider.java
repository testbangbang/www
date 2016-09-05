package com.onyx.kreader.host.layout;

import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderBitmapList;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.cache.ReaderBitmapImpl;
import com.onyx.kreader.common.ReaderDrawContext;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.ObjectHolder;

/**
 * Created by zhuzeng on 10/7/15.
 * For reflow stream document.
 */
public class LayoutImageReflowProvider extends LayoutProvider {
    @SuppressWarnings("unused")
    private static final String TAG = LayoutImageReflowProvider.class.getSimpleName();

    private boolean reverseOrder;
    private boolean isNewPage;

    public LayoutImageReflowProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public String getProviderName() {
        return PageConstants.IMAGE_REFLOW_PAGE;
    }

    public void activate() {
        getPageManager().setPageRepeat(0);
        getLayoutManager().getImageReflowManager().loadPageMap();
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    public boolean prevScreen() throws ReaderException {
        reverseOrder = true;
        if (atFirstSubPage()) {
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
            isNewPage = true;
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

        String key = getCurrentSubPageKey();
        Bitmap bmp = getCurrentSubPageBitmap();
        if (bmp != null) {
            drawContext.renderingBitmap.attachWith(key, bmp);
            LayoutProviderUtils.updateReaderViewInfo(readerViewInfo, getLayoutManager());
            if (isNewPage) {
                reflowNextPageForCache(reader, drawContext, readerViewInfo);
                isNewPage = false;
            }
            return true;
        }

        reflowFirstVisiblePage(reader, drawContext, readerViewInfo, drawContext.asyncDraw);
        if (drawContext.asyncDraw) {
            return false;
        }

        reflowNextPageForCache(reader, drawContext, readerViewInfo);
        if (reverseOrder) {
            moveToLastSubPage();
            reverseOrder = false;
        }
        bmp = getCurrentSubPageBitmap();
        if (bmp == null) {
            return false;
        }
        drawContext.renderingBitmap.attachWith(key, bmp);
        return true;
    }

    private void reflowFirstVisiblePage(final Reader reader,
                                        final ReaderDrawContext drawContext,
                                        final ReaderViewInfo readerViewInfo,
                                        boolean async) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), drawContext, readerViewInfo);
        reader.getImageReflowManager().reflowBitmap(drawContext.renderingBitmap.getBitmap(),
                reader.getViewOptions().getViewWidth(),
                reader.getViewOptions().getViewHeight(),
                getCurrentPageName(),
                async);
    }

    private void reflowNextPageForCache(final Reader reader,
                                final ReaderDrawContext drawContext,
                                final ReaderViewInfo readerViewInfo) throws ReaderException {
        if (gotoPosition(LayoutProviderUtils.nextPage(getLayoutManager()))) {
            ReaderDrawContext reflowContext = ReaderDrawContext.copy(drawContext);
            reflowContext.renderingBitmap = new ReaderBitmapImpl();
            reflowFirstVisiblePage(reader, reflowContext, readerViewInfo, true);
            gotoPosition(LayoutProviderUtils.prevPage(getLayoutManager()));
        }
    }

    private void reflowPage(final Reader reader,
                                        final String pageName,
                                        final ReaderDrawContext drawContext,
                                        final ReaderViewInfo readerViewInfo,
                                        boolean async) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), drawContext, readerViewInfo);
        reader.getImageReflowManager().reflowBitmap(drawContext.renderingBitmap.getBitmap(),
                reader.getViewOptions().getViewWidth(),
                reader.getViewOptions().getViewHeight(),
                pageName,
                async);
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

    private ReaderBitmapList getCurrentSubPageList() {
        return getLayoutManager().getImageReflowManager().getSubPageList(getCurrentPageName());
    }

    private String getCurrentSubPageKey() {
        return getLayoutManager().getImageReflowManager().getSubPageKey(getCurrentPageName(), getCurrentSubPageIndex());
    }

    private Bitmap getCurrentSubPageBitmap() {
        return getLayoutManager().getImageReflowManager().getSubPageBitmap(getCurrentPageName(), getCurrentSubPageIndex());
    }

    private int getCurrentSubPageIndex() {
        return getCurrentSubPageList().getCurrent();
    }

    private boolean atFirstSubPage() {
        return getCurrentSubPageList().atBegin();
    }

    private boolean atLastSubPage() {
        return getCurrentSubPageList().atEnd();
    }

    private void moveToFirstSubPage() {
        getCurrentSubPageList().moveToBegin();
    }

    private void moveToLastSubPage() {
        getCurrentSubPageList().moveToEnd();
    }

    private void previousSubPage() {
        getCurrentSubPageList().prev();
    }

    private void nextSubPage() {
        getCurrentSubPageList().next();
    }

    private void moveToSubSPage(int index) {
        getCurrentSubPageList().moveToScreen(index);
    }

}
