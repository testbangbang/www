package com.onyx.reader.host.wrapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.reader.api.*;
import com.onyx.reader.host.impl.ReaderBitmapImpl;
import com.onyx.reader.host.layout.ReaderLayoutManager;
import com.onyx.reader.host.math.EntryInfo;
import com.onyx.reader.host.math.EntryManager;
import com.onyx.reader.plugins.adobe.AdobeReaderPlugin;

/**
 * Created by zhuzeng on 10/5/15.
 * Save all helper data objects in this class.
 */
public class ReaderHelper {
    public Reader reader;
    private ReaderViewOptions viewOptions;
    private ReaderPluginOptions pluginOptions;
    private ReaderDocumentOptions documentOptions;

    private ReaderPlugin plugin;
    private ReaderDocument document;
    private ReaderView view;
    private ReaderNavigator navigator;
    private ReaderRenderer renderer;
    private ReaderSearchManager searchManager;
    private ReaderBitmapImpl renderBitmap;
    private ReaderLayoutManager readerLayoutManager;
    private ReaderHitTestManager hitTestManager;

    public ReaderHelper(final Reader r) {
        reader = r;
    }

    public void loadPlugin(final Context context, final String path) {
        plugin = new AdobeReaderPlugin(context);
    }

    public void onDocumentOpened(final ReaderDocument doc) {
        document = doc;
        view = document.createView(viewOptions);
        renderer = view.getRenderer();
        navigator = view.getNavigator();
        hitTestManager = view.getReaderHitTestManager();
        getReaderLayoutManager().init();
    }

    public void onDocumentClosed() {
        document = null;
        view = null;
        renderer = null;
        navigator = null;
        searchManager = null;
        hitTestManager = null;
    }

    public void onLayoutChanged() {

    }

    public void updateRenderBitmap(int width, int height) {
        Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
        if (renderBitmap == null) {
            renderBitmap = new ReaderBitmapImpl(width, height, bitmapConfig);
        } else {
            renderBitmap.update(width, height, bitmapConfig);
        }
    }

    public final ReaderBitmap getRenderBitmap() {
        updateRenderBitmap(viewOptions.getViewWidth(), viewOptions.getViewHeight());
        return renderBitmap;
    }

    public void renderToBitmap() {
        renderer.draw(renderBitmap);
    }

    public void renderToBitmap(int left, int top, int right, int bottom) {
        renderer.draw(renderBitmap, left, top, right, bottom);
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
            readerLayoutManager = new ReaderLayoutManager(reader);
        }
        return readerLayoutManager;
    }

    public ReaderHitTestManager getHitTestManager() {
        return hitTestManager;
    }

    public void setPluginOptions(final ReaderPluginOptions options) {
        pluginOptions = options;
    }

    public ReaderPluginOptions getPluginOptions() {
        return pluginOptions;
    }

    public void setViewOptions(final ReaderViewOptions options) {
        viewOptions = options;
    }

    public ReaderViewOptions getViewOptions() {
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

    public ReaderSearchManager getSearchManager() {
        return searchManager;
    }



}
