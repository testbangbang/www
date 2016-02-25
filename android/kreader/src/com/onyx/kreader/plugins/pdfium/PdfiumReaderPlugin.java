package com.onyx.kreader.plugins.pdfium;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;
import com.onyx.kreader.api.*;
import com.onyx.kreader.common.Benchmark;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.utils.PagePositionUtils;
import com.onyx.kreader.utils.StringUtils;

import java.util.List;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class PdfiumReaderPlugin implements ReaderPlugin,
        ReaderDocument,
        ReaderView,
        ReaderRenderer,
        ReaderNavigator,
        ReaderSearchManager,
        ReaderTextStyleManager,
        ReaderDrmManager,
        ReaderHitTestManager,
        ReaderRendererFeatures {

    private static final String TAG = PdfiumReaderPlugin.class.getSimpleName();
    private Benchmark benchmark = new Benchmark();

    private PdfiumJniWrapper impl;
    private String documentPath;

    public PdfiumReaderPlugin(final Context context, final ReaderPluginOptions pluginOptions) {
    }

    public PdfiumJniWrapper getPluginImpl() {
        if (impl == null) {
            impl = new PdfiumJniWrapper();
            impl.nativeInitLibrary();
        }
        return impl;
    }

    public String displayName() {
        return PdfiumReaderPlugin.class.getSimpleName();
    }

    static public boolean accept(final String path) {
        String string = path.toLowerCase();
        if (string.endsWith(".pdf")) {
            return true;
        }
        return false;
    }


    public ReaderDocument open(final String path, final ReaderDocumentOptions documentOptions, final ReaderPluginOptions pluginOptions) throws ReaderException {
        String docPassword = "";
        String archivePassword = "";
        documentPath = path;
        if (documentOptions != null) {
            docPassword = documentOptions.getDocumentPassword();
            archivePassword = documentOptions.getDocumentPassword();
        }
        long ret = getPluginImpl().nativeOpenDocument(path, docPassword);
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

    public boolean readMetadata(final ReaderDocumentMetadata metadata) {
        return false;
    }

    public boolean readCover(final ReaderBitmap bitmap) {
        return false;
    }

    public RectF getPageOriginSize(final String position) {
        float size [] = {0, 0};
        getPluginImpl().nativePageSize(Integer.parseInt(position), size);
        return new RectF(0, 0, size[0], size[1]);
    }

    public void abortCurrentJob() {

    }

    public void clearAbortFlag() {

    }

    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc) {
        return false;
    }

    public ReaderView getView(final ReaderViewOptions viewOptions) {
        return this;
    }

    public ReaderRendererFeatures getRendererFeatures() {
        return this;
    }

    public void close() {
        getPluginImpl().nativeCloseDocument();
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

    public void setStyle(final ReaderStyle style) {}

    /**
     * Retrieve reader hit test.
     */
    public ReaderHitTestManager getReaderHitTestManager() {
        return this;
    }

    /**
     * Retrieve current visible links.
     * @return
     */
    public List<ReaderLink> getLinks(final String position) {
        return null;
    }

    /**
     * Retrieve search interface.
     * @return
     */
    public ReaderSearchManager getSearchManager() {
        return this;
    }

    public boolean draw(final String page, final float scale,  final int rotation,  final ReaderBitmap bitmap) {
        benchmark.restart();
        boolean ret = getPluginImpl().drawPage(PagePositionUtils.getPageNumber(page), 0, 0, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight(), rotation, bitmap.getBitmap());
        Log.e(TAG, "rendering takes: " + benchmark.duration());
        return ret;
    }

    public boolean draw(final String page, final float scale,  final int rotation, final ReaderBitmap bitmap, int xInBitmap, int yInBitmap, int widthInBitmap, int heightInBitmap) {
        benchmark.restart();
        boolean ret = getPluginImpl().drawPage(PagePositionUtils.getPageNumber(page), xInBitmap, yInBitmap, widthInBitmap, heightInBitmap, rotation, bitmap.getBitmap());
        Log.e(TAG, "rendering takes: " + benchmark.duration());
        return ret;
    }

    /**
     * Retrieve the default init position.
     * @return
     */
    public String getInitPosition() {
        return firstPage();
    }

    /**
     * Get position from page number
     * @param pageNumber The 0 based page number.
     * @return
     */
    public String getPositionByPageNumber(int pageNumber) {
        return PagePositionUtils.fromPageNumber(pageNumber);
    }

    public String getPositionByPageName(final String pageName) {
        return pageName;
    }

    /**
     * Return total page number.
     * @return 1 based total page number.
     */
    public int getTotalPage() {
        return getPluginImpl().nativePageCount();
    }

    /**
     * Navigate to next screen.
     */
    public String nextScreen(final String position) {
        return nextPage(position);
    }

    /**
     * Navigate to previous screen.
     */
    public String prevScreen(final String position) {
        return prevPage(position);
    }

    /**
     * Navigate to next page.
     * @return
     */
    public String nextPage(final String position) {
        int pn = PagePositionUtils.getPageNumber(position);
        if (pn + 1 < getTotalPage()) {
            return PagePositionUtils.fromPageNumber(pn + 1);
        }
        return null;
    }

    /**
     * Navigate to previous page.
     * @return
     */
    public String prevPage(final String position) {
        int pn = PagePositionUtils.getPageNumber(position);
        if (pn > 0) {
            return PagePositionUtils.fromPageNumber(pn - 1);
        }
        return null;

    }

    /**
     * Navigate to first page.
     * @return
     */
    public String firstPage() {
        return PagePositionUtils.fromPageNumber(0);
    }

    /**
     * Navigate to last page.
     * @return
     */
    public String lastPage() {
        return PagePositionUtils.fromPageNumber(getTotalPage() - 1);
    }

    /**
     * Navigate to specified position.
     * @return
     */
    public boolean gotoPosition(final String position) {
        return false;
    }

    public boolean searchPrevious(final ReaderSearchOptions options) {
        return false;
    }

    public boolean searchNext(final ReaderSearchOptions options) {
        return false;
    }

    public List<ReaderSelection> searchResults() {
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

    public ReaderSelection selectWord(final ReaderHitTestArgs hitTest, final ReaderTextSplitter splitter) {

        return null;
    }

    public String position(final ReaderHitTestArgs hitTest) {
        return null;
    }

    public ReaderSelection select(final ReaderHitTestArgs start, final ReaderHitTestArgs end) {
        PdfiumSelection selection = new PdfiumSelection();
        getPluginImpl().nativeHitTest(PagePositionUtils.getPageNumber(start.pageName),
                (int) start.pageDisplayRect.left,
                (int) start.pageDisplayRect.top,
                (int) start.pageDisplayRect.width(),
                (int) start.pageDisplayRect.height(),
                start.pageDisplayOrientation,
                (int) start.point.x,
                (int) start.point.y,
                (int) end.point.x,
                (int) end.point.y,
                selection);
        return null;
    }

    public boolean supportScale() {
        if (StringUtils.isNullOrEmpty(documentPath)) {
            return false;
        }
        return documentPath.toLowerCase().endsWith("pdf");
    }

    public boolean supportFontSizeAdjustment() {
        return true;
    }

    public boolean supportTypefaceAdjustment() {
        if (StringUtils.isNullOrEmpty(documentPath)) {
            return false;
        }
        return documentPath.toLowerCase().endsWith("epub");
    }


}
