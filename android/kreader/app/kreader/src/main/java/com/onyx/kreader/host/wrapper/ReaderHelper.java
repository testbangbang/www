package com.onyx.kreader.host.wrapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import com.jakewharton.disklrucache.DiskLruCache;
import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderDocument;
import com.onyx.kreader.api.ReaderDocumentMetadata;
import com.onyx.kreader.api.ReaderHitTestManager;
import com.onyx.kreader.api.ReaderNavigator;
import com.onyx.kreader.api.ReaderPlugin;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.api.ReaderRenderer;
import com.onyx.kreader.api.ReaderRendererFeatures;
import com.onyx.kreader.api.ReaderSearchManager;
import com.onyx.kreader.api.ReaderView;
import com.onyx.kreader.cache.BitmapLruCache;
import com.onyx.kreader.cache.ReaderBitmapImpl;
import com.onyx.kreader.dataprovider.compatability.LegacySdkDataUtils;
import com.onyx.kreader.host.impl.ReaderDocumentMetadataImpl;
import com.onyx.kreader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.kreader.host.impl.ReaderViewOptionsImpl;
import com.onyx.kreader.host.layout.ReaderLayoutManager;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.plugins.comic.ComicReaderPlugin;
import com.onyx.kreader.plugins.djvu.DjvuReaderPlugin;
import com.onyx.kreader.plugins.images.ImagesReaderPlugin;
import com.onyx.kreader.plugins.pdfium.PdfiumReaderPlugin;
import com.onyx.kreader.reflow.ImageReflowManager;
import com.onyx.kreader.utils.ImageUtils;
import org.apache.lucene.analysis.cn.AnalyzerAndroidWrapper;

import java.io.File;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhuzeng on 10/5/15.
 * Save all helper data objects in this class.
 */
public class ReaderHelper {
    private static final String TAG = ReaderHelper.class.getSimpleName();

    public class BitmapCopyCoordinator {
        private ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        private boolean renderBitmapDirty = false;

        public void copyRenderBitmapToViewport() {
            try {
                lock.lock();
                if (!renderBitmapDirty) {
                    return;
                }
                ReaderHelper.this.copyRenderBitmapToViewportImpl();
                renderBitmapDirty = false;
                condition.signal();
            } finally {
                lock.unlock();
            }
        }

        public void waitCopy() {
            try {
                lock.lock();
                while (renderBitmapDirty) {
                    condition.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    private String documentPath;
    private String documentMd5;
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
    private ReaderBitmapImpl renderBitmap;
    // copy of renderBitmap, to be used by UI thread
    private ReaderBitmapImpl viewportBitmap = new ReaderBitmapImpl();
    private BitmapCopyCoordinator bitmapCopyCoordinator = new BitmapCopyCoordinator();
    private ReaderLayoutManager readerLayoutManager;
    private ReaderHitTestManager hitTestManager;
    private ImageReflowManager imageReflowManager;
    private BitmapLruCache bitmapLruCache;

    public ReaderHelper() {
    }

    public boolean selectPlugin(final Context context, final String path, final ReaderPluginOptions pluginOptions) {
        if (PdfiumReaderPlugin.accept(path)) {
            plugin = new PdfiumReaderPlugin(context, pluginOptions);
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
        getDocumentOptions().setZipPassword(options.getZipPassword());
        getDocumentOptions().setPassword(options.getPassword());
        saveMetadata(context, documentPath);
        saveThumbnail(context, documentPath);
    }

    private void saveMetadata(final Context context, final String path) {
        ReaderDocumentMetadata metadata = new ReaderDocumentMetadataImpl();
        if (getDocument().readMetadata(metadata)) {
            LegacySdkDataUtils.saveMetadata(context, path, metadata);
        }
    }

    private void saveThumbnail(final Context context, final String path) {
        WindowManager window = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        if (window == null) {
            Log.w(TAG, "get display metrics failed: " + documentPath);
            return;
        }
        DisplayMetrics display = new DisplayMetrics();
        window.getDefaultDisplay().getMetrics(display);
        ReaderBitmapImpl bitmap = ReaderBitmapImpl.create(display.widthPixels, display.heightPixels,
                Bitmap.Config.ARGB_8888);
        bitmap.clear();
        if (getDocument().readCover(bitmap)) {
            LegacySdkDataUtils.saveThumbnail(context, path, bitmap.getBitmap());
        }
        bitmap.recycleBitmap();
    }

    public void onViewSizeChanged() {
        view = document.getView(viewOptions);
        renderer = view.getRenderer();
        navigator = view.getNavigator();
        rendererFeatures = renderer.getRendererFeatures();
        hitTestManager = view.getReaderHitTestManager();
        searchManager = view.getSearchManager();
        getReaderLayoutManager().init();
        getReaderLayoutManager().updateViewportSize();
    }

    public void onDocumentClosed() {
        documentPath = null;
        document = null;
        view = null;
        renderer = null;
        navigator = null;
        searchManager = null;
        hitTestManager = null;
        if (bitmapLruCache != null) {
            FileUtils.closeQuietly(bitmapLruCache);
        }
    }

    public void updateViewportSize(int newWidth, int newHeight) {
        getViewOptions().setSize(newWidth, newHeight);
        updateRenderBitmap(newWidth, newHeight);
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

    public void updateRenderBitmap(int width, int height) {
        if (renderBitmap != null) {
            renderBitmap.recycleBitmap();
        }
        // delay the init of renderBitmap until we really need it
        renderBitmap = new ReaderBitmapImpl();
    }

    public final ReaderBitmapImpl getRenderBitmap() {
        updateRenderBitmap(viewOptions.getViewWidth(), viewOptions.getViewHeight());
        return renderBitmap;
    }

    public boolean isRenderBitmapDirty() {
        return bitmapCopyCoordinator.renderBitmapDirty;
    }

    public void setRenderBitmapDirty(boolean dirty) {
        bitmapCopyCoordinator.renderBitmapDirty = dirty;
    }

    public final ReaderBitmapImpl getViewportBitmap() {
        return viewportBitmap;
    }

    public BitmapCopyCoordinator getBitmapCopyCoordinator() {
        return bitmapCopyCoordinator;
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

    public BitmapLruCache getBitmapLruCache() {
        return bitmapLruCache;
    }

    public void initData(Context context) {
        initImageReflowManager(context);
        initBitmapLruCache(context);
        //initChineseAnalyzer(context);
    }

    private void initChineseAnalyzer(Context context) {
        AnalyzerAndroidWrapper.init(context);
    }

    private void initImageReflowManager(Context context) {
        if (imageReflowManager == null) {
            File cacheLocation = new File(context.getCacheDir(), ImageReflowManager.class.getCanonicalName());
            if (!cacheLocation.exists()) {
                cacheLocation.mkdirs();
            }
            imageReflowManager = new ImageReflowManager(cacheLocation,
                    getViewOptions().getViewWidth(),
                    getViewOptions().getViewHeight());
        }
    }

    private void initBitmapLruCache(Context context) {
        if (bitmapLruCache == null) {
            File cacheLocation = new File(context.getCacheDir(), DiskLruCache.class.getCanonicalName());
            if (!cacheLocation.exists()) {
                cacheLocation.mkdirs();
            }
            BitmapLruCache.Builder builder = new BitmapLruCache.Builder();
            builder.setMemoryCacheEnabled(true).setMemoryCacheMaxSizeUsingHeapSize();
            builder.setDiskCacheEnabled(false).setDiskCacheLocation(cacheLocation);
            bitmapLruCache = builder.build();
        }
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

    public void applyPostBitmapProcess(ReaderBitmap bitmap) {
        if (getDocumentOptions().isGamaCorrectionEnabled()) {
            ImageUtils.applyGammaCorrection(bitmap.getBitmap(), getDocumentOptions().getGammaLevel());
        }
        if (getDocumentOptions().isEmboldenLevelEnabled()) {
            ImageUtils.applyBitmapEmbolden(bitmap.getBitmap(), getDocumentOptions().getEmboldenLevel());
        }
    }

    private void copyRenderBitmapToViewportImpl() {
        if (renderBitmap != null && renderBitmap.getBitmap() != null &&
                !renderBitmap.getBitmap().isRecycled()) {
            viewportBitmap.attachWith(renderBitmap.getBitmapReference());
        }
   }

    public final String getDocumentPath() {
        return documentPath;
    }

    public final String getDocumentMd5() {
        return documentMd5;
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
        } catch (Exception e) {

        }
    }
}
