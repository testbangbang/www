package com.onyx.reader.plugins.adobe;

import android.content.Context;
import android.graphics.RectF;
import com.onyx.reader.api.*;
import com.onyx.reader.host.impl.ReaderDocumentOptionsImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class AdobeReaderPlugin implements ReaderPlugin,
        ReaderDocument,
        ReaderView,
        ReaderRenderer,
        ReaderNavigator,
        ReaderSearchManager,
        ReaderScalingManager,
        ReaderTextStyleManager,
        ReaderDrmManager,
        ReaderPageLayoutManager
{

    private AdobePluginImpl impl;

    public AdobeReaderPlugin(final Context context) {
        ReaderDeviceInfo.init(context);
    }

    public AdobePluginImpl getPluginImpl() {
        if (impl == null) {
            impl = new AdobePluginImpl();
        }
        return impl;
    }

    public String displayName() {
        return AdobeReaderPlugin.class.getSimpleName();
    }

    public List<String> supportedFileList() {
        String [] array = {".epub", ".pdf"};
        return Arrays.asList(array);
    }

    public ReaderDocument open(final String path, final ReaderDocumentOptions documentOptions, final ReaderPluginOptions pluginOptions) throws ReaderException {
        String docPassword = "";
        String archivePassword = "";
        if (documentOptions != null) {
            docPassword = documentOptions.getDocumentPassword();
            archivePassword = documentOptions.getDocumentPassword();
        }
        long ret = getPluginImpl().openFile(path, docPassword, archivePassword);
        if (ret == 0) {
            return this;
        }
        return null;
    }

    public boolean supportDrm() {
        return true;
    }

    public ReaderDrmManager createDrmManager() {
        return this;
    }

    public void abortCurrentJob() {
        getPluginImpl().setAbortFlagNative(true);
    }

    public void clearAbortFlag() {
        getPluginImpl().setAbortFlagNative(false);
    }

    public boolean readMetadata(final ReaderDocumentMetadata metadata) {
        return false;
    }

    public boolean readCover(final ReaderBitmap bitmap) {
        return false;
    }

    public RectF getPageOriginalSize(final ReaderDocumentPosition position) {
        float size [] = {0, 0};
        getPluginImpl().pageSizeNative(position.getPageNumber(), size);
        return new RectF(0, 0, size[0], size[1]);
    }

    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc) {
        return false;
    }

    public ReaderView createView(final ReaderViewOptions viewOptions) {
        return this;
    }

    public void close() {
        getPluginImpl().closeFile();
    }

    public ReaderViewOptions getViewOptions() {
        return null;
    }

    /**
     * Retrieve renderer.
     * @return the renderer.
     */
    public ReaderRenderer getRenderer() {
        return this;
    }

    /**
     * Retrieve the navigator.
     * @return
     */
    public ReaderNavigator getNavigator() {
        return this;
    }

    /**
     * Retrieve text style interface.
     */
    public ReaderTextStyleManager getTextStyleManager() {
        return this;
    }

    /**
     * Retrieve scaling manager interface.
     * @return
     */
    public ReaderScalingManager getScalingManager() {
        return this;
    }

    /**
     * Retrieve ReaderPageLayout manager interface.
     * @return
     */
    public ReaderPageLayoutManager getPageLayoutManager() {
        return this;
    }

    /**
     * Retrieve reader hit test.
     */
    public ReaderHitTestManager getReaderHitTestManager() {
        return null;
    }

    /**
     * Retrieve current visible links.
     * @return
     */
    public List<ReaderLink> getVisibleLinks() {
        return null;
    }

    /**
     * Retrieve search interface.
     * @return
     */
    public ReaderSearchManager getSearchManager() {
        return this;
    }

    public boolean draw(final ReaderBitmap bitmap) {
        return getPluginImpl().drawVisiblePages(bitmap.getBitmap(), 0, 0, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight(), false);
    }

    public boolean draw(final ReaderBitmap bitmap, int xInBitmap, int yInBitmap, int widthInBitmap, int heightInBitmp) {
        return getPluginImpl().drawVisiblePages(bitmap.getBitmap(), xInBitmap, yInBitmap, widthInBitmap, heightInBitmp,  true);
    }

    /**
     * Retrieve the default init position.
     * @return
     */
    public ReaderDocumentPosition getInitPosition() {
        return null;
    }


    public ReaderDocumentPosition getCurrentPosition() {
        List<ReaderPageInfo> pageInfoList = new ArrayList<ReaderPageInfo>();
        if (getPluginImpl().allVisiblePagesRectangle(pageInfoList) <= 0) {
            return null;
        }
        ReaderPageInfo pageInfo = pageInfoList.get(0);
        return new AdobeDocumentPositionImpl(pageInfo.pageNumber);
    }

    /**
     * Get position from page number
     * @param pageNumber The 0 based page number.
     * @return
     */
    public ReaderDocumentPosition getPositionByPageNumber(int pageNumber) {
        return new AdobeDocumentPositionImpl(pageNumber);
    }


    /**
     * Return total page number.
     * @return 1 based total page number.
     */
    public int getTotalPage() {
        return getPluginImpl().countPagesInternal();
    }

    /**
     * Navigate to next screen.
     */
    public boolean nextScreen() {
        return false;
    }

    /**
     * Navigate to prev screen.
     */
    public boolean prevScreen() {
        return false;
    }

    /**
     * Navigate to next page.
     * @return
     */
    public boolean nextPage() {
        return false;
    }

    /**
     * Navigate to prev page.
     * @return
     */
    public boolean prevPage() {
        return false;
    }

    /**
     * Navigate to first page.
     * @return
     */
    public boolean firstPage() {
        return false;
    }

    /**
     * Navigate to last page.
     * @return
     */
    public boolean lastPage() {
        return false;
    }

    /**
     * Navigate to specified position.
     * @return
     */
    public boolean gotoPosition(final ReaderDocumentPosition position) {
        return getPluginImpl().gotoLocationInternal(position.getPageNumber(), position.save());
    }

    public boolean searchPrevious(final ReaderSearchOptions options) {
        return false;
    }

    public boolean searchNext(final ReaderSearchOptions options) {
        return false;
    }

    public List<ReaderTextSelection> searchResults() {
        return null;
    }

    /**
     * Scale to page.
     */
    public void setScaleToPage() {}

    /**
     * Check if scale to page.
     * @return
     */
    public boolean isScaleToPage() {
        return false;
    }

    public void setScaleToWidth() {

    }

    public boolean isScaleToWidth() {
        return false;
    }

    public void setScaleToHeight() {}

    public boolean isScaleToHeight() {
        return false;
    }

    public boolean isCropPage() {
        return false;
    }

    public void setCropPage() {}

    public boolean isCropWidth() {
        return false;
    }

    public void setCropWidth() {}

    public double getActualScale() {
        return 0;
    }

    public void setActualScale(double scale) {}

    /**
     * Set viewport. The behavior is different on different page layout.
     * @param viewport
     */
    public boolean setViewport(final RectF viewport) {
        return false;
    }

    /**
     * Retrieve current viewport.
     * @return the current viewport.
     */
    public RectF getViewport() {
        return null;
    }


    /**
     * Convinent method to set scale and viewport directly.
     * @param actualScale the actual scale
     * @param x the viewport x position
     * @param y the viewport y position
     * @return
     */
    public boolean changeScale(float actualScale, float x, float y) {
        return getPluginImpl().setNavigationMatrix(actualScale, x, y);
    }

    /**
     * Return the page display rect on view coordinates.
     * @param position the page position.
     * @return
     */
    public RectF getPageDisplayRect(final ReaderDocumentPosition position) {
        return null;
    }

    public boolean acceptDRMFile(final String path) {
        return false;
    }

    public boolean registerDRMCallback(final ReaderDRMCallback callback) {
        return false;
    }

    public boolean activateDeviceDRM(String user, String password) {
        return false;
    }

    public boolean deactivateDeviceDRM() {
        return false;
    }

    public String getDeviceDRMAccount() {
        return "";
    }
    public boolean fulfillDRMFile(String path) {
        return false;
    }

    public boolean supportSinglePageLayout() {
        return true;
    }

    public void setSinglePageLayout() {

    }

    public boolean isSinglePageLayout() {
        return false;
    }

    public boolean supportContinuousPageLayout() {
        return true;
    }

    public void setContinuousPageLayout() {
    }

    public boolean isContinuousPageLayout() {
        return false;
    }

    public boolean supportReflowLayout() {
        return true;
    }
    public void setReflowLayout() {

    }

    public boolean isReflowLayout() {
        return false;
    }

}
