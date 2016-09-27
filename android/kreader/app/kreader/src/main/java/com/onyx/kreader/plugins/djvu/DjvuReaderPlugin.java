package com.onyx.kreader.plugins.djvu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.api.*;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.utils.PagePositionUtils;

import java.util.List;

/**
 * Created by joy on 3/2/16.
 */
public class DjvuReaderPlugin implements ReaderPlugin,
        ReaderDocument,
        ReaderView,
        ReaderRenderer,
        ReaderNavigator,
        ReaderSearchManager,
        ReaderTextStyleManager,
        ReaderDrmManager,
        ReaderHitTestManager,
        ReaderRendererFeatures {

    private static String TAG = DjvuReaderPlugin.class.getSimpleName();

    static public boolean accept(final String path) {
        return path.toLowerCase().endsWith(".djvu");
    }

    private DjvuJniWrapper impl = null;
    private Benchmark benchmark = new Benchmark();

    public DjvuReaderPlugin(Context context, ReaderPluginOptions pluginOptions) {

    }

    public DjvuJniWrapper getPluginImpl() {
        if (impl == null) {
            impl = new DjvuJniWrapper();
        }
        return impl;
    }

    @Override
    public boolean readMetadata(ReaderDocumentMetadata metadata) {
        return false;
    }

    @Override
    public boolean readCover(Bitmap bitmap) {
        return false;
    }

    @Override
    public RectF getPageOriginSize(String position) {
        float size [] = {0, 0};
        if (getPluginImpl().getPageSize(Integer.parseInt(position), size)) {
            return new RectF(0, 0, size[0], size[1]);
        }
        return null;
    }

    @Override
    public boolean isTextPage(String position) {
        return false;
    }

    @Override
    public String getPageText(String position) {
        // TODO to be implemented
        return "";
    }

    @Override
    public ReaderSentence getSentence(String position, String sentenceStartPosition) {
        return null;
    }

    @Override
    public boolean readTableOfContent(ReaderDocumentTableOfContent toc) {
        return false;
    }

    @Override
    public boolean exportNotes(String sourceDocPath, String targetDocPath, List<Annotation> annotations, List<Shape> scribbles) {
        return false;
    }

    @Override
    public ReaderView getView(ReaderViewOptions viewOptions) {
        return this;
    }

    @Override
    public void close() {
        getPluginImpl().close();

    }

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

    @Override
    public ReaderSelection selectWord(ReaderHitTestArgs hitTest, ReaderTextSplitter splitter) {
        return null;
    }

    @Override
    public String position(ReaderHitTestArgs hitTest) {
        return null;
    }

    @Override
    public ReaderSelection select(ReaderHitTestArgs start, ReaderHitTestArgs end, ReaderHitTestOptions hitTestOptions) {
        return null;
    }

    @Override
    public String getInitPosition() {
        return firstPage();
    }

    @Override
    public String getPositionByPageNumber(int pageNumber) {
        return PagePositionUtils.fromPageNumber(pageNumber);
    }

    @Override
    public String getPositionByPageName(String pageName) {
        return pageName;
    }

    @Override
    public int getTotalPage() {
        return getPluginImpl().getPageCount();
    }

    @Override
    public String nextScreen(String position) {
        return nextPage(position);
    }

    @Override
    public String prevScreen(String position) {
        return prevPage(position);
    }

    @Override
    public String nextPage(String position) {
        int pn = PagePositionUtils.getPageNumber(position);
        if (pn + 1 < getTotalPage()) {
            return PagePositionUtils.fromPageNumber(pn + 1);
        }
        return null;
    }

    @Override
    public String prevPage(String position) {
        int pn = PagePositionUtils.getPageNumber(position);
        if (pn > 0) {
            return PagePositionUtils.fromPageNumber(pn - 1);
        }
        return null;
    }

    @Override
    public String firstPage() {
        return PagePositionUtils.fromPageNumber(0);
    }

    @Override
    public String lastPage() {
        return PagePositionUtils.fromPageNumber(getTotalPage() - 1);
    }

    @Override
    public List<ReaderLink> getLinks(String position) {
        return null;
    }

    @Override
    public String displayName() {
        return DjvuReaderPlugin.class.getSimpleName();
    }

    @Override
    public ReaderDocument open(String path, ReaderDocumentOptions documentOptions, ReaderPluginOptions pluginOptions) throws ReaderException {
        if (!getPluginImpl().open(path)) {
            return null;
        }
        return this;
    }

    @Override
    public boolean supportDrm() {
        return false;
    }

    @Override
    public ReaderDrmManager createDrmManager() {
        return null;
    }

    @Override
    public void abortCurrentJob() {

    }

    @Override
    public void clearAbortFlag() {

    }

    @Override
    public ReaderRendererFeatures getRendererFeatures() {
        return this;
    }

    @Override
    public boolean draw(String page, float scale, int rotation, Bitmap bitmap, final RectF displayRect, final RectF pageRect, final RectF visibleRect) {
        benchmark.restart();
        try {
            final int pn = PagePositionUtils.getPageNumber(page);
            return getPluginImpl().drawPage(pn, bitmap,
                    scale, bitmap.getWidth(), bitmap.getHeight(),
                    -(int)displayRect.left, -(int)displayRect.top, (int)displayRect.width(), (int)displayRect.height());
        } finally {
            benchmark.report("djvu rendering finished");
        }
    }

    @Override
    public boolean supportScale() {
        return true;
    }

    @Override
    public boolean supportFontSizeAdjustment() {
        return false;
    }

    @Override
    public boolean supportTypefaceAdjustment() {
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
    public boolean searchInPage(int currentPage, ReaderSearchOptions options, boolean clear) {
        return false;
    }

    @Override
    public List<ReaderSelection> searchResults() {
        return null;
    }

    @Override
    public void setStyle(ReaderStyle style) {

    }

    @Override
    public ReaderViewOptions getViewOptions() {
        return null;
    }

    @Override
    public ReaderRenderer getRenderer() {
        return this;
    }

    @Override
    public ReaderNavigator getNavigator() {
        return this;
    }

    @Override
    public ReaderTextStyleManager getTextStyleManager() {
        return this;
    }

    @Override
    public ReaderHitTestManager getReaderHitTestManager() {
        return this;
    }

    @Override
    public ReaderSearchManager getSearchManager() {
        return this;
    }
}
