package com.onyx.android.sdk.reader.host.wrapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.neverland.engbook.level1.JEBFilesZIP;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.ReaderBaseApp;
import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderDocumentMetadata;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderNavigator;
import com.onyx.android.sdk.reader.api.ReaderPlugin;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.api.ReaderRenderer;
import com.onyx.android.sdk.reader.api.ReaderRendererFeatures;
import com.onyx.android.sdk.reader.api.ReaderSearchManager;
import com.onyx.android.sdk.reader.api.ReaderTextStyleManager;
import com.onyx.android.sdk.reader.api.ReaderView;
import com.onyx.android.sdk.reader.cache.BitmapReferenceLruCache;
import com.onyx.android.sdk.reader.cache.ReaderBitmapReferenceImpl;
import com.onyx.android.sdk.reader.dataprovider.LegacySdkDataUtils;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentMetadataImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderViewOptionsImpl;
import com.onyx.android.sdk.reader.host.layout.ReaderLayoutManager;
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
import com.onyx.android.sdk.utils.StringUtils;

import org.apache.lucene.analysis.cn.AnalyzerAndroidWrapper;

import java.io.File;

/**
 * Created by zhuzeng on 10/5/15.
 * Save all helper data objects in this class.
 */
public class ReaderHelper {
    private static final String TAG = ReaderHelper.class.getSimpleName();

    private String documentPath;
    private String bookName;
    private String documentMd5;
    private ReaderDocumentMetadata documentMetadata;
    private ReaderViewOptionsImpl viewOptions = new ReaderViewOptionsImpl();
    private ReaderPluginOptions pluginOptions;
    private BaseOptions documentOptions = new BaseOptions();

    private ReaderPlugin plugin;
    private ReaderDocument document;
    private ReaderView view;
    private ReaderNavigator navigator;
    private ReaderRenderer renderer;
    private ReaderRendererFeatures rendererFeatures;
    private ReaderTextStyleManager textStyleManager;
    private ReaderSearchManager searchManager;
    // to be used by UI thread
    private ReaderBitmapReferenceImpl viewportBitmap;
    private ReaderLayoutManager readerLayoutManager;
    private ReaderHitTestManager hitTestManager;
    private ImageReflowManager imageReflowManager;
    private BitmapReferenceLruCache bitmapCache;

    private boolean layoutChanged = false;

    public ReaderHelper() {
    }

    public boolean selectPlugin(final Context context, final String path, final ReaderPluginOptions pluginOptions) {
        if (NeoPdfReaderPlugin.accept(path)) {
            plugin = new NeoPdfReaderPlugin(context, pluginOptions);
        } else if (ImagesReaderPlugin.accept(path)) {
            plugin = new ImagesReaderPlugin(context, pluginOptions);
        } else if (DjvuReaderPlugin.accept(path)) {
            plugin = new DjvuReaderPlugin(context, pluginOptions);
        } else if (ComicReaderPlugin.accept(path)) {
            plugin = new ComicReaderPlugin(context, pluginOptions);
        } else if (AlReaderPlugin.accept(path)) {
            plugin = new AlReaderPlugin(context, pluginOptions);
        } else if(JEBReaderPlugin.accept(path)){
            plugin = new JEBReaderPlugin(context,pluginOptions);
        }
        return (plugin != null);
    }

    public void onDocumentOpened(final Context context,
                                 final String path,
                                 final ReaderDocument doc,
                                 final BaseOptions options,
                                 final ReaderPluginOptions pluginOptions) throws Exception {
        documentPath = path;
        document = doc;
        if (StringUtils.isNotBlank(options.getMd5())) {
            documentMd5 = options.getMd5();
        } else {
            documentMd5 = FileUtils.computeMD5(new File(documentPath));
        }
        initLayoutManager();

        getDocumentOptions().setZipPassword(options.getZipPassword());
        getDocumentOptions().setPassword(options.getPassword());
        getDocumentOptions().setCodePage(options.getCodePage());
        getDocumentOptions().setChineseConvertType(options.getChineseConvertType());
        this.pluginOptions = pluginOptions;
        saveMetadata(context, documentPath);
        saveThumbnail(context, documentPath);
    }

    private void saveMetadata(final Context context, final String path) {
        ReaderDocumentMetadata metadata = new ReaderDocumentMetadataImpl();
        if (getDocument().readMetadata(metadata)) {
            documentMetadata = metadata;
            LegacySdkDataUtils.saveMetadata(context, path, metadata);
        }
    }

    private void saveThumbnail(final Context context, final String path) {
        if (LegacySdkDataUtils.hasThumbnail(context, path)) {
            return;
        }
        WindowManager window = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        if (window == null) {
            Log.w(TAG, "getById display metrics failed: " + documentPath);
            return;
        }
        DisplayMetrics display = new DisplayMetrics();
        window.getDefaultDisplay().getMetrics(display);
        ReaderBitmapReferenceImpl bitmap = ReaderBitmapReferenceImpl.create(display.widthPixels, display.heightPixels,
                ReaderBitmapReferenceImpl.DEFAULT_CONFIG);
        try {
            bitmap.eraseColor(Color.WHITE);
            if (getDocument().readCover(bitmap.getBitmap())) {
                LegacySdkDataUtils.saveThumbnail(context, path, bitmap.getBitmap());
            }
        } finally {
            bitmap.close();
        }
    }

    public void onViewSizeChanged() throws ReaderException {
        updateView();
        getReaderLayoutManager().updateViewportSize();
        getImageReflowManager().updateViewportSize(viewOptions.getViewWidth(), viewOptions.getViewHeight());
    }

    private void updateView() {
        view = document.getView(viewOptions);
        renderer = view.getRenderer();
        navigator = view.getNavigator();
        rendererFeatures = renderer.getRendererFeatures();
        textStyleManager = view.getTextStyleManager();
        hitTestManager = view.getReaderHitTestManager();
        searchManager = view.getSearchManager();
    }

    public void onDocumentClosed() {
        documentPath = null;
        document = null;
        view = null;
        renderer = null;
        navigator = null;
        searchManager = null;
        hitTestManager = null;

        clearBitmapCache();
        clearImageReflowManager();
        releaseWordAnalyzer();
    }

    public void updateViewportSize(int newWidth, int newHeight) throws ReaderException {
        getViewOptions().setSize(newWidth, newHeight);
        onViewSizeChanged();
    }

    public void onLayoutChanged() {
        setLayoutChanged(true);
    }

    public void onPositionChanged(final String oldPosition, final String newPosition) {
    }

    public void beforeDraw(ReaderBitmapReferenceImpl bitmap) {
    }

    public void afterDraw(ReaderBitmapReferenceImpl bitmap) {
    }

    public final ReaderBitmapReferenceImpl getViewportBitmap() {
        return viewportBitmap;
    }

    public ReaderPlugin getPlugin() {
        return plugin;
    }

    public void setAbortFlag() {
        getPlugin().abortCurrentJob();
    }

    public void clearAbortFlag() {
        getPlugin().clearAbortFlag();
    }

    public boolean isReaderLayoutManagerCreated() {
        return (readerLayoutManager != null);
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

    public ReaderHitTestManager getHitTestManager() {
        return hitTestManager;
    }

    public ImageReflowManager getImageReflowManager() {
        return imageReflowManager;
    }

    public void transferRenderBitmapToViewport(ReaderBitmapReferenceImpl renderBitmap) {
        if (viewportBitmap != null && viewportBitmap.isValid()) {
            returnBitmapToCache(viewportBitmap);
        }
        viewportBitmap = renderBitmap;
    }

    public void returnBitmapToCache(ReaderBitmapReferenceImpl bitmap) {
        if (bitmapCache != null) {
            bitmapCache.put(bitmap.getKey(), bitmap);
        }
    }

    public BitmapReferenceLruCache getBitmapCache() {
        return bitmapCache;
    }

    public void setLayoutChanged(boolean layoutChanged) {
        this.layoutChanged = layoutChanged;
    }

    public boolean isLayoutChanged() {
        return layoutChanged;
    }

    public void initData(Context context) {
        initImageReflowManager(context);
        initBitmapCache();
        initWordAnalyzerInBackground(context);
        if(AlReaderPlugin.isJEB(documentPath)){
            bookName = JEBFilesZIP.bookName;
        }
    }

    private void initLayoutManager() {
        updateView();
        getReaderLayoutManager().init();
    }

    private void initWordAnalyzerInBackground(final Context context) {
        AnalyzerAndroidWrapper.initialize(context.getApplicationContext(), true);
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
        AnalyzerAndroidWrapper.release();
    }

    public ReaderPluginOptions getPluginOptions() {
        return pluginOptions;
    }

    public ReaderViewOptionsImpl getViewOptions() {
        return viewOptions;
    }

    public BaseOptions getDocumentOptions() {
        return documentOptions;
    }

    public ReaderDocument getDocument() {
        return document;
    }

    public ReaderView getView() {
        return view;
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

    public void applyPostBitmapProcess(ReaderBitmapReferenceImpl bitmap) {
        applyGammaCorrection(bitmap);
        applyEmbolden(bitmap);
        applySaturation(bitmap);
    }

    private void applyGammaCorrection(final ReaderBitmapReferenceImpl bitmap) {
        if (getDocumentOptions().isGamaCorrectionEnabled() &&
                Float.compare(bitmap.gammaCorrection(), getDocumentOptions().getGammaLevel()) != 0) {
            if (ImageUtils.applyGammaCorrection(bitmap.getBitmap(), getDocumentOptions().getGammaLevel())) {
                bitmap.setGammaCorrection(getDocumentOptions().getGammaLevel());
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

    public final String getDocumentPath() {
        return documentPath;
    }

    public final String getBookName(){
        return bookName;
    }

    public void setBookName(String bookName){
        this.bookName = bookName;
    }

    public final String getDocumentMd5() {
        return documentMd5;
    }

    public ReaderDocumentMetadata getDocumentMetadata() {
        return documentMetadata;
    }

    /**
     * collect all options from reader components to BaseOptions.
     */
    public void saveOptions() {
        if (!isReaderLayoutManagerCreated()) {
            return;
        }
        try {
            final ReaderLayoutManager layoutManager = getReaderLayoutManager();
            getDocumentOptions().setLayoutType(layoutManager.getCurrentLayoutType());
            getDocumentOptions().setSpecialScale(layoutManager.getSpecialScale());
            getDocumentOptions().setActualScale(layoutManager.getActualScale());
            getDocumentOptions().setCurrentPage(layoutManager.getCurrentPagePosition());
            getDocumentOptions().setTotalPage(getNavigator().getTotalPage());
            getDocumentOptions().setViewport(layoutManager.getViewportRect());
            getDocumentOptions().setNavigationArgs(layoutManager.getCurrentLayoutProvider().getNavigationArgs());
            getDocumentOptions().setReflowOptions(getImageReflowManager().getSettings().jsonString());

            final ReaderTextStyle style = layoutManager.getTextStyleManager().getStyle();
            saveReaderTextStyle(style);
        } catch (Exception e) {

        }
    }

    private void saveReaderTextStyle(final ReaderTextStyle style) {
        if (style == null) {
            return;
        }
        getDocumentOptions().setFontFace(style.getFontFace());
        getDocumentOptions().setFontSize(style.getFontSize().getValue());
        getDocumentOptions().setFontFace(style.getFontFace());
        getDocumentOptions().setLineSpacing(style.getLineSpacing().getPercent());
        getDocumentOptions().setLeftMargin(style.getPageMargin().getLeftMargin().getPercent());
        getDocumentOptions().setTopMargin(style.getPageMargin().getTopMargin().getPercent());
        getDocumentOptions().setRightMargin(style.getPageMargin().getRightMargin().getPercent());
        getDocumentOptions().setBottomMargin(style.getPageMargin().getBottomMargin().getPercent());
    }
}
