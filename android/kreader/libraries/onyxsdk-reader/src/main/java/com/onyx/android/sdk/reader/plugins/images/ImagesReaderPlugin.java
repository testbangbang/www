package com.onyx.android.sdk.reader.plugins.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import com.onyx.android.sdk.data.SortOrder;
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
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.ComparatorUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        ReaderDrmManager,
        ReaderFormManager,
        ReaderHitTestManager,
        ReaderRendererFeatures {

    private static final String TAG = ImagesReaderPlugin.class.getSimpleName();
    private Benchmark benchmark = new Benchmark();

    private ImagesWrapper impl;
    private String documentPath;
    private List<String> pageList = new ArrayList<String>();
    static private Set<String> extensionFilters = new HashSet<String>();

    public ImagesReaderPlugin(final Context context, final ReaderPluginOptions pluginOptions) {
    }

    public ImagesWrapper getPluginImpl() {
        if (impl == null) {
            impl = new ImagesAndroidWrapper();
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
            extensionFilters.add("tif");
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
        FileUtils.collectFiles(baseDir, getExtensionFilters(), false, pageList);
        Collections.sort(pageList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return ComparatorUtils.stringComparator(lhs, rhs, SortOrder.Asc);
            }
        });
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

    public boolean readCover(final Bitmap bitmap) {
        return false;
    }

    public RectF getPageOriginSize(final String position) {
        ImagesWrapper.ImageInformation imageInformation = getPluginImpl().imageInfo(pageList.get(Integer.parseInt(position)));
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

    @Override
    public boolean readBuiltinOptions(BaseOptions options) {
        return false;
    }

    @Override
    public boolean saveOptions() {
        return false;
    }

    public ReaderRendererFeatures getRendererFeatures() {
        return this;
    }

    @Override
    public void setChineseConvertType(ReaderChineseConvertType convertType) {

    }

    @Override
    public void setTextGamma(float gamma) {

    }

    public void close() {
        getPluginImpl().closeAll();
        pageList.clear();
    }

    @Override
    public void updateDocumentOptions(ReaderDocumentOptions documentOptions, ReaderPluginOptions pluginOptions) {

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
        return null;
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

    @Override
    public List<ReaderImage> getImages(String position) {
        return null;
    }

    @Override
    public List<ReaderRichMedia> getRichMedias(String position) {
        return null;
    }

    /**
     * Retrieve search interface.
     * @return
     */
    public ReaderSearchManager getSearchManager() {
        return this;
    }

    @Override
    public ReaderFormManager getFormManager() {
        return this;
    }

    public boolean draw(final String pagePosition, final float scale, final int rotation, final RectF displayRect, final RectF pageRect, final RectF visibleRect, final Bitmap bitmap) {
        final String path = getImagePath(pagePosition);
        if (StringUtils.isNullOrEmpty(path)) {
            return false;
        }
        benchmark.restart();
        // getById doc position
        boolean ret = getPluginImpl().drawImage(path, scale, rotation, displayRect, pageRect, visibleRect, bitmap);
        Log.e(TAG, "rendering png:　"+ benchmark.duration());
        return ret;
    }

    /**
     * Retrieve the default init position.
     * @return
     */
    public String getInitPosition() {
        return getPositionByPageName(documentPath);
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
        int index = pageList.indexOf(pageName);
        if (index < 0 || index >= pageList.size()) {
            return null;
        }
        return PagePositionUtils.fromPageNumber(index);
    }

    @Override
    public int getPageNumberByPosition(String position) {
        return -1;
    }

    private String getImagePath(final String position) {
        int index = PagePositionUtils.getPageNumber(position);
        if (index < 0 || index >= pageList.size()) {
            return null;
        }
        return pageList.get(index);
    }

    /**
     * Return total page number.
     * @return 1 based total page number.
     */
    public int getTotalPage() {
        return pageList.size();
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

    @Override
    public boolean isFirstPage() {
        return false;
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
        return false;
    }

    /**
     * Navigate to last page.
     * @return
     */
    public String lastPage() {
        return PagePositionUtils.fromPageNumber(getTotalPage() - 1);
    }

    public boolean searchPrevious(final ReaderSearchOptions options) {
        return false;
    }

    public boolean searchNext(final ReaderSearchOptions options) {
        return false;
    }

    @Override
    public boolean searchInPage(int currentPage, ReaderSearchOptions options, boolean clear) {
        return false;
    }

    public List<ReaderSelection> searchResults() {
        return null;
    }


    public boolean activateDeviceDRM(String deviceId, String certificate) {
        return false;
    }

    public ReaderSelection selectWordOnScreen(final ReaderHitTestArgs hitTest, final ReaderTextSplitter splitter) {

        return null;
    }

    public String position(final ReaderHitTestArgs hitTest) {
        return null;
    }

    public ReaderSelection selectOnScreen(final ReaderHitTestArgs start, final ReaderHitTestArgs end, ReaderHitTestOptions hitTestOptions) {
        return null;
    }

    @Override
    public ReaderSelection selectOnScreen(String pagePosition, String startPosition, String endPosition) {
        return null;
    }

    @Override
    public List<ReaderSelection> allText(final String pagePosition) {
        return null;
    }

    public boolean supportScale() {
        return true;
    }

    public boolean supportFontSizeAdjustment() {
        return false;
    }

    @Override
    public boolean supportFontGammaAdjustment() {
        return false;
    }

    public boolean supportTypefaceAdjustment() {
        return false;
    }

    @Override
    public boolean supportConvertBetweenSimplifiedAndTraditionalChineseText() {
        return false;
    }

    public boolean isCustomFormEnabled() {
        return false;
    }

    @Override
    public boolean loadFormFields(int page, List<ReaderFormField> fields) {
        return false;
    }
}
