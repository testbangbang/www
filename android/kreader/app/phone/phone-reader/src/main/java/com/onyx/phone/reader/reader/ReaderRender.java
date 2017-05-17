package com.onyx.phone.reader.reader;

import android.content.Context;

import com.onyx.phone.reader.reader.data.ReaderDataHolder;
import com.onyx.phone.reader.reader.opengl.PageRenderView;

/**
 * Created by ming on 2017/5/5.
 */

public class ReaderRender {

    public static void renderPage(final Context context, final ReaderDataHolder readerDataHolder, final PageRenderView renderView) {
        drawHighLight(readerDataHolder, renderView);
    }

    private static void drawHighLight(final ReaderDataHolder readerDataHolder, final PageRenderView renderView) {
        renderView.updateShapeObjects(readerDataHolder.getSelectionManager().getSelectionRectangles());
    }
}
