package com.onyx.phone.reader.reader;

import android.content.Context;

import com.onyx.phone.reader.reader.data.ReaderDataHolder;
import com.onyx.phone.reader.reader.opengl.CurlView;

/**
 * Created by ming on 2017/5/5.
 */

public class ReaderRender {

    public static void renderPage(final Context context, final ReaderDataHolder readerDataHolder, final CurlView curlView) {
        drawHighLight(readerDataHolder, curlView);
    }

    private static void drawHighLight(final ReaderDataHolder readerDataHolder, final CurlView curlView) {
        if (readerDataHolder.getSelectionManager().hasSelection()) {
            curlView.updateIOpenGLObjects(readerDataHolder.getSelectionManager().getSelectionRectangles());
        }
    }
}
