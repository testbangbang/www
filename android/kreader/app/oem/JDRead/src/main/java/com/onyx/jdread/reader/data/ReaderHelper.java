package com.onyx.jdread.reader.data;

import android.content.Context;
import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderDocumentMetadata;
import com.onyx.android.sdk.reader.api.ReaderDocumentOptions;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderFormManager;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderNavigator;
import com.onyx.android.sdk.reader.api.ReaderPlugin;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.api.ReaderRenderer;
import com.onyx.android.sdk.reader.api.ReaderRendererFeatures;
import com.onyx.android.sdk.reader.api.ReaderSearchManager;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.api.ReaderTextStyleManager;
import com.onyx.android.sdk.reader.api.ReaderView;
import com.onyx.android.sdk.reader.cache.BitmapReferenceLruCache;
import com.onyx.android.sdk.reader.cache.ReaderBitmapReferenceImpl;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.impl.ReaderViewOptionsImpl;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.plugins.alreader.AlReaderPlugin;
import com.onyx.android.sdk.reader.plugins.comic.ComicReaderPlugin;
import com.onyx.android.sdk.reader.plugins.djvu.DjvuReaderPlugin;
import com.onyx.android.sdk.reader.plugins.images.ImagesReaderPlugin;
import com.onyx.android.sdk.reader.plugins.jeb.JEBReaderPlugin;
import com.onyx.android.sdk.reader.plugins.neopdf.NeoPdfReaderPlugin;
import com.onyx.android.sdk.reader.reflow.ImageReflowManager;
import com.onyx.android.sdk.reader.utils.ImageUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.layout.ReaderLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class ReaderHelper {
    private static final String TAG = ReaderHelper.class.getSimpleName();
    private String documentMd5;
    private ReaderDocument readerDocument;
    private ReaderViewOptionsImpl viewOptions = new ReaderViewOptionsImpl();
    private ReaderPlugin plugin;
    private ReaderView view;
    private ReaderNavigator navigator;
    private ReaderRenderer renderer;
    private ReaderRendererFeatures rendererFeatures;
    private ReaderTextStyleManager textStyleManager;
    private ReaderSearchManager searchManager;
    private ReaderFormManager formManager;
    private ReaderHitTestManager hitTestManager;
    private String firstVisiblePagePosition;
    private ReaderLayoutManager readerLayoutManager = null;
    private BaseOptions documentOptions = new BaseOptions();
    private ImageReflowManager imageReflowManager;
    private BitmapReferenceLruCache bitmapCache;

    public ReaderHelper() {
    }

    public boolean selectPlugin(final Context context, final DocumentInfo documentInfo, final ReaderPluginOptions pluginOptions) {
        if (NeoPdfReaderPlugin.accept(documentInfo.getBookPath())) {
            plugin = new NeoPdfReaderPlugin(context, pluginOptions);
        } else if (AlReaderPlugin.accept(documentInfo.getBookPath())) {
            plugin = new AlReaderPlugin(context, pluginOptions);
        } else if (JEBReaderPlugin.accept(documentInfo.getBookPath())) {
            plugin = new JEBReaderPlugin(context, pluginOptions);
        } else if (ImagesReaderPlugin.accept(documentInfo.getBookPath())) {
            plugin = new ImagesReaderPlugin(context, pluginOptions);
        } else if (DjvuReaderPlugin.accept(documentInfo.getBookPath())) {
            plugin = new DjvuReaderPlugin(context, pluginOptions);
        } else if (ComicReaderPlugin.accept(documentInfo.getBookPath())) {
            plugin = new ComicReaderPlugin(context, pluginOptions);
        }
        return (plugin != null);
    }

    public void saveReaderDocument(ReaderDocument readerDocument,DocumentInfo documentInfo) {
        this.readerDocument = readerDocument;
        setFileMd5(documentInfo);
        initData(JDReadApplication.getInstance().getApplicationContext());
    }

    public void setFileMd5(DocumentInfo documentInfo){
        try {
            if (StringUtils.isNotBlank(getDocumentOptions().getMd5())) {
                documentMd5 = getDocumentOptions().getMd5();
            } else {
                documentMd5 = FileUtils.computeMD5(new File(documentInfo.getBookPath()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateView() {
        view = readerDocument.getView(viewOptions);
        renderer = view.getRenderer();
        navigator = view.getNavigator();
        rendererFeatures = renderer.getRendererFeatures();
        textStyleManager = view.getTextStyleManager();
        hitTestManager = view.getReaderHitTestManager();
        searchManager = view.getSearchManager();
        formManager = view.getFormManager();
    }

    public ReaderDocument openDocument(final String path, final ReaderDocumentOptions documentOptions, final ReaderPluginOptions pluginOptions) throws Exception {
        return plugin.open(path, documentOptions, pluginOptions);
    }

    public ReaderLayoutManager getReaderLayoutManager() {
        if (readerLayoutManager == null) {
            readerLayoutManager = new ReaderLayoutManager(this,
                    getDocument(),
                    getNavigator(),
                    getRendererFeatures(),
                    getTextStyleManager(),
                    getViewOptions());
        }
        return readerLayoutManager;
    }



    public boolean nextScreen() throws ReaderException {
        return getReaderLayoutManager().nextScreen();
    }

    public boolean previousScreen() throws ReaderException{
        return getReaderLayoutManager().prevScreen();
    }

    public boolean gotoPosition(String position) throws ReaderException{
        return getReaderLayoutManager().gotoPosition(position);
    }

    public void onDocumentClosed() {
        clearBitmapCache();
        clearImageReflowManager();
        releaseWordAnalyzer();
    }

    public void updateViewportSize(int newWidth, int newHeight) throws ReaderException {
        getViewOptions().setSize(newWidth, newHeight);
        updateView();
        getReaderLayoutManager().init();
        getReaderLayoutManager().updateViewportSize();
    }

    public ImageReflowManager getImageReflowManager() {
        return imageReflowManager;
    }

    public void onLayoutChanged() {
        setLayoutChanged(true);
    }

    public void onPositionChanged() {
    }

    public void beforeDraw(ReaderBitmapReferenceImpl bitmap) {
    }

    public void afterDraw(ReaderBitmapReferenceImpl bitmap) {
    }

    public final ReaderBitmapReferenceImpl getViewportBitmap() {
        return null;
    }

    private ReaderPlugin getPlugin() {
        return plugin;
    }

    public void setAbortFlag() {
        getPlugin().abortCurrentJob();
    }

    public void clearAbortFlag() {
        getPlugin().clearAbortFlag();
    }

    public boolean isReaderLayoutManagerCreated() {
        return false;
    }

    public ReaderHitTestManager getHitTestManager() {
        return null;
    }

    public void transferRenderBitmapToViewport(ReaderBitmapReferenceImpl renderBitmap) {

    }

    public void returnBitmapToCache(ReaderBitmapReferenceImpl bitmap) {

    }

    public BitmapReferenceLruCache getBitmapCache() {
        return bitmapCache;
    }

    public void setLayoutChanged(boolean layoutChanged) {

    }

    public boolean isLayoutChanged() {
        return false;
    }

    public void initData(Context context) {
        initImageReflowManager(context);
        initBitmapCache();
    }

    private void initLayoutManager() {

    }

    private void initWordAnalyzerInBackground(final Context context) {

    }

    private void initImageReflowManager(Context context) {
        if (imageReflowManager == null) {
            File cacheLocation = new File(context.getCacheDir(), ImageReflowManager.class.getCanonicalName());
            if (!cacheLocation.exists()) {
                cacheLocation.mkdirs();
            }
            imageReflowManager = new ImageReflowManager(documentMd5,
                    cacheLocation,
                    getViewOptions().getViewWidth(),
                    getViewOptions().getViewHeight());
        }
    }

    private void clearImageReflowManager() {
        if (imageReflowManager != null) {
            imageReflowManager.release();
        }
    }

    private void initBitmapCache() {
        if (bitmapCache == null) {
            bitmapCache = new BitmapReferenceLruCache(5);
        }
    }

    private void clearBitmapCache() {
        if (bitmapCache != null) {
            bitmapCache.clear();
        }
    }

    private void releaseWordAnalyzer() {

    }

    public ReaderPluginOptions getPluginOptions() {
        return null;
    }

    public ReaderViewOptionsImpl getViewOptions() {
        return viewOptions;
    }

    public BaseOptions getDocumentOptions() {
        return documentOptions;
    }

    public ReaderDocument getDocument() {
        return readerDocument;
    }

    public ReaderView getView() {
        return null;
    }

    public ReaderNavigator getNavigator() {
        return navigator;
    }

    public ReaderRenderer getRenderer() {
        return renderer;
    }

    public ReaderRendererFeatures getRendererFeatures() {
        return rendererFeatures;
    }

    public ReaderTextStyleManager getTextStyleManager() {
        return textStyleManager;
    }

    public ReaderSearchManager getSearchManager() {
        return searchManager;
    }

    public ReaderFormManager getFormManager() {
        return formManager;
    }

    private void translateToScreen(PageInfo pageInfo, List<RectF> list) {
        for (RectF rect : list) {
            PageUtils.translate(pageInfo.getDisplayRect().left,
                    pageInfo.getDisplayRect().top,
                    pageInfo.getActualScale(),
                    rect);
        }
    }

    public List<RectF> collectTextRectangleList(final ReaderViewInfo viewInfo) {
        final List<ReaderSelection> selectionList = getHitTestManager().allText(viewInfo.getFirstVisiblePageName());
        if (selectionList == null) {
            return null;
        }
        final List<RectF> list = new ArrayList<>();
        for (ReaderSelection selection : selectionList) {
            list.addAll(selection.getRectangles());
        }
        translateToScreen(viewInfo.getFirstVisiblePage(), list);
        return list;
    }

    public void applyPostBitmapProcess(final ReaderViewInfo viewInfo, ReaderBitmapReferenceImpl bitmap) {
        applyGamma(viewInfo, bitmap);
        applyEmbolden(bitmap);
        applySaturation(bitmap);
    }

    private void applyGamma(final ReaderViewInfo viewInfo, final ReaderBitmapReferenceImpl bitmap) {
        boolean applyImageGammaOnly = true;
        final RectF parent = new RectF(0, 0, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight());
        if (applyImageGammaOnly) {
            final List<RectF> imageRegions = new ArrayList();
            imageRegions.add(parent);
            applyImageGamma(bitmap, imageRegions);
        } else {
            final List<RectF> regions = collectTextRectangleList(viewInfo);
            final List<RectF> imageRegions = RectUtils.cutRectByExcludingRegions(parent, regions);
            applyTextGamma(bitmap);
            applyImageGamma(bitmap, imageRegions);
        }
    }

    private void applyTextGamma(final ReaderBitmapReferenceImpl bitmap) {
        if (getDocumentOptions().isTextGamaCorrectionEnabled() &&
                getRenderer().getRendererFeatures().supportFontGammaAdjustment()) {
            bitmap.setTextGammaCorrection(getDocumentOptions().getTextGammaLevel());
        }
    }

    private void applyImageGamma(final ReaderBitmapReferenceImpl bitmap, final List<RectF> regions) {
        float level = getDocumentOptions().getGammaLevel();
        if (getDocumentOptions().isGammaCorrectionEnabled() && !bitmap.isGammaApplied(level)) {
            if (ImageUtils.applyGammaCorrection(bitmap.getBitmap(), level, regions)) {
                bitmap.setGammaCorrection(level);
            }
        }
    }

    private void applyEmbolden(final ReaderBitmapReferenceImpl bitmap) {
        if (getDocumentOptions().isEmboldenLevelEnabled() &&
                bitmap.getEmboldenLevel() != getDocumentOptions().getEmboldenLevel()) {
            if (ImageUtils.applyBitmapEmbolden(bitmap.getBitmap(), getDocumentOptions().getEmboldenLevel())) {
                bitmap.setEmboldenLevel(getDocumentOptions().getEmboldenLevel());
            }
        }
    }

    private void applySaturation(final ReaderBitmapReferenceImpl bitmap) {
    }

    public ReaderDocumentMetadata getDocumentMetadata() {
        return null;
    }

    /**
     * collect all options from reader components to BaseOptions.
     */
    public void saveOptions() {

    }

    private void saveReaderTextStyle(final ReaderTextStyle style) {

    }
}
