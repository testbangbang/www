package com.onyx.kreader.plugins.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;
import com.onyx.kreader.api.*;
import com.onyx.kreader.common.Benchmark;
import com.onyx.kreader.utils.FileUtils;
import com.onyx.kreader.utils.PagePositionUtils;
import com.onyx.kreader.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ImagesReaderPlugin implements ReaderPlugin,
        ReaderDocument,
        ReaderView,
        ReaderRenderer,
        ReaderNavigator,
        ReaderSearchManager,
        ReaderTextStyleManager,
        ReaderDrmManager,
        ReaderHitTestManager,
        ReaderRendererFeatures {

    private static final String TAG = ImagesReaderPlugin.class.getSimpleName();
    private Benchmark benchmark = new Benchmark();

    private ImagesJniWrapper impl;
    private String documentPath;
    private List<String> pageList = new ArrayList<String>();
    static private Set<String> extensionFilters = new HashSet<String>();

    public ImagesReaderPlugin(final Context context, final ReaderPluginOptions pluginOptions) {
    }

    public ImagesJniWrapper getPluginImpl() {
        if (impl == null) {
            impl = new ImagesJniWrapper();
        }
        return impl;
    }

    public String displayName() {
        return ImagesReaderPlugin.class.getSimpleName();
    }

    static public Set<String> getExtensionFilters() {
        if (extensionFilters.size() <= 0) {
            extensionFilters.add("png");
            extensionFilters.add("jpg");
            extensionFilters.add("jpeg");
            extensionFilters.add("bmp");
            extensionFilters.add("tiff");
        }
        return extensionFilters;
    }

    static public boolean accept(final String path) {
        String extension = FileUtils.getFileExtension(path);
        return getExtensionFilters().contains(extension);
    }

    public ReaderDocument open(final String path, final ReaderDocumentOptions documentOptions, final ReaderPluginOptions pluginOptions) throws ReaderException {
        documentPath = path;
        final String baseDir = FileUtils.getParent(path);
        FileUtils.collectFiles(baseDir, getExtensionFilters(), pageList);
        return this;
    }

    public boolean supportDrm() {
        return false;
    }

    public ReaderDrmManager createDrmManager() {
        return null;
    }

    public boolean readMetadata(final ReaderDocumentMetadata metadata) {
        return false;
    }

    public boolean readCover(final ReaderBitmap bitmap) {
        return false;
    }

    public RectF getPageOriginSize(final String position) {
        float size [] = {0, 0};
        getPluginImpl().nativePageSize(pageList.get(Integer.parseInt(position)), size);
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
        pageList.clear();
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

    public boolean clear(final ReaderBitmap bitmap) {
        return getPluginImpl().nativeClearBitmap(bitmap.getBitmap());
    }

    public boolean draw(final String page, final float scale, final int rotation, final ReaderBitmap bitmap) {
        return getPluginImpl().nativeDrawImage(page, 0, 0, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight(), rotation, bitmap.getBitmap());
    }

    public boolean draw(final String page, final float scale, final int rotation, final ReaderBitmap bitmap, int xInBitmap, int yInBitmap, int widthInBitmap, int heightInBitmap) {
        return getPluginImpl().nativeDrawImage(page, xInBitmap, yInBitmap, widthInBitmap, heightInBitmap, rotation, bitmap.getBitmap());
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


    /**
     * Return total page number.
     * @return 1 based total page number.
     */
    public int getTotalPage() {
        return pageList.size();
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
