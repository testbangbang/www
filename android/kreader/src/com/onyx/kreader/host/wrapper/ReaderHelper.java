package com.onyx.kreader.host.wrapper;

import android.content.Context;
import android.graphics.Bitmap;
import com.onyx.kreader.api.*;
import com.onyx.kreader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.kreader.host.impl.ReaderViewOptionsImpl;
import com.onyx.kreader.host.layout.ReaderLayoutManager;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.plugins.adobe.AdobeReaderPlugin;
import com.onyx.kreader.plugins.images.ImagesJniWrapper;
import com.onyx.kreader.plugins.images.ImagesReaderPlugin;
import com.onyx.kreader.plugins.pdfium.PdfiumReaderPlugin;

/**
 * Created by zhuzeng on 10/5/15.
 * Save all helper data objects in this class.
 */
public class ReaderHelper {
    public Reader reader;
    private ReaderViewOptionsImpl viewOptions = new ReaderViewOptionsImpl();
    private ReaderPluginOptionsImpl pluginOptions;
    private ReaderDocumentOptions documentOptions;

    private ReaderPlugin plugin;
    private ReaderDocument document;
    private ReaderView view;
    private ReaderNavigator navigator;
    private ReaderRenderer renderer;
    private ReaderRendererFeatures rendererFeatures;
    private ReaderSearchManager searchManager;
    private ReaderBitmapImpl renderBitmap;
    private ReaderLayoutManager readerLayoutManager;
    private ReaderHitTestManager hitTestManager;
    private ReaderCacheManager readerCacheManager = new ReaderCacheManager();

    public ReaderHelper(final Reader r) {
        reader = r;
    }

    public boolean selectPlugin(final Context context, final String path, final ReaderPluginOptions pluginOptions) {
        if (PdfiumReaderPlugin.accept(path)) {
            plugin = new PdfiumReaderPlugin(context, pluginOptions);
        } else if (AdobeReaderPlugin.accept(path)) {
            plugin = new AdobeReaderPlugin(context, pluginOptions);
        } else if (ImagesReaderPlugin.accept(path)) {
            plugin = new ImagesReaderPlugin(context, pluginOptions);
        }
        return (plugin != null);
    }

    public void onDocumentOpened(final ReaderDocument doc) {
        document = doc;
    }

    public void onViewCreated() {
        view = document.getView(viewOptions);
        renderer = view.getRenderer();
        navigator = view.getNavigator();
        rendererFeatures = renderer.getRendererFeatures();
        hitTestManager = view.getReaderHitTestManager();
        getReaderLayoutManager().init();
        getReaderCacheManager().clear();
    }

    public void onDocumentClosed() {
        document = null;
        view = null;
        renderer = null;
        navigator = null;
        searchManager = null;
        hitTestManager = null;
        getReaderCacheManager().clear();
    }

    public void updateViewportSize(int newWidth, int newHeight) {
        reader.getReaderHelper().getViewOptions().setSize(newWidth, newHeight);
        reader.getReaderHelper().updateRenderBitmap(newWidth, newHeight);
        reader.getReaderLayoutManager().updateViewportSize();
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
        Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
        if (renderBitmap == null) {
            renderBitmap = ReaderBitmapImpl.create(width, height, bitmapConfig);
        } else {
            renderBitmap.update(width, height, bitmapConfig);
        }
    }

    public final ReaderBitmapImpl getRenderBitmap() {
        updateRenderBitmap(viewOptions.getViewWidth(), viewOptions.getViewHeight());
        return renderBitmap;
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

    public ReaderLayoutManager getReaderLayoutManager() {
        if (readerLayoutManager == null) {
            readerLayoutManager = new ReaderLayoutManager(reader.getDocument(),
                    reader.getNavigator(),
                    reader.getRendererFeatures(),
                    reader.getViewOptions());
        }
        return readerLayoutManager;
    }

    public ReaderCacheManager getReaderCacheManager() {
        return readerCacheManager;
    }

    public ReaderHitTestManager getHitTestManager() {
        return hitTestManager;
    }

    public ReaderPluginOptionsImpl getPluginOptions() {
        return pluginOptions;
    }

    public ReaderViewOptionsImpl getViewOptions() {
        return viewOptions;
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



}
