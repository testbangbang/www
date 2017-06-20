package com.onyx.android.sdk.reader.host.layout;

import android.graphics.Rect;
import android.graphics.RectF;

import com.onyx.android.sdk.reader.host.math.PageManager;
import com.onyx.android.sdk.reader.host.math.PositionSnapshot;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.host.wrapper.ReaderHelper;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.api.*;
import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.reflow.ImageReflowManager;
import com.onyx.android.sdk.reader.utils.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private ReaderHelper readerHelper;
    private ReaderDocument readerDocument;
    private ReaderNavigator readerNavigator;
    private ReaderRendererFeatures readerRendererFeatures;
    private ReaderTextStyleManager textStyleManager;
    private ReaderViewOptions readerViewOptions;
    private HistoryManager historyManager;
    private PageManager pageManager;
    private String currentProvider;
    private Map<String, LayoutProvider> provider = new HashMap<String, LayoutProvider>();
    private boolean supportScale;
    private boolean supportTextFlow;
    private boolean savePosition = true;

    public ReaderLayoutManager(final ReaderHelper helper,
                               final ReaderDocument document,
                               final ReaderNavigator navigator,
                               final ReaderRendererFeatures features,
                               final ReaderTextStyleManager styleManager,
                               final ReaderViewOptions viewOptions) {
        readerHelper = helper;
        readerDocument = document;
        readerNavigator = navigator;
        readerRendererFeatures = features;
        textStyleManager = styleManager;
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

    public ReaderTextStyleManager getTextStyleManager() {
        return textStyleManager;
    }

    public ReaderViewOptions getReaderViewOptions() {
        return readerViewOptions;
    }

    public ImageReflowManager getImageReflowManager() {
        return readerHelper.getImageReflowManager();
    }

    public void init()  {
        supportScale = getReaderRendererFeatures().supportScale();
        supportTextFlow = getReaderRendererFeatures().supportFontSizeAdjustment();
        if (supportScale) {
            provider.put(PageConstants.SINGLE_PAGE, new LayoutSinglePageProvider(this));
            provider.put(PageConstants.SINGLE_PAGE_NAVIGATION_LIST, new LayoutSinglePageNavigationListProvider(this));
            provider.put(PageConstants.CONTINUOUS_PAGE, new LayoutContinuousProvider(this));
            provider.put(PageConstants.IMAGE_REFLOW_PAGE, new LayoutImageReflowProvider(this));
        }
        if (supportTextFlow) {
            provider.put(PageConstants.TEXT_REFLOW_PAGE, new LayoutTextReflowProvider(this));
        }

        String layoutName = PageConstants.SINGLE_PAGE;
        if (!supportScale) {
            layoutName = PageConstants.TEXT_REFLOW_PAGE;
        }
        setActiveProvider(layoutName);
    }

    public void updateViewportSize() throws ReaderException {
        getCurrentLayoutProvider().updateViewportRect(new RectF(0, 0, getReaderViewOptions().getViewWidth(), getReaderViewOptions().getViewHeight()));
    }

    public LayoutProvider getCurrentLayoutProvider() {
        return provider.get(getCurrentLayoutType());
    }

    public final String getCurrentLayoutType() {
        return currentProvider;
    }

    public boolean setCurrentLayout(final String layoutName, NavigationArgs navigationArgs) throws ReaderException {
        if (!provider.containsKey(layoutName)) {
            return false;
        }

        if (currentProvider.equals(layoutName)) {
            // even it's same layout provider, user may want to change to a different navigation args
            getCurrentLayoutProvider().setNavigationArgs(navigationArgs);
            return true;
        }

        // before change layout, record current position.
        String pagePosition = getCurrentPagePosition();
        setActiveProvider(layoutName);
        getCurrentLayoutProvider().setNavigationArgs(navigationArgs);

        // goto the stored page
        if (StringUtils.isNotBlank(pagePosition)) {
            getCurrentLayoutProvider().gotoPosition(pagePosition);
        }
        readerHelper.onLayoutChanged();
        return true;
    }

    private void setActiveProvider(String layoutName) {
        if (currentProvider != null) {
            getCurrentLayoutProvider().deactivate();
        }
        currentProvider = layoutName;
        getCurrentLayoutProvider().activate();
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

    public boolean isScaleToPageContent() {
        return getPageManager().isScaleToPageContent();
    }

    public boolean isWidthCrop() {
        return getPageManager().isWidthCrop();
    }

    public int getSpecialScale() {
        return getPageManager().getSpecialScale();
    }

    public boolean isLayoutChanged() {
        return readerHelper.isLayoutChanged();
    }

    public void setSavePosition(boolean save) {
        savePosition = save;
    }

    public boolean isSavePosition() {
        return savePosition;
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

    public RectF getCropRect() throws ReaderException {
        return getCurrentLayoutProvider().getCropRect();
    }

    public RectF getPageRectOnViewport(final String position) throws ReaderException {
        return getCurrentLayoutProvider().getPageRectOnViewport(position);
    }

    public boolean isSupportScale() {
        return supportScale;
    }

    public boolean isSupportReflow() {
        return supportTextFlow;
    }


    // share the same instance for all providers. if necessary, we could change to use different
    // instance for different providers.
    public PageManager getPageManager() {
        if (pageManager == null) {
            pageManager = new PageManager();
            pageManager.setCropProvider(new PageCropper(readerHelper.getRenderer()));
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
        if (isSavePosition()) {
            savePositionSnapshot();
        }
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

    public PositionSnapshot savePositionSnapshot() {
        try {
            final PositionSnapshot snapshot = getCurrentLayoutProvider().saveSnapshot();
            if (snapshot != null) {
                getHistoryManager().addToHistory(snapshot.key(), false);
                return snapshot;
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * internal method to change layout type and position without adding to history list.
     * @param snapshot
     * @return
     */
    private boolean restoreBySnapshot(final String snapshot) {
        try {
            PositionSnapshot positionSnapshot = PositionSnapshot.fromSnapshotKey(snapshot);
            setActiveProvider(positionSnapshot.layoutType);
            getCurrentLayoutProvider().restoreBySnapshot(positionSnapshot);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String getCurrentPagePosition() {
        return getPageManager().getFirstVisiblePagePosition();
    }

    public String getCurrentPageName() {
        PageInfo pageInfo = getPageManager().getFirstVisiblePage();
        if (pageInfo == null) {
            return null;
        }
        return pageInfo.getName();
    }

    public int getCurrentPageNumber() {
        PageInfo pageInfo = getPageManager().getFirstVisiblePage();
        if (pageInfo == null) {
            return -1;
        }
        return pageInfo.getPageNumber();
    }

    public final PageInfo getCurrentPageInfo() {
        return getPageManager().getFirstVisiblePage();
    }

    public boolean gotoPage(final int page) throws ReaderException {
        beforePositionChange();
        if (getCurrentLayoutProvider().gotoPage(page)) {
            onPositionChanged();
            return true;
        }
        return false;
    }

    public boolean gotoPosition(final String position) throws ReaderException {
        beforePositionChange();
        if (getCurrentLayoutProvider().gotoPosition(position)) {
            onPositionChanged();
            return true;
        }
        return false;
    }

    public boolean drawVisiblePages(final Reader reader, final ReaderDrawContext drawContext, final ReaderViewInfo viewInfo) throws ReaderException {
        drawContext.targetGammaCorrection = reader.getDocumentOptions().getGammaLevel();
        drawContext.targetTextGammaCorrection = reader.getDocumentOptions().getTextGammaLevel();
        drawContext.targetEmboldenLevel = reader.getDocumentOptions().getEmboldenLevel();
        if (!getCurrentLayoutProvider().drawVisiblePages(reader, drawContext, viewInfo)) {
            return false;
        }
        reader.getReaderHelper().applyPostBitmapProcess(viewInfo, drawContext.renderingBitmap);
        return true;
    }

    public void pan(final int dx, final int dy) throws ReaderException {
        beforePositionChange();
        getCurrentLayoutProvider().pan(dx, dy);
        onPositionChanged();
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

    public void scaleToHeight(final String pageName) throws ReaderException {
        beforePositionChange();
        getCurrentLayoutProvider().scaleToWidth(pageName);
        onPositionChanged();
    }

    public void scaleToPageContent(final String pageName) throws ReaderException {
        beforePositionChange();
        getCurrentLayoutProvider().scaleToPageContent(pageName);
        onPositionChanged();
    }

    public void scaleToWidthContent(final String pageName) throws ReaderException {
        beforePositionChange();
        getCurrentLayoutProvider().scaleToWidthContent(pageName);
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

    public void setStyle(final ReaderTextStyle style) throws ReaderException {
        getCurrentLayoutProvider().setStyle(style);
    }

}
