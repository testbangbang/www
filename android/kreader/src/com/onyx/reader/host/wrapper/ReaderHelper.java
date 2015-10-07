package com.onyx.reader.host.wrapper;

import android.content.Context;
import android.graphics.Bitmap;
import com.onyx.reader.api.*;
import com.onyx.reader.host.impl.ReaderBitmapImpl;
import com.onyx.reader.plugins.adobe.AdobeReaderPlugin;

/**
 * Created by zhuzeng on 10/5/15.
 * Save all helper data objects in this class.
 */
public class ReaderHelper {

    public ReaderViewOptions viewOptions;
    public ReaderPluginOptions pluginOptions;
    public ReaderDocumentOptions documentOptions;

    public ReaderPlugin plugin;
    public ReaderDocument document;
    public ReaderView view;
    public ReaderNavigator navigator;
    public ReaderRenderer renderer;
    public ReaderSearchManager searchManager;
    public ReaderBitmapImpl renderBitmap;



    public void loadPlugin(final Context context, final String path) {
        plugin = new AdobeReaderPlugin(context);
    }

    public void onDocumentOpened(final ReaderDocument doc) {
        document = doc;
        view = document.createView(viewOptions);
        renderer = view.getRenderer();
        navigator = view.getNavigator();
    }

    public void onDocumentClosed() {
        document = null;
        view = null;
        renderer = null;
        navigator = null;
        searchManager = null;
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

    public void renderToBitmap() {
        renderer.draw(renderBitmap);
    }

    public ReaderPlugin getCurrentPlugin() {
        return plugin;
    }

    public void setAbortFlag() {
        getCurrentPlugin().abortCurrentJob();
    }

    public void clearAbortFlag() {
        getCurrentPlugin().clearAbortFlag();
    }


}
