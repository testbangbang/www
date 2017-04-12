package com.onyx.android.sdk.reader.host.layout;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderRenderer;
import com.onyx.android.sdk.reader.cache.BitmapReferenceLruCache;
import com.onyx.android.sdk.reader.cache.ReaderBitmapImpl;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.math.PageManager;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.math.PositionSnapshot;
import com.onyx.android.sdk.reader.host.navigation.NavigationList;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.utils.BitmapUtils;

import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class LayoutProviderUtils {

    private static final Class TAG = LayoutProviderUtils.class;
    static boolean ENABLE_CACHE = true;

    /**
     * draw all visible pages. For each page:render the visible part of page. in screen coordinates system.
     * Before draw, make sure all visible pages have been calculated correctly.
     * @param reader
     * @param layoutManager
     * @param drawContext
     * @param readerViewInfo
     */
    static public void drawVisiblePages(final Reader reader,
                                        final ReaderLayoutManager layoutManager,
                                        final ReaderDrawContext drawContext,
                                        final ReaderViewInfo readerViewInfo) throws ReaderException {
        drawVisiblePages(reader, layoutManager, drawContext, readerViewInfo, true);
    }

    static public void drawVisiblePages(final Reader reader,
                                        final ReaderLayoutManager layoutManager,
                                        final ReaderDrawContext drawContext,
                                        final ReaderViewInfo readerViewInfo,
                                        final boolean enableCache) throws ReaderException {
        // step1: prepare.
        final ReaderRenderer renderer = reader.getRenderer();
        final BitmapReferenceLruCache cache = reader.getBitmapCache();
        List<PageInfo> visiblePages = layoutManager.getPageManager().collectVisiblePages();

        // step2: check cache.
        final String key = PositionSnapshot.cacheKey(visiblePages);
        boolean hitCache = false;
        if (ENABLE_CACHE && enableCache && checkCache(cache, key, drawContext)) {
            hitCache = true;
        }
        Debug.d(TAG, "hit cache: " + hitCache + ", " + key);
        if (!hitCache) {
            ReaderBitmapImpl freeBitmap = cache.getFreeBitmap(reader.getViewOptions().getViewWidth(),
                    reader.getViewOptions().getViewHeight(), Bitmap.Config.ARGB_8888);
            try {
                drawContext.renderingBitmap = new ReaderBitmapImpl();
                drawContext.renderingBitmap.attachWith(key, freeBitmap.getBitmapReference());
                drawContext.renderingBitmap.clear();
            } finally {
                freeBitmap.close();
            }
        }
        Debug.d(TAG, "rendering bitmap reference count: " + drawContext.renderingBitmap.getBitmapReference().getUnderlyingReferenceTestOnly().getRefCountTestOnly());

        // step3: render
        drawVisiblePagesImpl(renderer, layoutManager, drawContext.renderingBitmap, visiblePages, hitCache);

        // final step: update view info.
        updateReaderViewInfo(reader, readerViewInfo, layoutManager);
    }

    static private void drawVisiblePagesImpl(final ReaderRenderer renderer,
                                             final ReaderLayoutManager layoutManager,
                                             final ReaderBitmapImpl bitmap,
                                             final List<PageInfo> visiblePages,
                                             boolean hitCache) {

        final RectF pageRect = new RectF();
        final RectF visibleRect = new RectF();
        for (PageInfo pageInfo : visiblePages) {
            final String documentPosition = pageInfo.getName();
            final RectF displayRect = pageInfo.getDisplayRect();
            final RectF positionRect = pageInfo.getPositionRect();
            pageRect.set(positionRect);
            pageRect.offset(0, -positionRect.top);
            visibleRect.set(positionRect);
            visibleRect.intersect(layoutManager.getPageManager().getViewportRect());
            PageUtils.translateCoordinates(visibleRect, positionRect);
            if (!hitCache) {
                Debug.d(TAG, "draw visible page: " + documentPosition + ", scale: " + pageInfo.getActualScale() +
                        ", bitmap: " + bitmap.getBitmap().getWidth() + ", " + bitmap.getBitmap().getHeight() +
                        ", display rect: " + displayRect +
                        ", position rect: " + positionRect +
                        ", viewport: " + layoutManager.getPageManager().getViewportRect() +
                        ", page rect: " + pageRect +
                        ", visible rect: " + visibleRect);
                renderer.draw(documentPosition, pageInfo.getActualScale(),
                        pageInfo.getPageDisplayOrientation(), bitmap.getBitmap(), displayRect,
                        pageRect, visibleRect);
            }
        }
    }

    static public void updateReaderViewInfo(final Reader reader,
                                            final ReaderViewInfo readerViewInfo,
                                            final ReaderLayoutManager layoutManager) throws ReaderException {
        Debug.d(TAG, "updateReaderViewInfo");
        if (!reader.getRendererFeatures().supportScale()) {
            updateVisiblePagesForFlowDocument(reader, readerViewInfo, layoutManager);
        }
        final List<PageInfo> visiblePages = layoutManager.getPageManager().collectVisiblePages();
        for (PageInfo pageInfo : visiblePages) {
            readerViewInfo.copyPageInfo(pageInfo);
        }
        readerViewInfo.isFixedDocument = layoutManager.getReaderRendererFeatures().supportScale();
        readerViewInfo.canPrevScreen = layoutManager.getCurrentLayoutProvider().canPrevScreen();
        readerViewInfo.canNextScreen = layoutManager.getCurrentLayoutProvider().canNextScreen();
        readerViewInfo.supportTextPage = layoutManager.getReaderDocument().supportTextPage();
        readerViewInfo.supportReflow = layoutManager.isSupportReflow();
        readerViewInfo.supportScalable = layoutManager.getCurrentLayoutProvider().supportScale();
        readerViewInfo.supportFontSizeAdjustment = layoutManager.getReaderRendererFeatures().supportFontSizeAdjustment();
        readerViewInfo.supportTypefaceAdjustment = layoutManager.getReaderRendererFeatures().supportTypefaceAdjustment();
        readerViewInfo.canGoBack = layoutManager.canGoBack();
        readerViewInfo.canGoForward = layoutManager.canGoForward();
        readerViewInfo.viewportInDoc.set(layoutManager.getViewportRect());
        if (layoutManager.getCropRect() != null) {
            RectF rect = new RectF(layoutManager.getCropRect());
            PageUtils.translateCoordinates(rect, readerViewInfo.viewportInDoc);
            readerViewInfo.cropRegionInViewport.set(rect);
        }
        readerViewInfo.pagesBoundingRect.set(layoutManager.getPageBoundingRect());
        readerViewInfo.scale = layoutManager.getSpecialScale();
        if (layoutManager.getTextStyleManager() != null && layoutManager.getTextStyleManager().getStyle() != null) {
            readerViewInfo.readerTextStyle = ReaderTextStyle.copy(layoutManager.getTextStyleManager().getStyle());
        }
        if (layoutManager.getCurrentLayoutProvider().getNavigationArgs() != null &&
                layoutManager.getCurrentLayoutProvider().getNavigationArgs().getList() != null) {
            readerViewInfo.subScreenCount = layoutManager.getCurrentLayoutProvider().getNavigationArgs().getList().getSubScreenCount();
            readerViewInfo.autoCropForEachBlock = layoutManager.getCurrentLayoutProvider().getNavigationArgs().isAutoCropForEachBlock();
        }
        readerViewInfo.layoutChanged = layoutManager.isLayoutChanged();
    }

    static private void updateVisiblePagesForFlowDocument(final Reader reader,
                                                          final ReaderViewInfo readerViewInfo,
                                                          final ReaderLayoutManager layoutManager) {
        clear(layoutManager);
        String startPage = PagePositionUtils.fromPageNumber(reader.getNavigator().getScreenStartPageNumber());
        addPage(layoutManager, startPage, reader.getNavigator().getScreenStartPosition());
        layoutManager.getPageManager().gotoPage(reader.getNavigator().getScreenStartPosition());
        for (int i = reader.getNavigator().getScreenStartPageNumber() + 1;
             i <= reader.getNavigator().getScreenEndPageNumber();
             i++) {
            String page = PagePositionUtils.fromPageNumber(i);
            String pos = reader.getNavigator().getPositionByPageNumber(i);
            addPage(layoutManager, page, pos);
        }
        layoutManager.getPageManager().getVisiblePages().addAll(layoutManager.getPageManager().getPageInfoList());
    }

    static public PageInfo drawReflowablePage(final PageInfo pageInfo, final ReaderBitmap bitmap, final ReaderRenderer readerRenderer) {
        final RectF viewport = new RectF(0, 0, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight());
        if (!readerRenderer.draw(pageInfo.getPositionSafely(), 1.0f, 0, bitmap.getBitmap(), viewport, viewport, viewport)) {
            return null;
        }
        return pageInfo;
    }

    /**
     * draw page with scale to page on specified bitmap.
     * @param pageInfo
     * @param bitmap
     * @param readerRenderer
     */
    static public PageInfo drawPageWithScaleToPage(final PageInfo pageInfo, final ReaderBitmap bitmap, final ReaderRenderer readerRenderer) {
        final RectF viewport = new RectF(0, 0, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight());
        final float scale = PageUtils.scaleToPage(pageInfo.getOriginWidth(), pageInfo.getOriginHeight(), viewport.width(), viewport.height());

        final PageManager internalPageManager = new PageManager();
        internalPageManager.add(pageInfo);
        internalPageManager.setViewportRect(viewport);
        internalPageManager.scaleToPage(pageInfo.getPositionSafely());
        final PageInfo visiblePage = internalPageManager.getFirstVisiblePage();
        final RectF visibleRect = new RectF(visiblePage.getPositionRect());
        visibleRect.intersect(viewport);

        BitmapUtils.clear(bitmap.getBitmap());
        readerRenderer.draw(pageInfo.getPositionSafely(),
                scale,
                pageInfo.getPageDisplayOrientation(),
                bitmap.getBitmap(),
                visiblePage.getDisplayRect(),
                visiblePage.getPositionRect(),
                visibleRect);

        List<PageInfo> pageInfos = internalPageManager.collectVisiblePages();
        if (pageInfos.size() > 0){
            return pageInfos.get(0);
        }
        return null;
    }

    static public boolean checkCache(final BitmapReferenceLruCache cache, final String key, final ReaderDrawContext context) {
        ReaderBitmapImpl result = cache.get(key);
        if (result == null || !result.isGammaApplied(context.targetGammaCorrection) || !result.isEmboldenApplied(context.targetEmboldenLevel)) {
            return false;
        }

        result = result.clone();
        cache.remove(key);
        context.renderingBitmap = result;
        return true;
    }

    static public void clear(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().clear();
    }

    static public void addPage(final ReaderLayoutManager layoutManager, final String name) {
        addPage(layoutManager, name, name);
    }

    static public void addPage(final ReaderLayoutManager layoutManager, final String name, final String position) {
        PageInfo pageInfo = layoutManager.getPageManager().getPageInfo(position);
        if (pageInfo == null) {
            RectF size = layoutManager.getReaderDocument().getPageOriginSize(position);
            pageInfo = new PageInfo(name, position, size.width(), size.height());
            pageInfo.setIsTextPage(layoutManager.getReaderDocument().isTextPage(position));
        }
        layoutManager.getPageManager().add(pageInfo);
    }

    static public void addAllPage(final ReaderLayoutManager layoutManager) {
        int total = layoutManager.getNavigator().getTotalPage();
        LayoutProviderUtils.clear(layoutManager);
        for(int i = 0; i < total; ++i) {
            final String position = layoutManager.getNavigator().getPositionByPageNumber(i);
            LayoutProviderUtils.addPage(layoutManager, position);
        }
        LayoutProviderUtils.updatePageBoundingRect(layoutManager);
    }

    static public void addSinglePage(final ReaderLayoutManager layoutManager, final String name) {
        addSinglePage(layoutManager, name, name);
    }

    static public void addSinglePage(final ReaderLayoutManager layoutManager, final String name, final String position) {
        LayoutProviderUtils.clear(layoutManager);
        LayoutProviderUtils.addPage(layoutManager, name, position);
        LayoutProviderUtils.updatePageBoundingRect(layoutManager);
        LayoutProviderUtils.resetViewportPosition(layoutManager);
    }

    static public void updatePageBoundingRect(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().updatePagesBoundingRect();
    }

    static public void updateVisiblePages(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().collectVisiblePages();
    }

    static public void resetViewportPosition(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().panViewportPosition(null, 0, 0);
    }

    static public boolean moveViewportByPosition(final ReaderLayoutManager layoutManager, final String location) {
        return layoutManager.getPageManager().gotoPage(location);
    }

    static public void pan(final ReaderLayoutManager layoutManager, final float dx, final float dy) {
        layoutManager.getPageManager().panViewportPosition(dx, dy);
    }

    static public boolean firstSubScreen(final ReaderLayoutManager layoutManager, final String pageName, final NavigationList navigationList) {
        RectF subScreen = navigationList.first();
        if (subScreen == null) {
            return false;
        }
        layoutManager.getPageManager().scaleByRatioRect(pageName, subScreen);
        return true;
    }

    static public boolean lastSubScreen(final ReaderLayoutManager layoutManager, final String pageName, final NavigationList navigationList) {
        RectF subScreen = navigationList.last();
        if (subScreen == null) {
            return false;
        }
        layoutManager.getPageManager().scaleByRatioRect(pageName, subScreen);
        return true;
    }

    static public String firstPage(final ReaderLayoutManager layoutManager) {
        return layoutManager.getNavigator().getPositionByPageNumber(0);
    }

    static public String lastPage(final ReaderLayoutManager layoutManager) {
        int total = layoutManager.getNavigator().getTotalPage();
        return layoutManager.getNavigator().getPositionByPageNumber(total - 1);
    }

    static public String nextPage(final ReaderLayoutManager layoutManager) {
        String currentPagePosition = layoutManager.getCurrentPagePosition();
        return layoutManager.getNavigator().nextPage(currentPagePosition);
    }

    static public String prevPage(final ReaderLayoutManager layoutManager) {
        String currentPagePosition = layoutManager.getCurrentPagePosition();
        return layoutManager.getNavigator().prevPage(currentPagePosition);
    }

    static public String nextScreen(final ReaderLayoutManager layoutManager) {
        PageInfo pageInfo = layoutManager.getPageManager().getFirstVisiblePage();
        if (pageInfo == null) {
            return null;
        }

        return layoutManager.getNavigator().nextScreen(pageInfo.getName());
    }

    static public String prevScreen(final ReaderLayoutManager layoutManager) {
        PageInfo pageInfo = layoutManager.getPageManager().getFirstVisiblePage();
        if (pageInfo == null) {
            return null;
        }

        return layoutManager.getNavigator().prevScreen(pageInfo.getName());
    }
}
