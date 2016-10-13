package com.onyx.kreader.host.wrapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderDocument;
import com.onyx.kreader.api.ReaderDocumentMetadata;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.api.ReaderHitTestManager;
import com.onyx.kreader.api.ReaderNavigator;
import com.onyx.kreader.api.ReaderPlugin;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.api.ReaderRenderer;
import com.onyx.kreader.api.ReaderRendererFeatures;
import com.onyx.kreader.api.ReaderSearchManager;
import com.onyx.kreader.api.ReaderView;
import com.onyx.kreader.cache.BitmapSoftLruCache;
import com.onyx.kreader.cache.ReaderBitmapImpl;
import com.onyx.kreader.dataprovider.LegacySdkDataUtils;
import com.onyx.kreader.host.impl.ReaderDocumentMetadataImpl;
import com.onyx.kreader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.kreader.host.impl.ReaderViewOptionsImpl;
import com.onyx.kreader.host.layout.ReaderLayoutManager;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.plugins.comic.ComicReaderPlugin;
import com.onyx.kreader.plugins.djvu.DjvuReaderPlugin;
import com.onyx.kreader.plugins.images.ImagesReaderPlugin;
import com.onyx.kreader.plugins.neopdf.NeoPdfReaderPlugin;
import com.onyx.kreader.reflow.ImageReflowManager;
import com.onyx.kreader.utils.ImageUtils;
import org.apache.lucene.analysis.cn.AnalyzerAndroidWrapper;

import java.io.File;

/**
 * Created by zhuzeng on 10/5/15.
 * Save all helper data objects in this class.
 */
public class ReaderHelper {
    private static final String TAG = ReaderHelper.class.getSimpleName();

    private String documentPath;
    private String documentMd5;
    private ReaderDocumentMetadata documentMetadata;
    private ReaderViewOptionsImpl viewOptions = new ReaderViewOptionsImpl();
    private ReaderPluginOptionsImpl pluginOptions;
    private BaseOptions documentOptions = new BaseOptions();

    private ReaderPlugin plugin;
    private ReaderDocument document;
    private ReaderView view;
    private ReaderNavigator navigator;
    private ReaderRenderer renderer;
    private ReaderRendererFeatures rendererFeatures;
    private ReaderSearchManager searchManager;
    // to be used by UI thread
    private ReaderBitmapImpl viewportBitmap;
    private ReaderLayoutManager readerLayoutManager;
    private ReaderHitTestManager hitTestManager;
    private ImageReflowManager imageReflowManager;
    private BitmapSoftLruCache bitmapCache;

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
        }
        return (plugin != null);
    }

    public void onDocumentOpened(final Context context,
                                 final String path,
                                 final ReaderDocument doc,
                                 final BaseOptions options) throws Exception {
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
        WindowManager window = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        if (window == null) {
            Log.w(TAG, "getById display metrics failed: " + documentPath);
            return;
        }
        DisplayMetrics display = new DisplayMetrics();
        window.getDefaultDisplay().getMetrics(display);
        Bitmap bitmap = Bitmap.createBitmap(display.widthPixels, display.heightPixels,
                Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.WHITE);
        if (getDocument().readCover(bitmap)) {
            LegacySdkDataUtils.saveThumbnail(context, path, bitmap);
        }
        bitmap.recycle();
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

    }

    public void onPositionChanged(final String oldPosition, final String newPosition) {

    }

    public void beforeDraw(ReaderBitmapImpl bitmap) {
    }

    public void afterDraw(ReaderBitmapImpl bitmap) {
    }

    public final ReaderBitmapImpl getViewportBitmap() {
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

    public void transferRenderBitmapToViewport(ReaderBitmapImpl renderBitmap) {
        if (viewportBitmap != null) {
            returnBitmapToCache(viewportBitmap);
        }
        viewportBitmap = renderBitmap;
    }

    public void returnBitmapToCache(ReaderBitmapImpl bitmap) {
        if (bitmapCache != null) {
            bitmapCache.put(bitmap.getKey(), bitmap);
        }
    }

    public BitmapSoftLruCache getBitmapCache() {
        return bitmapCache;
    }

    public void initData(Context context) {
        initImageReflowManager(context);
        initBitmapCache();
        initWordAnalyzerInBackground(context);
    }

    private void initLayoutManager() {
        updateView();
        getReaderLayoutManager().init();
    }

    private void initWordAnalyzerInBackground(Context context) {
        AnalyzerAndroidWrapper.initialize(context, true);
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
            bitmapCache = new BitmapSoftLruCache(5);
        }
    }

    private void clearBitmapCache() {
        if (bitmapCache != null) {
            bitmapCache.clear();
            bitmapCache = null;
        }
    }

    private void releaseWordAnalyzer() {
        AnalyzerAndroidWrapper.release();
    }

    public ReaderPluginOptionsImpl getPluginOptions() {
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

    public ReaderSearchManager getSearchManager() {
        return searchManager;
    }

    public void applyPostBitmapProcess(ReaderBitmapImpl bitmap) {
        if (getDocumentOptions().isGamaCorrectionEnabled() &&
                Float.compare(bitmap.gammaCorrection(), getDocumentOptions().getGammaLevel()) != 0) {
            if (ImageUtils.applyGammaCorrection(bitmap.getBitmap(), getDocumentOptions().getGammaLevel())) {
                bitmap.setGammaCorrection(getDocumentOptions().getGammaLevel());
            }
        }
        if (getDocumentOptions().isEmboldenLevelEnabled()) {
            ImageUtils.applyBitmapEmbolden(bitmap.getBitmap(), getDocumentOptions().getEmboldenLevel());
        }
    }

    public final String getDocumentPath() {
        return documentPath;
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
            getDocumentOptions().setLayoutType(getReaderLayoutManager().getCurrentLayoutType());
            getDocumentOptions().setSpecialScale(getReaderLayoutManager().getSpecialScale());
            getDocumentOptions().setActualScale(getReaderLayoutManager().getActualScale());
            getDocumentOptions().setCurrentPage(getReaderLayoutManager().getCurrentPageName());
            getDocumentOptions().setTotalPage(getNavigator().getTotalPage());
            getDocumentOptions().setViewport(getReaderLayoutManager().getViewportRect());
            getDocumentOptions().setNavigationArgs(getReaderLayoutManager().getCurrentLayoutProvider().getNavigationArgs());
            getDocumentOptions().setReflowOptions(getImageReflowManager().getSettings().jsonString());
        } catch (Exception e) {

        }
    }
}
