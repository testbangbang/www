package com.onyx.reader.host.layout;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderBitmap;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderException;
import com.onyx.reader.host.math.EntryManager;
import com.onyx.reader.host.wrapper.Reader;
import com.onyx.reader.host.wrapper.ReaderHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhuzeng on 10/7/15.
 * 1. All in host coordinates system including page and viewportInPage
 * 2. when rendering, calculate the viewportInPage in host coordinates system and ask plugin to render
 *    visible pages and visible part in viewportInPage and render the visible rectangles.
 * 3. hitTest, convert pointInView to pointInHost and send the point to plugin for test.
 *
 * layout manager --> layout provider --> Plugin & EntryManager
 */
public class ReaderLayoutManager {

    public static final String SINGLE_PAGE = "singlePage";
    public static final String REFLOW_PAGE = "reflowPage";
    public static final String CONTINUOUS_PAGE = "continuousPage";
    public static final String SCANNED_PAGE_REFLOW = "scanPageReflow";

    private Reader reader;
    private ReaderHelper readerHelper;
    private EntryManager entryManager;
    private String currentProvider;
    private Map<String, LayoutProvider> provider = new ConcurrentHashMap<String, LayoutProvider>();


    public ReaderLayoutManager(final Reader r) {
        reader = r;
        readerHelper = reader.getReaderHelper();
        provider.put(SINGLE_PAGE, new LayoutSinglePageProvider(this));
        provider.put(REFLOW_PAGE, new LayoutReflowProvider(this));
        provider.put(CONTINUOUS_PAGE, new LayoutContinuousPageProvider(this));
        currentProvider = SINGLE_PAGE;
    }

    public Reader getReader() {
        return reader;
    }

    public ReaderHelper getReaderHelper() {
        return readerHelper;
    }

    public void init() {
        getEntryManager().setViewportRect(0, 0, reader.getViewOptions().getViewWidth(), reader.getViewOptions().getViewHeight());
    }

    public LayoutProvider getCurrentLayoutProvider() {
        return provider.get(currentProvider);
    }

    public final String getCurrentLayout() {
        return currentProvider;
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

    public float getActualScale() throws ReaderException {
        return getCurrentLayoutProvider().getActualScale();
    }

    public RectF getHostRect() throws ReaderException {
        return getCurrentLayoutProvider().getHostRect();
    }

    public RectF getViewportRect() throws ReaderException {
        return getCurrentLayoutProvider().getViewportRect();
    }

    public RectF getPageRect(final ReaderDocumentPosition position) throws ReaderException {
        return getCurrentLayoutProvider().getPageRect(position);
    }

    public EntryManager getEntryManager() {
        if (entryManager == null) {
            entryManager = new EntryManager();
        }
        return entryManager;
    }

    public boolean gotoPosition(final ReaderDocumentPosition position) throws ReaderException {
        return getCurrentLayoutProvider().gotoPosition(position);
    }

    public boolean drawVisiblePages(ReaderBitmap bitmap) throws ReaderException {
        return getCurrentLayoutProvider().drawVisiblePages(bitmap);
    }

    public void setScale(final float scale, final float x, final float y) throws ReaderException {
        getCurrentLayoutProvider().setScale(scale, x, y);
    }
}
