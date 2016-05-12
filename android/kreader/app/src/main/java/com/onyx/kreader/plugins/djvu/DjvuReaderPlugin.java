package com.onyx.kreader.plugins.djvu;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderDRMCallback;
import com.onyx.kreader.api.ReaderDocument;
import com.onyx.kreader.api.ReaderDocumentMetadata;
import com.onyx.kreader.api.ReaderDocumentOptions;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.api.ReaderDrmManager;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.api.ReaderHitTestArgs;
import com.onyx.kreader.api.ReaderHitTestManager;
import com.onyx.kreader.api.ReaderLink;
import com.onyx.kreader.api.ReaderNavigator;
import com.onyx.kreader.api.ReaderPlugin;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.api.ReaderRenderer;
import com.onyx.kreader.api.ReaderRendererFeatures;
import com.onyx.kreader.api.ReaderSearchManager;
import com.onyx.kreader.api.ReaderSearchOptions;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.api.ReaderTextSplitter;
import com.onyx.kreader.api.ReaderTextStyleManager;
import com.onyx.kreader.api.ReaderView;
import com.onyx.kreader.api.ReaderViewOptions;
import com.onyx.kreader.common.Benchmark;
import com.onyx.kreader.host.math.PageUtils;
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
    public boolean readCover(ReaderBitmap bitmap) {
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
    public boolean readTableOfContent(ReaderDocumentTableOfContent toc) {
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
    public ReaderSelection select(ReaderHitTestArgs start, ReaderHitTestArgs end) {
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
    public String getFilePath() {
        return getPluginImpl().getFilePath();
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
    public boolean draw(String page, float scale, int rotation, ReaderBitmap bitmap, final RectF displayRect, final RectF pageRect, final RectF visibleRect) {
        benchmark.restart();
        try {
            final int pn = PagePositionUtils.getPageNumber(page);
            return getPluginImpl().drawPage(pn, bitmap.getBitmap(),
                    scale, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight(),
                    (int)visibleRect.left, (int)visibleRect.top, (int)visibleRect.width(), (int)visibleRect.height());
        } finally {
            Log.e(TAG, "rendering takes: " + benchmark.duration());
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
