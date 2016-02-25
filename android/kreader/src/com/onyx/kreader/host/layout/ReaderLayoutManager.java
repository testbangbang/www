package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.*;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PageManager;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.StringUtils;
import com.onyx.kreader.utils.HistoryManager;

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

    private ReaderDocument readerDocument;
    private ReaderNavigator readerNavigator;
    private ReaderRendererFeatures readerRendererFeatures;
    private ReaderViewOptions readerViewOptions;
    private HistoryManager historyManager;
    private PageManager pageManager;
    private String currentProvider;
    private Map<String, LayoutProvider> provider = new HashMap<String, LayoutProvider>();
    private boolean supportScale;
    private boolean supportReflow;

    public ReaderLayoutManager(final ReaderDocument document,
                               final ReaderNavigator navigator,
                               final ReaderRendererFeatures features,
                               final ReaderViewOptions viewOptions) {
        readerDocument = document;
        readerNavigator = navigator;
        readerRendererFeatures = features;
        readerViewOptions = viewOptions;
    }

    public ReaderNavigator getNavigator() {
        return readerNavigator;
    }

    public ReaderDocument getReaderDocument() {
        return readerDocument;
    }

    public ReaderRendererFeatures getReaderRendererFeatures() {
        return readerRendererFeatures;
    }

    public ReaderViewOptions getReaderViewOptions() {
        return readerViewOptions;
    }

    public void init()  {
        supportScale = getReaderRendererFeatures().supportScale();
        supportReflow = getReaderRendererFeatures().supportFontSizeAdjustment();
        if (supportScale) {
            provider.put(ReaderConstants.SINGLE_PAGE, new LayoutSinglePageProvider(this));
            provider.put(ReaderConstants.SINGLE_PAGE_NAVIGATION_LIST, new LayoutSinglePageNavigationListProvider(this));
            provider.put(ReaderConstants.CONTINUOUS_PAGE, new LayoutContinuousProvider(this));
        }
        if (supportReflow) {
            provider.put(ReaderConstants.REFLOW_PAGE, new LayoutReflowProvider(this));
        }
        if (supportScale) {
            currentProvider = ReaderConstants.SINGLE_PAGE;
        } else {
            currentProvider = ReaderConstants.REFLOW_PAGE;
        }
        getCurrentLayoutProvider().activate();
    }

    public void updateViewportSize() {
        getPageManager().setViewportRect(new RectF(0, 0, getReaderViewOptions().getViewWidth() - 1, getReaderViewOptions().getViewHeight() - 1));
    }

    public LayoutProvider getCurrentLayoutProvider() {
        return provider.get(getCurrentLayoutType());
    }

    public final String getCurrentLayoutType() {
        return currentProvider;
    }

    public boolean setCurrentLayout(final String layoutName) throws ReaderException {
        if (!provider.containsKey(layoutName)) {
            return false;
        }

        // before change layout, record current position.
        String pagePosition = getCurrentPageName();

        currentProvider = layoutName;
        getCurrentLayoutProvider().activate();

        // goto the stored page
        if (StringUtils.isNotBlank(pagePosition)) {
            getCurrentLayoutProvider().gotoPosition(pagePosition);
        }
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

    public boolean isSupportScale() {
        return supportScale;
    }

    public boolean isSupportReflow() {
        return supportReflow;
    }


    // share the same instance for all providers. if necessary, we could change to use different
    // instance for different providers.
    public PageManager getPageManager() {
        if (pageManager == null) {
            pageManager = new PageManager();
        }
        return pageManager;
    }

    public HistoryManager getHistoryManager() {
        if (historyManager == null) {
            historyManager = new HistoryManager();
        }
        return historyManager;
    }

    public boolean canGoBack() {
        return getHistoryManager().canGoBack();
    }

    public boolean canGoForward() {
        return getHistoryManager().canGoForward();
    }

    public void beforePositionChange() {

    }

    public void onPositionChanged() {
        savePositionSnapshot();
    }

    public boolean goBack() {
        if (!canGoBack()) {
            return false;
        }

        restoreBySnapshot(getHistoryManager().backward());
        return true;
    }

    public boolean goForward() {
        if (!canGoForward()) {
            return false;
        }
        restoreBySnapshot(getHistoryManager().forward());
        return true;
    }

    private boolean savePositionSnapshot() {
        try {
            final PositionSnapshot snapshot = getCurrentLayoutProvider().saveSnapshot();
            if (snapshot != null) {
                getHistoryManager().addToHistory(snapshot.key(), false);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * internal method to change layout type and position without adding to history list.
     * @param snapshot
     * @return
     */
    private boolean restoreBySnapshot(final String snapshot) {
        try {
            PositionSnapshot positionSnapshot = PositionSnapshot.fromSnapshotKey(snapshot);
            currentProvider = positionSnapshot.layoutType;
            getCurrentLayoutProvider().activate();
            getCurrentLayoutProvider().restoreBySnapshot(positionSnapshot);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String getCurrentPageName() {
        return getPageManager().getFirstVisiblePageName();
    }

    public final PageInfo getCurrentPageInfo() {
        return getPageManager().getFirstVisiblePage();
    }

    public boolean gotoPosition(final String position) throws ReaderException {
        beforePositionChange();
        if (getCurrentLayoutProvider().gotoPosition(position)) {
            onPositionChanged();
            return true;
        }
        return false;
    }

    public boolean drawVisiblePages(final Reader reader, ReaderBitmapImpl bitmap, final ReaderViewInfo viewInfo) throws ReaderException {
        boolean ret = getCurrentLayoutProvider().drawVisiblePages(reader, bitmap, viewInfo);
        return ret;
    }

    public void setScale(final String pageName, final float scale, final float x, final float y) throws ReaderException {
        beforePositionChange();
        getCurrentLayoutProvider().setScale(pageName, scale, x, y);
        onPositionChanged();
    }

    public void scaleToPage(final String pageName) throws ReaderException {
        beforePositionChange();
        getCurrentLayoutProvider().scaleToPage(pageName);
        onPositionChanged();
    }

    public void scaleToWidth(final String pageName) throws ReaderException {
        beforePositionChange();
        getCurrentLayoutProvider().scaleToWidth(pageName);
        onPositionChanged();
    }

    public void scaleByRect(final String pageName, final RectF child) throws ReaderException {
        beforePositionChange();
        getCurrentLayoutProvider().scaleByRect(pageName, child);
        onPositionChanged();
    }

    public boolean changeScaleWithDelta(final String pageName, float delta) throws ReaderException {
        beforePositionChange();
        getCurrentLayoutProvider().changeScaleWithDelta(pageName, delta);
        onPositionChanged();
        return true;
    }

    public boolean nextScreen() throws ReaderException {
        beforePositionChange();
        if (getCurrentLayoutProvider().nextScreen()) {
            onPositionChanged();
            return true;
        }
        return false;
    }

    public boolean prevScreen() throws ReaderException {
        beforePositionChange();
        if (getCurrentLayoutProvider().prevScreen()) {
            onPositionChanged();
            return true;
        }
        return false;
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return getCurrentLayoutProvider().setNavigationArgs(args);
    }

    public void setStyle(final ReaderStyle style) throws ReaderException {
        getCurrentLayoutProvider().setStyle(style);
    }

}
