package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.api.ReaderNavigator;
import com.onyx.kreader.host.math.PageManager;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderHelper;

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

    public static final String SINGLE_PAGE = "singlePage";
    public static final String SINGLE_PAGE_NAVIGATION_LIST = "singlePageNavigationList";
    public static final String CONTINUOUS_PAGE = "continuousPage";
    public static final String REFLOW_PAGE = "reflowPage";
    public static final String SCANNED_REFLOW_PAGE = "scanReflowPage";

    private Reader reader;
    private ReaderHelper readerHelper;
    private ReaderPositionHolder positionHolder;
    private PageManager pageManager;
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

    public void init()  {
        getPageManager().setViewportRect(0, 0, reader.getViewOptions().getViewWidth(), reader.getViewOptions().getViewHeight());

        // check renderer features
        boolean supportScale = reader.getRendererFeatures().supportScale();
        boolean supportReflow = reader.getRendererFeatures().supportFontSizeAdjustment();
        if (supportScale) {
            provider.put(SINGLE_PAGE, new LayoutSinglePageProvider(this));
            provider.put(SINGLE_PAGE_NAVIGATION_LIST, new LayoutSinglePageNavigationListProvider(this));
            provider.put(CONTINUOUS_PAGE, new LayoutContinuousProvider(this));
        }
        if (supportReflow) {
            provider.put(REFLOW_PAGE, new LayoutReflowProvider(this));
        }
        if (supportScale) {
            currentProvider = SINGLE_PAGE;
        } else {
            currentProvider = REFLOW_PAGE;
        }
        getCurrentLayoutProvider().activate();
    }

    public LayoutProvider getCurrentLayoutProvider() {
        return provider.get(getCurrentLayout());
    }

    public final String getCurrentLayout() {
        return currentProvider;
    }

    public boolean setCurrentLayout(final String layoutName) throws ReaderException {
        if (!provider.containsKey(layoutName)) {
            return false;
        }
        currentProvider = layoutName;
        getCurrentLayoutProvider().activate();
        return true;
    }

    public boolean isScaleToPage() {
        return getPageManager().isScaleToPage();
    }

    public boolean isScaleToWidth() {
        return getPageManager().isScaleToWidth();
    }

    public boolean isScaleToHeight() {
        return getPageManager().isScaleToHeight();
    }

    public boolean isPageCrop() {
        return getPageManager().isPageCrop();
    }

    public boolean isWidthCrop() {
        return getPageManager().isWidthCrop();
    }

    public float getActualScale() throws ReaderException {
        return getCurrentLayoutProvider().getActualScale();
    }

    public RectF getPageBoundingRect() throws ReaderException {
        return getCurrentLayoutProvider().getPageBoundingRect();
    }

    public RectF getViewportRect() throws ReaderException {
        return getCurrentLayoutProvider().getViewportRect();
    }

    public RectF getPageRectOnViewport(final String position) throws ReaderException {
        return getCurrentLayoutProvider().getPageRectOnViewport(position);
    }

    public PageManager getPageManager() {
        if (pageManager == null) {
            pageManager = new PageManager();
        }
        return pageManager;
    }

    public ReaderPositionHolder getPositionHolder() {
        if (positionHolder == null) {
            positionHolder = new ReaderPositionHolder(this);
        }
        return positionHolder;
    }

    public void onPositionChanged() {
        // save current position into position stack
        // make sure it's different from stack top.
    }

    public String getCurrentPagePosition() {
        return getPageManager().getFirstVisiblePage().getName();
    }

    public boolean gotoPosition(final String position) throws ReaderException {
        if (getCurrentLayoutProvider().gotoPosition(position)) {
            onPositionChanged();
            return true;
        }
        return false;
    }

    public boolean drawVisiblePages(ReaderBitmap bitmap) throws ReaderException {
        return getCurrentLayoutProvider().drawVisiblePages(bitmap);
    }

    public void setScale(final float scale, final float x, final float y) throws ReaderException {
        getCurrentLayoutProvider().setScale(scale, x, y);
        onPositionChanged();
    }

    public void scaleToPage() throws ReaderException {
        getCurrentLayoutProvider().scaleToPage();
        onPositionChanged();
    }

    public void scaleToWidth() throws ReaderException {
        getCurrentLayoutProvider().scaleToWidth();
        onPositionChanged();
    }

    public void scaleByRect(final RectF child) throws ReaderException {
        getCurrentLayoutProvider().scaleByRect(child);
        onPositionChanged();
    }

    public boolean nextScreen() throws ReaderException {
        if (getCurrentLayoutProvider().nextScreen()) {
            onPositionChanged();
            return true;
        }
        return false;
    }

    public boolean prevScreen() throws ReaderException {
        if (getCurrentLayoutProvider().prevScreen()) {
            onPositionChanged();
            return true;
        }
        return false;
    }

    public void setNavigationArgs(final NavigationArgs args) throws ReaderException {
        getCurrentLayoutProvider().setNavigationArgs(args);
    }

}
