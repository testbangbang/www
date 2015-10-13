package com.onyx.reader.host.layout;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/7/15.
 * 1. All in host coordinates system including page and viewportInPage
 * 2. when rendering, calculate the viewportInPage in host coordinates system and ask plugin to render
 *    visible pages and visible part in viewportInPage and render the visible rectangles.
 * 3. hitTest, convert pointInView to pointInHost and send the point to plugin for test.
 */
public class ReaderLayoutManager {
    private Reader reader;

    public ReaderLayoutManager(final Reader r) {
        reader = r;
    }

    public Reader getReader() {
        return reader;
    }

    public boolean isScaleToPage() {
        return false;
    }

    public boolean isScaleToWidth() {
        return false;
    }

    public boolean isScaleToHeight() {
        return false;
    }

    public boolean isPageCrop() {
        return false;
    }

    public boolean isWidthCrop() {
        return false;
    }

    public float getActualScale() {
        return 0;
    }

    public RectF getHostRect() {
        return null;
    }

    public RectF getViewportRect() {
        return null;
    }

    public RectF getPageRect(final ReaderDocumentPosition position) {
        return null;
    }
}
