package com.onyx.android.sdk.reader.plugins.comic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.plugins.images.ImagesWrapper;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.reader.api.*;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

import java.util.List;

/**
 * Created by joy on 3/15/16.
 */
public class ComicReaderPlugin implements ReaderPlugin,
        ReaderDocument,
        ReaderView,
        ReaderRenderer,
        ReaderNavigator,
        ReaderSearchManager,
        ReaderTextStyleManager,
        ReaderDrmManager,
        ReaderHitTestManager,
        ReaderRendererFeatures {

    static public boolean accept(final String path) {
        return path.toLowerCase().endsWith(".cbz") ||
                path.toLowerCase().endsWith(".cbr");
    }

    private ComicArchiveWrapper impl;

    public ComicReaderPlugin(Context context, ReaderPluginOptions pluginOptions) {

    }

    private ComicArchiveWrapper getPluginImpl() {
        if (impl == null) {
            impl = new ComicArchiveWrapper();
        }
        return impl;
    }

    /**
     * Read the document metadata.
     *
     * @param metadata The metadata interface.
     * @return
     */
    @Override
    public boolean readMetadata(ReaderDocumentMetadata metadata) {
        return false;
    }

    /**
     * Retrieve cover image.
     *
     * @param bitmap
     */
    @Override
    public boolean readCover(Bitmap bitmap) {
        if (getPluginImpl().getPageCount() < 1) {
            return false;
        }
        ImagesWrapper.ImageInformation info = getPluginImpl().imageInfo(0);
        if (info == null) {
            return false;
        }
        float zoom = PageUtils.scaleToPage(info.getWidth(), info.getHeight(),
                bitmap.getWidth(), bitmap.getHeight());
        RectF pageRect = new RectF(0, 0, info.getWidth() * zoom, info.getHeight() * zoom);
        RectF displayRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        return getPluginImpl().drawPage(0, zoom, 0, displayRect, pageRect, pageRect, bitmap);
    }

    /**
     * Retrieve the page natural size.
     *
     * @param position
     * @return
     */
    @Override
    public RectF getPageOriginSize(String position) {
        ImagesWrapper.ImageInformation imageInformation = getPluginImpl().imageInfo(Integer.parseInt(position));
        if (imageInformation == null) {
            return null;
        }
        return new RectF(0, 0, imageInformation.getWidth(), imageInformation.getHeight());
    }

    @Override
    public boolean supportTextPage() {
        return false;
    }

    @Override
    public boolean isTextPage(String position) {
        return false;
    }

    @Override
    public String getPageText(String position) {
        return "";
    }

    @Override
    public ReaderSentence getSentence(String position, String sentenceStartPosition) {
        return null;
    }

    /**
     * Read the document table of content.
     *
     * @param toc
     * @return
     */
    @Override
    public boolean readTableOfContent(ReaderDocumentTableOfContent toc) {
        return false;
    }

    @Override
    public boolean exportNotes(String sourceDocPath, String targetDocPath, List<Annotation> annotations, List<Shape> scribbles) {
        return false;
    }

    /**
     * Get corresponding view.
     *
     * @param viewOptions The view options.
     * @return The created view. null if failed.
     */
    @Override
    public ReaderView getView(ReaderViewOptions viewOptions) {
        return this;
    }

    @Override
    public boolean saveOptions() {
        return true;
    }

    /**
     * Close the document.
     */
    @Override
    public void close() {
        getPluginImpl().close();
    }

    @Override
    public void updateDocumentOptions(ReaderDocumentOptions documentOptions, ReaderPluginOptions pluginOptions) {
        
    }

    /**
     * Check if drm manager accept the file or not.
     *
     * @param path
     * @return
     */
    @Override
    public boolean acceptDRMFile(String path) {
        return false;
    }

    @Override
    public boolean registerDRMCallback(ReaderDRMCallback callback) {
        return false;
    }

    @Override
    public boolean activateDeviceDRM(String user, String password) {
        return false;
    }

    @Override
    public boolean deactivateDeviceDRM() {
        return false;
    }

    @Override
    public String getDeviceDRMAccount() {
        return null;
    }

    @Override
    public boolean fulfillDRMFile(String path) {
        return false;
    }

    /**
     * Select word by the point. The plugin should automatically extend the selection to word boundary.
     *
     * @param hitTest  the user input point in document coordinates system.
     * @param splitter the text splitter.
     * @return the selection.
     */
    @Override
    public ReaderSelection selectWordOnScreen(ReaderHitTestArgs hitTest, ReaderTextSplitter splitter) {
        return null;
    }

    @Override
    public List<ReaderSelection> allText(final String pagePosition) {
        return null;
    }

    /**
     * Get document position for specified point.
     *
     * @param hitTest the hit test args.
     * @return
     */
    @Override
    public String position(ReaderHitTestArgs hitTest) {
        return null;
    }

    /**
     * Select text between start point and end point.
     *
     * @param start The start view point.
     * @param end
     * @param hitTestOptions
     * @return the selection.
     */
    @Override
    public ReaderSelection selectOnScreen(ReaderHitTestArgs start, ReaderHitTestArgs end, ReaderHitTestOptions hitTestOptions) {
        return null;
    }

    @Override
    public ReaderSelection selectOnScreen(String pagePosition, String startPosition, String endPosition) {
        return null;
    }

    /**
     * Retrieve the default start position.
     *
     * @return
     */
    @Override
    public String getInitPosition() {
        return firstPage();
    }

    /**
     * Get position from page number. Page position can be retrieved by both index and name.
     *
     * @param pageNumber The 0 based page number.
     * @return
     */
    @Override
    public String getPositionByPageNumber(int pageNumber) {
        return PagePositionUtils.fromPageNumber(pageNumber);
    }

    /**
     * Get position from page name.
     *
     * @param pageName The page name.
     * @return page position.
     */
    @Override
    public String getPositionByPageName(String pageName) {
        return pageName;
    }

    @Override
    public int getPageNumberByPosition(String position) {
        return -1;
    }

    /**
     * Return total page number.
     *
     * @return 1 based total page number. return -1 if not available yet.
     */
    @Override
    public int getTotalPage() {
        return getPluginImpl().getPageCount();
    }

    @Override
    public int getScreenStartPageNumber() {
        return 0;
    }

    @Override
    public int getScreenEndPageNumber() {
        return 0;
    }

    @Override
    public String getScreenStartPosition() {
        return null;
    }

    @Override
    public String getScreenEndPosition() {
        return null;
    }

    @Override
    public int comparePosition(String pos1, String pos2) {
        return 0;
    }

    @Override
    public boolean gotoPosition(String position) {
        return false;
    }

    @Override
    public boolean gotoPage(int page) {
        return false;
    }

    /**
     * Navigate to next screen.
     *
     * @param position
     */
    @Override
    public String nextScreen(String position) {
        return nextPage(position);
    }

    /**
     * Navigate to previous screen.
     *
     * @param position
     */
    @Override
    public String prevScreen(String position) {
        return prevPage(position);
    }

    /**
     * Navigate to next page.
     *
     * @param position
     * @return
     */
    @Override
    public String nextPage(String position) {
        int pn = PagePositionUtils.getPageNumber(position);
        if (pn + 1 < getTotalPage()) {
            return PagePositionUtils.fromPageNumber(pn + 1);
        }
        return null;
    }

    /**
     * Navigate to previous page.
     *
     * @param position
     * @return
     */
    @Override
    public String prevPage(String position) {
        int pn = PagePositionUtils.getPageNumber(position);
        if (pn > 0) {
            return PagePositionUtils.fromPageNumber(pn - 1);
        }
        return null;
    }

    @Override
    public boolean isFirstPage() {
        return false;
    }

    /**
     * Navigate to first page.
     *
     * @return
     */
    @Override
    public String firstPage() {
        return PagePositionUtils.fromPageNumber(0);
    }

    @Override
    public boolean isLastPage() {
        return false;
    }

    /**
     * Navigate to last page.
     *
     * @return
     */
    @Override
    public String lastPage() {
        return PagePositionUtils.fromPageNumber(getPluginImpl().getPageCount() - 1);
    }

    /**
     * Retrieve links of specified page.
     *
     * @param position
     * @return link list.
     */
    @Override
    public List<ReaderSelection> getLinks(String position) {
        return null;
    }

    @Override
    public List<ReaderImage> getImages(String position) {
        return null;
    }

    /**
     * Return the plugin display name.
     *
     * @return
     */
    @Override
    public String displayName() {
        return ComicReaderPlugin.class.getSimpleName();
    }

    /**
     * Try to open the document specified by the path.
     *
     * @param path            The path in local file system.
     * @param documentOptions The document opening options.
     * @param pluginOptions   The plugin options.
     * @return Reader document instance.
     * @throws ReaderException
     */
    @Override
    public ReaderDocument open(String path, ReaderDocumentOptions documentOptions, ReaderPluginOptions pluginOptions) throws ReaderException {
        if (!getPluginImpl().open(path, documentOptions.getDocumentPassword())) {
            if (getPluginImpl().isEncrypted()) {
                throw ReaderException.passwordRequired();
            } else {
                throw ReaderException.cannotOpen();
            }
        }
        if (getPluginImpl().getPageCount() <= 0) {
            throw ReaderException.cannotOpen();
        }
        return this;
    }

    /**
     * Check if drm is supported or not.
     *
     * @return
     */
    @Override
    public boolean supportDrm() {
        return false;
    }

    /**
     * DRM support
     */
    @Override
    public ReaderDrmManager createDrmManager() {
        return null;
    }

    /**
     * Abort current running job if possible.
     */
    @Override
    public void abortCurrentJob() {

    }

    /**
     * Clear setAbortFlag flag.
     */
    @Override
    public void clearAbortFlag() {

    }

    /**
     * Get renderer features.
     *
     * @return renderer features interface.
     */
    @Override
    public ReaderRendererFeatures getRendererFeatures() {
        return this;
    }

    @Override
    public void setChineseConvertType(ReaderChineseConvertType convertType) {

    }

    /**
     * draw content. There are two coordinates system.
     * host coordinates system, the viewportInPage is specified in host coordinates system
     * the bitmapx, bitmapy, width and height can be regarded as viewportInPage coordinates system, whereas viewportInPage is the
     * origin point(0, 0)
     *
     * @param pagePosition        the page position.
     * @param scale       the actual scale used to render page.
     * @param rotation    the rotation.
     * @param gamma
     *@param displayRect the display rect in screen coordinate system.
     * @param pageRect    the page rect in doc coordinate system.
     * @param visibleRect the visible rect in doc coordinate system.
*                    <p/>
*                    bitmap  matrix
*                    (viewportX, viewportY)
*                    |--------------|
*                    |              |
*                    | (x,y)        |
*                    |  |------|    |
*                    |  |      |    |
*                    |  |      |    |
*                    |  |------|    |
*                    |        (w,h) |
*                    |--------------|
     * @param bitmap      the target bitmap to draw content. Caller may use this method to draw part of content.     @return
     */
    @Override
    public boolean draw(String pagePosition, float scale, int rotation, float gamma, RectF displayRect, RectF pageRect, RectF visibleRect, Bitmap bitmap) {
        final int pn = PagePositionUtils.getPageNumber(pagePosition);
        return getPluginImpl().drawPage(pn, scale, rotation, displayRect, pageRect, visibleRect, bitmap);
    }

    /**
     * Check if the document support scale or not.
     *
     * @return true if supports.
     */
    @Override
    public boolean supportScale() {
        return true;
    }

    /**
     * support font size adjustment.
     *
     * @return
     */
    @Override
    public boolean supportFontSizeAdjustment() {
        return false;
    }

    @Override
    public boolean supportFontGammaAdjustment() {
        return false;
    }

    /**
     * support type face adjustment.
     *
     * @return
     */
    @Override
    public boolean supportTypefaceAdjustment() {
        return false;
    }

    @Override
    public boolean supportConvertBetweenSimplifiedAndTraditionalChineseText() {
        return false;
    }

    @Override
    public boolean searchPrevious(ReaderSearchOptions options) {
        return false;
    }

    @Override
    public boolean searchNext(ReaderSearchOptions options) {
        return false;
    }

    @Override
    public boolean searchInPage(int currentPage, ReaderSearchOptions options,boolean clear) {
        return false;
    }

    @Override
    public List<ReaderSelection> searchResults() {
        return null;
    }

    @Override
    public ReaderTextStyle getStyle() {
        return null;
    }

    @Override
    public void setStyle(ReaderTextStyle style) {

    }

    /**
     * Retrieve view options interface.
     */
    @Override
    public ReaderViewOptions getViewOptions() {
        return null;
    }

    /**
     * Retrieve renderer.
     *
     * @return the renderer.
     */
    @Override
    public ReaderRenderer getRenderer() {
        return this;
    }

    /**
     * Retrieve the navigator.
     *
     * @return
     */
    @Override
    public ReaderNavigator getNavigator() {
        return this;
    }

    /**
     * Retrieve text style interface.
     */
    @Override
    public ReaderTextStyleManager getTextStyleManager() {
        return this;
    }

    /**
     * Retrieve reader hit test.
     */
    @Override
    public ReaderHitTestManager getReaderHitTestManager() {
        return this;
    }

    /**
     * Retrieve search interface.
     *
     * @return
     */
    @Override
    public ReaderSearchManager getSearchManager() {
        return this;
    }
}
