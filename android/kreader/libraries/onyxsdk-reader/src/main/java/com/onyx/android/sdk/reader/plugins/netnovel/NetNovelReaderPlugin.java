package com.onyx.android.sdk.reader.plugins.netnovel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderCallback;
import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderDocumentMetadata;
import com.onyx.android.sdk.reader.api.ReaderDocumentOptions;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDrmManager;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderFormField;
import com.onyx.android.sdk.reader.api.ReaderFormManager;
import com.onyx.android.sdk.reader.api.ReaderHitTestArgs;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderHitTestOptions;
import com.onyx.android.sdk.reader.api.ReaderImage;
import com.onyx.android.sdk.reader.api.ReaderNavigator;
import com.onyx.android.sdk.reader.api.ReaderPlugin;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.api.ReaderRenderer;
import com.onyx.android.sdk.reader.api.ReaderRendererFeatures;
import com.onyx.android.sdk.reader.api.ReaderRichMedia;
import com.onyx.android.sdk.reader.api.ReaderSearchManager;
import com.onyx.android.sdk.reader.api.ReaderSearchOptions;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.api.ReaderSentence;
import com.onyx.android.sdk.reader.api.ReaderTextSplitter;
import com.onyx.android.sdk.reader.api.ReaderTextStyleManager;
import com.onyx.android.sdk.reader.api.ReaderView;
import com.onyx.android.sdk.reader.api.ReaderViewOptions;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.List;

/**
 * Created by joy on 3/19/18.
 */

public class NetNovelReaderPlugin implements ReaderPlugin,
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

    private Context context;
    private ReaderPluginOptions pluginOptions;

    private NetNovelReaderWrapper impl;
    private ReaderViewOptions readerViewOptions;

    public NetNovelReaderPlugin(Context context, ReaderPluginOptions pluginOptions) {
        this.context = context;
        this.pluginOptions = pluginOptions;
    }

    public static boolean accept(final String path) {
        String string = path.toLowerCase();
        if (string.endsWith(".jdnovel")) {
            return true;
        }
        return false;
    }

    private NetNovelReaderWrapper getPluginImpl() {
        if (impl == null) {
            impl = new NetNovelReaderWrapper(context, pluginOptions);
        }
        return impl;
    }

    @Override
    public boolean activateDeviceDRM(String deviceId, String certificate) {
        return false;
    }

    @Override
    public boolean isCustomFormEnabled() {
        return false;
    }

    @Override
    public boolean loadFormFields(int page, List<ReaderFormField> fields) {
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
    public String displayName() {
        return null;
    }

    @Override
    public void setReaderCallback(ReaderCallback callback) {
        getPluginImpl().setBookCallback(callback);
    }

    @Override
    public ReaderDocument open(String path, ReaderDocumentOptions documentOptions, ReaderPluginOptions pluginOptions) throws ReaderException {
        if (!getPluginImpl().openDocument(path, documentOptions)) {
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
    public void abortBookLoadingJob() {

    }

    @Override
    public ReaderTextStyle getStyle() {
        return null;
    }

    @Override
    public void setStyle(ReaderTextStyle style) {

    }

    @Override
    public boolean supportScale() {
        return false;
    }

    @Override
    public boolean supportFontSizeAdjustment() {
        return true;
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
    public String getInitPosition() {
        return getPluginImpl().getInitPosition();
    }

    @Override
    public String getPositionByPageNumber(int pageNumber) {
        return getPluginImpl().getPositionOfPageNumber(pageNumber);
    }

    @Override
    public String getPositionByPageName(String pageName) {
        return getPositionByPageNumber(PagePositionUtils.getPageNumber(pageName));
    }

    @Override
    public int getPageNumberByPosition(String position) {
        return getPluginImpl().getPageNumberOfPosition(position);
    }

    @Override
    public int getTotalPage() {
        return getPluginImpl().getTotalPage();
    }

    @Override
    public float getProgress(String position) {
        return getPluginImpl().getProgress();
    }

    @Override
    public int getScreenStartPageNumber() {
        return getPluginImpl().getScreenStartPage();
    }

    @Override
    public int getScreenEndPageNumber() {
        return getPluginImpl().getScreenEndPage();
    }

    @Override
    public String getScreenStartPosition() {
        return getPluginImpl().getScreenStartPosition();
    }

    @Override
    public String getScreenEndPosition() {
        return getPluginImpl().getScreenEndPosition();
    }

    @Override
    public int comparePosition(String pos1, String pos2) {
        return getPluginImpl().comparePosition(pos1, pos2);
    }

    @Override
    public boolean gotoPosition(String position) {
        return getPluginImpl().gotoPosition(position);
    }

    @Override
    public boolean gotoPage(int page) {
        return getPluginImpl().gotoPage(page);
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
        if (!getPluginImpl().nextPage()) {
            return null;
        }
        return PagePositionUtils.fromPageNumber(getPluginImpl().getScreenStartPage());
    }

    @Override
    public String prevPage(String position) {
        if (!getPluginImpl().prevPage()) {
            return null;
        }
        return PagePositionUtils.fromPageNumber(getPluginImpl().getScreenStartPage());
    }

    @Override
    public boolean isFirstPage() {
        return getPluginImpl().isFirstPage();
    }

    @Override
    public String firstPage() {
        return PagePositionUtils.fromPageNumber(0);
    }

    @Override
    public boolean isLastPage() {
        return getPluginImpl().isLastPage();
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
    public List<ReaderRichMedia> getRichMedias(String position) {
        return null;
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
    public boolean draw(String pagePosition, float scale, int rotation, RectF displayRect, RectF pageRect, RectF visibleRect, Bitmap bitmap) {
        getPluginImpl().draw(bitmap, (int)displayRect.width(), (int)displayRect.height());
        return true;
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
        return new RectF(0, 0, readerViewOptions.getViewWidth(), readerViewOptions.getViewHeight());
    }

    @Override
    public boolean supportTextPage() {
        return true;
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

    @Override
    public boolean readTableOfContent(ReaderDocumentTableOfContent toc) {
        return getPluginImpl().readTableOfContent(toc);
    }

    @Override
    public ReaderView getView(ReaderViewOptions viewOptions) {
        readerViewOptions = viewOptions;
        getPluginImpl().setViewSize(readerViewOptions.getViewWidth(), readerViewOptions.getViewHeight());
        return this;
    }

    @Override
    public boolean readBuiltinOptions(BaseOptions options) {
        return false;
    }

    @Override
    public boolean saveOptions() {
        return false;
    }

    @Override
    public void close() {
        getPluginImpl().close();
    }

    @Override
    public void updateDocumentOptions(ReaderDocumentOptions documentOptions, ReaderPluginOptions pluginOptions) {

    }

    @Override
    public ReaderViewOptions getViewOptions() {
        return readerViewOptions;
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
    public List<ReaderSelection> allText(String pagePosition) {
        return null;
    }
}
