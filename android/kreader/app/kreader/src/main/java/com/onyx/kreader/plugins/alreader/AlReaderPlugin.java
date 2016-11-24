package com.onyx.kreader.plugins.alreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import com.neverland.engbook.forpublic.AlOneSearchResult;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.api.ReaderDRMCallback;
import com.onyx.kreader.api.ReaderDocument;
import com.onyx.kreader.api.ReaderDocumentMetadata;
import com.onyx.kreader.api.ReaderDocumentOptions;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.api.ReaderDrmManager;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.api.ReaderHitTestArgs;
import com.onyx.kreader.api.ReaderHitTestManager;
import com.onyx.kreader.api.ReaderHitTestOptions;
import com.onyx.kreader.api.ReaderNavigator;
import com.onyx.kreader.api.ReaderPlugin;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.api.ReaderRenderer;
import com.onyx.kreader.api.ReaderRendererFeatures;
import com.onyx.kreader.api.ReaderSearchManager;
import com.onyx.kreader.api.ReaderSearchOptions;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.api.ReaderSentence;
import com.onyx.kreader.api.ReaderTextSplitter;
import com.onyx.kreader.api.ReaderTextStyleManager;
import com.onyx.kreader.api.ReaderView;
import com.onyx.kreader.api.ReaderViewOptions;
import com.onyx.kreader.utils.PagePositionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhuzeng on 29/10/2016.
 */

public class AlReaderPlugin implements ReaderPlugin,
        ReaderDocument,
        ReaderView,
        ReaderRenderer,
        ReaderNavigator,
        ReaderSearchManager,
        ReaderTextStyleManager,
        ReaderDrmManager,
        ReaderHitTestManager,
        ReaderRendererFeatures {

    private static final String TAG = AlReaderPlugin.class.getSimpleName();
    private static Set<String> extensionFilters = new HashSet<String>();
    private Benchmark benchmark = new Benchmark();
    private AlReaderWrapper impl;
    private String documentPath;
    private List<ReaderSelection> searchResults = new ArrayList<>();

    private ReaderViewOptions readerViewOptions;

    public AlReaderPlugin(final Context context, final ReaderPluginOptions pluginOptions) {
        if (impl == null) {
            impl = new AlReaderWrapper(context, pluginOptions);
        }
    }

    public AlReaderWrapper getPluginImpl() {
        return impl;
    }

    public String displayName() {
        return TAG;
    }

    static public Set<String> getExtensionFilters() {
        if (extensionFilters.size() <= 0) {
            extensionFilters.add("epub");
            extensionFilters.add("fb2");
            extensionFilters.add("mobi");
            extensionFilters.add("rtf");
            extensionFilters.add("txt");
            extensionFilters.add("doc");
            extensionFilters.add("docx");
        }
        return extensionFilters;
    }

    static public boolean accept(final String path) {
        String extension = FileUtils.getFileExtension(path);
        return getExtensionFilters().contains(extension);
    }

    public ReaderDocument open(final String path, final ReaderDocumentOptions documentOptions, final ReaderPluginOptions pluginOptions) throws ReaderException {
        String docPassword = "";
        String archivePassword = "";
        documentPath = path;
        if (documentOptions != null) {
            docPassword = documentOptions.getDocumentPassword();
            archivePassword = documentOptions.getDocumentPassword();
        }
        long ret = getPluginImpl().openDocument(path, documentOptions);
        if (ret  == AlReaderWrapper.NO_ERROR) {
            return this;
        }
        if (ret == AlReaderWrapper.ERROR_PASSWORD_INVALID) {
            throw ReaderException.passwordRequired();
        } else if (ret == AlReaderWrapper.ERROR_UNKNOWN) {
            throw ReaderException.cannotOpen();
        }
        return null;
    }

    public boolean supportDrm() {
        return false;
    }

    public ReaderDrmManager createDrmManager() {
        return this;
    }

    public boolean readMetadata(final ReaderDocumentMetadata metadata) {
        metadata.setTitle(getPluginImpl().metadataString("Title"));
        metadata.getAuthors().add(getPluginImpl().metadataString("Author"));
        return true;
    }

    public boolean readCover(final Bitmap bitmap) {
        return false;
    }

    public RectF getPageOriginSize(final String position) {
        return new RectF(0, 0, readerViewOptions.getViewWidth(), readerViewOptions.getViewHeight());
    }

    @Override
    public boolean isTextPage(String position) {
        return true;
    }

    @Override
    public String getPageText(String position) {
        return null;
    }

    @Override
    public ReaderSentence getSentence(String position, String sentenceStartPosition) {
        return null;
    }

    public void abortCurrentJob() {
    }

    public void clearAbortFlag() {
    }

    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc) {
        return false;
    }

    @Override
    public boolean exportNotes(String sourceDocPath, String targetDocPath, List<Annotation> annotations, List<Shape> scribbles) {
        return false;
    }

    public ReaderView getView(final ReaderViewOptions viewOptions) {
        readerViewOptions = viewOptions;
        getPluginImpl().setViewSize(readerViewOptions.getViewWidth(), readerViewOptions.getViewHeight());
        return this;
    }

    public ReaderRendererFeatures getRendererFeatures() {
        return this;
    }

    public void close() {
        getPluginImpl().closeDocument();
    }

    public ReaderViewOptions getViewOptions() {
        return readerViewOptions;
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

    @Override
    public ReaderTextStyle getStyle() {
        return getPluginImpl().getStyle();
    }

    /**
     * set stream document style. ignore.
     * @param style
     */
    public void setStyle(final ReaderTextStyle style) {
        getPluginImpl().setStyle(style);
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
    public List<ReaderSelection> getLinks(final String position) {
        return null;
    }

    /**
     * Retrieve search interface.
     * @return
     */
    public ReaderSearchManager getSearchManager() {
        return this;
    }

    public boolean draw(final String pagePosition, final float scale, final int rotation, final Bitmap bitmap, final RectF displayRect, final RectF pageRect, final RectF visibleRect) {
        getPluginImpl().draw(bitmap, (int)displayRect.width(), (int)displayRect.height());
        return true;
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
        return getPluginImpl().getTotalPage();
    }

    @Override
    public int getCurrentPageNumber() {
        return getPluginImpl().getCurrentPage();
    }

    @Override
    public String getCurrentPosition() {
        return PagePositionUtils.fromPosition(getPluginImpl().getCurrentPosition());
    }

    /**
     * Navigate to specified position.
     * @return
     */
    public boolean gotoPosition(final String position) {
        int pos = PagePositionUtils.getPosition(position);
        return getPluginImpl().gotoPosition(pos);
    }

    @Override
    public boolean gotoPage(int page) {
        return getPluginImpl().gotoPage(page);
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
        if (!getPluginImpl().nextPage()) {
            return null;
        }
        return PagePositionUtils.fromPageNumber(getPluginImpl().getCurrentPage());
//        int pn = PagePositionUtils.getPageNumber(position);
//        if (pn + 1 < getTotalPage()) {
//            return PagePositionUtils.fromPageNumber(pn + 1);
//        }
//        return null;
    }

    /**
     * Navigate to previous page.
     * @return
     */
    public String prevPage(final String position) {
        if (!getPluginImpl().prevPage()) {
            return null;
        }
        int pn = PagePositionUtils.getPageNumber(position);
        if (pn > 0) {
            return PagePositionUtils.fromPageNumber(pn - 1);
        }
        return null;

    }

    @Override
    public boolean isFirstPage() {
        return getPluginImpl().isFirstPage();
    }

    /**
     * Navigate to first page.
     * @return
     */
    public String firstPage() {
        return PagePositionUtils.fromPageNumber(0);
    }

    @Override
    public boolean isLastPage() {
        return getPluginImpl().isLastPage();
    }

    /**
     * Navigate to last page.
     * @return
     */
    public String lastPage() {
        return PagePositionUtils.fromPageNumber(getTotalPage() - 1);
    }

    public boolean searchPrevious(final ReaderSearchOptions options) {
        searchResults.clear();
        int page = Integer.parseInt(options.fromPage());
        for (int i = page; i >= 0; i--) {
            if (searchInPage(i, options,false)) {
                return true;
            }
        }
        return false;
    }

    public boolean searchNext(final ReaderSearchOptions options) {
        searchResults.clear();
        int page = Integer.parseInt(options.fromPage());
        for (int i = page; i < getTotalPage(); i++) {
            if (searchInPage(i, options,false)) {
                return true;
            }
        }
        return false;
    }

    public boolean searchInPage(final int currentPage, final ReaderSearchOptions options, boolean clear) {
        if (clear){
            searchResults.clear();
        }
        List<AlOneSearchResult> list = getPluginImpl().search(options.pattern());
        return false;
    }

    public List<ReaderSelection> searchResults() {
        return searchResults;
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

    public ReaderSelection select(final ReaderHitTestArgs start, final ReaderHitTestArgs end, ReaderHitTestOptions hitTestOptions) {
        return null;
    }

    public boolean supportScale() {
        return false;
    }

    public boolean supportFontSizeAdjustment() {
        return true;
    }

    public boolean supportTypefaceAdjustment() {
        return true;
    }

    @Override
    public boolean supportTextPage() {
        return true;
    }
}
