package com.onyx.kreader.plugins.neopdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.StringUtils;
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
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.utils.PagePositionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class NeoPdfReaderPlugin implements ReaderPlugin,
        ReaderDocument,
        ReaderView,
        ReaderRenderer,
        ReaderNavigator,
        ReaderSearchManager,
        ReaderTextStyleManager,
        ReaderDrmManager,
        ReaderHitTestManager,
        ReaderRendererFeatures {

    private static final String TAG = NeoPdfReaderPlugin.class.getSimpleName();
    private Benchmark benchmark = new Benchmark();

    private NeoPdfJniWrapper impl;
    private String documentPath;

    List<ReaderSelection> searchResults = new ArrayList<>();
    Map<String, List<ReaderSelection>> pageLinks = new HashMap<>();

    private ReaderViewOptions readerViewOptions;

    public NeoPdfReaderPlugin(final Context context, final ReaderPluginOptions pluginOptions) {
    }

    public NeoPdfJniWrapper getPluginImpl() {
        if (impl == null) {
            impl = new NeoPdfJniWrapper();
            impl.nativeInitLibrary();
        }
        return impl;
    }

    public String displayName() {
        return NeoPdfReaderPlugin.class.getSimpleName();
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
        long ret = getPluginImpl().openDocument(path, docPassword);
        if (ret  == NeoPdfJniWrapper.NO_ERROR) {
            return this;
        }
        if (ret == NeoPdfJniWrapper.ERROR_PASSWORD_INVALID) {
            throw ReaderException.passwordRequired();
        } else if (ret == NeoPdfJniWrapper.ERROR_UNKNOWN) {
            throw ReaderException.cannotOpen();
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
        metadata.setTitle(getPluginImpl().metadataString("Title"));
        metadata.getAuthors().add(getPluginImpl().metadataString("Author"));
        return true;
    }

    public boolean readCover(final Bitmap bitmap) {
        return getPluginImpl().drawPage(0, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                0, bitmap);
    }

    public RectF getPageOriginSize(final String position) {
        float size [] = {0, 0};
        getPluginImpl().pageSize(Integer.parseInt(position), size);
        return new RectF(0, 0, size[0], size[1]);
    }

    @Override
    public boolean supportTextPage() {
        return true;
    }

    @Override
    public boolean isTextPage(String position) {
        return getPluginImpl().isTextPage(Integer.parseInt(position));
    }

    @Override
    public String getPageText(String position) {
        return getPluginImpl().getPageText(PagePositionUtils.getPageNumber(position));
    }

    @Override
    public ReaderSentence getSentence(String position, String sentenceStartPosition) {
        int page = PagePositionUtils.getPageNumber(position);
        int startIndex = StringUtils.isNullOrEmpty(sentenceStartPosition) ? 0 :
                Integer.parseInt(sentenceStartPosition);
        return getPluginImpl().getSentence(page, startIndex);
    }

    public void abortCurrentJob() {

    }

    public void clearAbortFlag() {

    }

    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc) {
        return getPluginImpl().getTableOfContent(toc.getRootEntry());
    }

    @Override
    public boolean exportNotes(String sourceDocPath, String targetDocPath, List<Annotation> annotations, List<Shape> scribbles) {
        return false;
    }

    public ReaderView getView(final ReaderViewOptions viewOptions) {
        readerViewOptions = viewOptions;
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

    /**
     * set stream document style. ignore.
     * @param style
     */
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
    public List<ReaderSelection> getLinks(final String position) {
        List<ReaderSelection> list = pageLinks.get(position);
        if (list == null) {
            list = new ArrayList<>();
            int page = PagePositionUtils.getPageNumber(position);
            if (!getPluginImpl().getPageLinks(page, list)) {
                return list;
            }
            pageLinks.put(position, list);
        }
        ArrayList<ReaderSelection> copy = new ArrayList<>();
        for (ReaderSelection link : list) {
            copy.add(((NeoPdfSelection)link).clone());
        }
        return copy;
    }

    /**
     * Retrieve search interface.
     * @return
     */
    public ReaderSearchManager getSearchManager() {
        return this;
    }

    public boolean draw(final String page, final float scale, final int rotation, final Bitmap bitmap, final RectF displayRect, final RectF pageRect, final RectF visibleRect) {
        benchmark.restart();
        boolean ret = getPluginImpl().drawPage(PagePositionUtils.getPageNumber(page),
                (int)displayRect.left,
                (int)displayRect.top,
                (int)displayRect.width(),
                (int)displayRect.height(),
                rotation, bitmap);
        Debug.d(TAG, "rendering takes: " + benchmark.duration());
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
        return getPluginImpl().pageCount();
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
        getPluginImpl().searchInPage(currentPage, 0, 0,
                getViewOptions().getViewWidth(),
                getViewOptions().getViewHeight(),
                0, options.pattern(), options.isCaseSensitive(),
                options.isMatchWholeWord(), options.contextLength(),
                searchResults);
        return searchResults.size() > 0;
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
        NeoPdfSelection selection = new NeoPdfSelection(start.pageName);
        getPluginImpl().hitTest(PagePositionUtils.getPageNumber(start.pageName),
                (int) start.pageDisplayRect.left,
                (int) start.pageDisplayRect.top,
                (int) start.pageDisplayRect.width(),
                (int) start.pageDisplayRect.height(),
                start.pageDisplayOrientation,
                (int) start.point.x,
                (int) start.point.y,
                (int) end.point.x,
                (int) end.point.y,
                hitTestOptions.isSelectingWord(),
                selection);
        return selection;
    }

    public boolean supportScale() {
        if (StringUtils.isNullOrEmpty(documentPath)) {
            return false;
        }
        return documentPath.toLowerCase().endsWith("pdf");
    }

    public boolean supportFontSizeAdjustment() {
        return false;
    }

    public boolean supportTypefaceAdjustment() {
        return false;
    }


}
