package com.onyx.kreader.host.layout;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.ReaderDrawContext;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.reflow.ImageReflowManager;
import com.onyx.kreader.utils.StringUtils;

/**
 * Created by zhuzeng on 10/7/15.
 * For reflow stream document.
 */
public class LayoutImageReflowProvider extends LayoutProvider {
    private static final String TAG = LayoutImageReflowProvider.class.getSimpleName();

    private boolean reverseOrder;

    public LayoutImageReflowProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public String getProviderName() {
        return ReaderConstants.IMAGE_REFLOW_PAGE;
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
        ImageReflowManager reflowManager = getLayoutManager().getImageReflowManager();
        if (reflowManager.atBegin(getCurrentPageName())) {
            return prevPage();
        }
        return reflowManager.prev(getCurrentPageName());
    }

    public boolean nextScreen() throws ReaderException {
        ImageReflowManager reflowManager = getLayoutManager().getImageReflowManager();
        if (reflowManager.atEnd(getCurrentPageName())) {
            return nextPage();
        }
        return reflowManager.next(getCurrentPageName());
    }

    public boolean prevPage() throws ReaderException {
        if (gotoPosition(LayoutProviderUtils.prevPage(getLayoutManager()))) {
            getLayoutManager().getImageReflowManager().moveToEnd(getCurrentPageName());
            return true;
        }
        return false;
    }

    public boolean nextPage() throws ReaderException {
        if (gotoPosition(LayoutProviderUtils.nextPage(getLayoutManager()))) {
            getLayoutManager().getImageReflowManager().moveToBegin(getCurrentPageName());
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

    public boolean drawVisiblePages(final Reader reader, final ReaderDrawContext drawContext, final ReaderBitmapImpl bitmap, final ReaderViewInfo readerViewInfo) throws ReaderException {
        Bitmap bmp = reader.getImageReflowManager().getCurrentBitmap(getCurrentPageName());
        if (bmp != null) {
            LayoutProviderUtils.updateReaderViewInfo(readerViewInfo, getLayoutManager());
        } else {
            reflowFirstVisiblePage(reader, drawContext, bitmap, readerViewInfo, drawContext.asyncDraw);
            if (drawContext.asyncDraw) {
                return false;
            }
            if (reverseOrder) {
                reader.getImageReflowManager().moveToEnd(getCurrentPageName());
                reverseOrder = false;
            }
            bmp = reader.getImageReflowManager().getCurrentBitmap(getCurrentPageName());
        }
        bitmap.copyFrom(bmp);
        return true;
    }

    private void reflowFirstVisiblePage(final Reader reader,
                                        final ReaderDrawContext drawContext,
                                        final ReaderBitmapImpl bitmap,
                                        final ReaderViewInfo readerViewInfo,
                                        boolean async) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), drawContext, bitmap, readerViewInfo);
        reader.getImageReflowManager().reflowBitmap(bitmap.getBitmap(),
                reader.getViewOptions().getViewWidth(),
                reader.getViewOptions().getViewHeight(),
                getCurrentPageName(),
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
        ImageReflowManager reflowManager = getLayoutManager().getImageReflowManager();
        return PositionSnapshot.snapshot(getProviderName(),
                getPageManager().getFirstVisiblePage(),
                getPageManager().getViewportRect(),
                getPageManager().getSpecialScale(),
                reflowManager.getCurrentScreenIndex(getCurrentPageName()));
    }

    public boolean restoreBySnapshot(final PositionSnapshot snapshot) throws ReaderException {
        super.restoreBySnapshot(snapshot);
        ImageReflowManager reflowManager = getLayoutManager().getImageReflowManager();
        reflowManager.moveToScreen(getCurrentPageName(), snapshot.subScreenIndex);
        return true;
    }


}
