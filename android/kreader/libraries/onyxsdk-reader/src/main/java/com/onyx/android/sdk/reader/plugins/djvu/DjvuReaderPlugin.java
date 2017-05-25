package com.onyx.android.sdk.reader.plugins.djvu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.android.sdk.reader.api.ReaderFormField;
import com.onyx.android.sdk.reader.api.ReaderFormManager;
import com.onyx.android.sdk.reader.api.ReaderImage;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderDocumentMetadata;
import com.onyx.android.sdk.reader.api.ReaderDocumentOptions;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDrmManager;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderHitTestArgs;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderHitTestOptions;
import com.onyx.android.sdk.reader.api.ReaderNavigator;
import com.onyx.android.sdk.reader.api.ReaderPlugin;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.api.ReaderRenderer;
import com.onyx.android.sdk.reader.api.ReaderRendererFeatures;
import com.onyx.android.sdk.reader.api.ReaderSearchManager;
import com.onyx.android.sdk.reader.api.ReaderSearchOptions;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.api.ReaderSentence;
import com.onyx.android.sdk.reader.api.ReaderTextSplitter;
import com.onyx.android.sdk.reader.api.ReaderTextStyleManager;
import com.onyx.android.sdk.reader.api.ReaderView;
import com.onyx.android.sdk.reader.api.ReaderViewOptions;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

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
        ReaderFormManager,
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
        float size [] = {0, 0};
        if (!getPluginImpl().getPageSize(0, size)) {
            return false;
        }
        float zoom = PageUtils.scaleToPage(size[0], size[1], bitmap.getWidth(), bitmap.getHeight());
        return getPluginImpl().drawPage(0, bitmap, zoom, bitmap.getWidth(), bitmap.getHeight(),
                0, 0, bitmap.getWidth(), bitmap.getHeight());
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
    public boolean supportTextPage() {
        return false;
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
    public ReaderView getView(ReaderViewOptions viewOptions) {
        return this;
    }

    @Override
    public boolean readBuiltinOptions(BaseOptions options) {
        return false;
    }

    @Override
    public boolean saveOptions() {
        return true;
    }

    @Override
    public void close() {
        getPluginImpl().close();
    }

    @Override
    public void updateDocumentOptions(ReaderDocumentOptions documentOptions, ReaderPluginOptions pluginOptions) {

    }

    @Override
    public boolean activateDeviceDRM(String deviceId, String certificate) {
        return false;
    }

    @Override
    public ReaderSelection selectWordOnScreen(ReaderHitTestArgs hitTest, ReaderTextSplitter splitter) {
        return null;
    }

    @Override
    public String position(ReaderHitTestArgs hitTest) {
        return null;
    }

    @Override
    public ReaderSelection selectOnScreen(ReaderHitTestArgs start, ReaderHitTestArgs end, ReaderHitTestOptions hitTestOptions) {
        return null;
    }

    @Override
    public ReaderSelection selectOnScreen(String pagePosition, String startPosition, String endPosition) {
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
    public int getPageNumberByPosition(String position) {
        return -1;
    }

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
    public boolean isFirstPage() {
        return false;
    }

    @Override
    public String firstPage() {
        return PagePositionUtils.fromPageNumber(0);
    }

    @Override
    public boolean isLastPage() {
        return false;
    }

    @Override
    public String lastPage() {
        return PagePositionUtils.fromPageNumber(getTotalPage() - 1);
    }

    @Override
    public List<ReaderSelection> getLinks(String position) {
        return null;
    }

    @Override
    public List<ReaderImage> getImages(String position) {
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
    public void setChineseConvertType(ReaderChineseConvertType convertType) {

    }

    @Override
    public void setTextGamma(float gamma) {

    }

    @Override
    public boolean draw(String pagePosition, float scale, int rotation, final RectF displayRect, final RectF pageRect, final RectF visibleRect, Bitmap bitmap) {
        benchmark.restart();
        try {
            final int pn = PagePositionUtils.getPageNumber(pagePosition);
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
    public boolean supportFontGammaAdjustment() {
        return false;
    }

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
    public boolean searchInPage(int currentPage, ReaderSearchOptions options, boolean clear) {
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

    @Override
    public ReaderFormManager getFormManager() {
        return this;
    }

    @Override
    public boolean loadFormFields(int page, List<ReaderFormField> fields) {
        return false;
    }
}
