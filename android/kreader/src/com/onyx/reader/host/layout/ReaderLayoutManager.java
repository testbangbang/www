package com.onyx.reader.host.layout;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderBitmap;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderException;
import com.onyx.reader.api.ReaderNavigator;
import com.onyx.reader.host.math.EntryManager;
import com.onyx.reader.host.navigation.NavigationArgs;
import com.onyx.reader.host.wrapper.Reader;
import com.onyx.reader.host.wrapper.ReaderHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 10/7/15.
 * 1. All in host coordinates system including page and viewportInPage
 * 2. when rendering, calculate the viewportInPage in host coordinates system and ask plugin to render
 *    visible pages and visible part in viewportInPage and render the visible rectangles.
 * 3. hitTest, convert pointInView to pointInHost and send the point to plugin for test.
 *
 * layout manager --> layout provider --> Navigation Manager -> Navigation provider -> EntryManager & plugin
 * forward everything to layout provider impl.
 */
public class ReaderLayoutManager {

    public static final String SINGLE_HARD_PAGE = "singleHardPage";
    public static final String SINGLE_NAVIGATION_LIST_PAGE = "singleNavigationListPage";
    public static final String CONTINUOUS_PAGE = "continuousPage";
    public static final String REFLOW_PAGE = "reflowPage";
    public static final String SCANNED_REFLOW_PAGE = "scanReflowPage";

    private Reader reader;
    private ReaderHelper readerHelper;
    private ReaderPositionHolder positionHolder;
    private EntryManager entryManager;
    private String currentProvider;
    private Map<String, LayoutProvider> provider = new HashMap<String, LayoutProvider>();


    public ReaderLayoutManager(final Reader r) {
        reader = r;
        readerHelper = reader.getReaderHelper();
    }

    public Reader getReader() {
        return reader;
    }

    public ReaderNavigator getNavigator() {
        return getReader().getNavigator();
    }

    public ReaderHelper getReaderHelper() {
        return readerHelper;
    }

    public void init() {
        getEntryManager().setViewportRect(0, 0, reader.getViewOptions().getViewWidth(), reader.getViewOptions().getViewHeight());
        getPositionHolder().updatePosition(getReaderHelper().getNavigator().getInitPosition());

        // check renderer features
        boolean supportScale = reader.getRendererFeatures().supportScale();
        boolean supportReflow = reader.getRendererFeatures().supportFontSizeAdjustment();
        if (supportScale) {
            provider.put(SINGLE_HARD_PAGE, new LayoutSingleHardPageProvider(this));
            provider.put(SINGLE_NAVIGATION_LIST_PAGE, new LayoutSingleNavigationListProvider(this));
            provider.put(CONTINUOUS_PAGE, new LayoutContinuousProvider(this));
        }
        if (supportReflow) {
            provider.put(REFLOW_PAGE, new LayoutReflowProvider(this));
        }
        if (supportScale) {
            currentProvider = SINGLE_HARD_PAGE;
        } else {
            currentProvider = REFLOW_PAGE;
        }
    }

    public LayoutProvider getCurrentLayoutProvider() {
        return provider.get(currentProvider);
    }

    public final String getCurrentLayout() {
        return currentProvider;
    }

    public boolean setCurrentLayout(final String layoutName) throws ReaderException {
        if (!provider.containsKey(layoutName)) {
            return false;
        }
        currentProvider = layoutName;
        getCurrentLayoutProvider().activate(this);
        return true;
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

    public ReaderPositionHolder getPositionHolder() {
        if (positionHolder == null) {
            positionHolder = new ReaderPositionHolder(this);
        }
        return positionHolder;
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

    public void scaleToPage() throws ReaderException {
        getCurrentLayoutProvider().scaleToPage();
    }

    public void scaleToWidth() throws ReaderException {
        getCurrentLayoutProvider().scaleToWidth();
    }

    public void scaleByRect(final RectF child) throws ReaderException {
        getCurrentLayoutProvider().scaleByRect(child);
    }

    public boolean nextScreen() throws ReaderException {
        return getCurrentLayoutProvider().nextScreen();
    }

    public boolean prevScreen() throws ReaderException {
        return getCurrentLayoutProvider().prevScreen();
    }

    public void setNavigationArgs(final NavigationArgs args) throws ReaderException {
        getCurrentLayoutProvider().setNavigationArgs(args);
    }

}
