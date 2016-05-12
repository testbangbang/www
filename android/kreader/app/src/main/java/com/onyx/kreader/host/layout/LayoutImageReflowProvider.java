package com.onyx.kreader.host.layout;

import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
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

    public boolean drawVisiblePages(final Reader reader, ReaderBitmapImpl bitmap, final ReaderViewInfo readerViewInfo, boolean precache) throws ReaderException {
        Bitmap bmp = reader.getImageReflowManager().getCurrentBitmap(getCurrentPageName());
        if (bmp == null) {
            reflowPage(reader, bitmap, readerViewInfo, precache);
            if (precache) {
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

    private void reflowPage(final Reader reader, final ReaderBitmapImpl bitmap, final ReaderViewInfo readerViewInfo, boolean precache) {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), bitmap, readerViewInfo);
        reader.getImageReflowManager().reflowBitmap(bitmap.getBitmap(),
                reader.getViewOptions().getViewWidth(),
                reader.getViewOptions().getViewHeight(),
                getCurrentPageName(),
                precache);
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


}
