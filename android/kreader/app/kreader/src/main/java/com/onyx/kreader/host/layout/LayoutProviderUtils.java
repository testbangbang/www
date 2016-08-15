package com.onyx.kreader.host.layout;

import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.api.ReaderRenderer;
import com.onyx.kreader.cache.BitmapLruCache;
import com.onyx.kreader.cache.BitmapSoftLruCache;
import com.onyx.kreader.cache.ReaderBitmapImpl;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.common.ReaderDrawContext;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.host.math.PageManager;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.navigation.NavigationList;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.kreader.utils.ObjectHolder;

import java.lang.ref.SoftReference;
import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class LayoutProviderUtils {

    private static final String TAG = LayoutProviderUtils.class.getSimpleName();
    static boolean ENABLE_CACHE = true;

    /**
     * draw all visible pages. For each page:render the visible part of page. in screen coordinates system.
     * Before draw, make sure all visible pages have been calculated correctly.
     * @param layoutManager
     * @param renderedBitmap
     */
    static public void drawVisiblePages(final Reader reader,
                                        final ReaderLayoutManager layoutManager,
                                        final ReaderDrawContext drawContext,
                                        final ObjectHolder<ReaderBitmapImpl> renderedBitmap,
                                        final ReaderViewInfo readerViewInfo) throws ReaderException {
        // step1: prepare.
        final ReaderBitmapImpl bitmap = new ReaderBitmapImpl();
        renderedBitmap.setObject(bitmap);

        final ReaderRenderer renderer = reader.getRenderer();
        final BitmapSoftLruCache cache = reader.getBitmapCache();
        List<PageInfo> visiblePages = layoutManager.getPageManager().collectVisiblePages();

        // step2: check cache.
        final String key = PositionSnapshot.cacheKey(visiblePages);
        boolean hitCache = false;
        if (ENABLE_CACHE && checkCache(cache, key, bitmap)) {
            hitCache = true;
        }
        if (!hitCache) {
            bitmap.attachWith(key, cache.getFreeBitmap(reader.getViewOptions().getViewWidth(), reader.getViewOptions().getViewHeight(), Bitmap.Config.ARGB_8888));
            bitmap.clear();
        }

        // step3: render
        drawVisiblePagesImpl(renderer, layoutManager, bitmap, visiblePages, hitCache);

        // step4: update cache
        if (!hitCache && ENABLE_CACHE && StringUtils.isNotBlank(key)) {
            addToCache(cache, key, bitmap);
        }

        // final step: update view info.
        updateReaderViewInfo(readerViewInfo, layoutManager);
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
                        pageInfo.getPageDisplayOrientation(), bitmap, displayRect,
                        pageRect, visibleRect);
            }
        }
    }

    static public void updateReaderViewInfo(final ReaderViewInfo readerViewInfo,
                                            final ReaderLayoutManager layoutManager) throws ReaderException {
        final List<PageInfo> visiblePages = layoutManager.getPageManager().collectVisiblePages();
        for (PageInfo pageInfo : visiblePages) {
            readerViewInfo.copyPageInfo(pageInfo);
        }
        readerViewInfo.supportScalable = layoutManager.isSupportScale();
        readerViewInfo.supportReflow = layoutManager.isSupportReflow();
        readerViewInfo.canGoBack = layoutManager.canGoBack();
        readerViewInfo.canGoForward = layoutManager.canGoForward();
        readerViewInfo.viewportInDoc.set(layoutManager.getViewportRect());
        readerViewInfo.pagesBoundingRect.set(layoutManager.getPageBoundingRect());
        readerViewInfo.scale = layoutManager.getSpecialScale();
    }

    /**
     * draw page with scale to page on specified bitmap.
     * @param pageInfo
     * @param bitmap
     * @param readerRenderer
     */
    static public void drawPageWithScaleToPage(final PageInfo pageInfo, final ReaderBitmap bitmap, final ReaderRenderer readerRenderer) {
        final RectF viewport = new RectF(0, 0, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight());
        final float scale = PageUtils.scaleToPage(pageInfo.getOriginWidth(), pageInfo.getOriginHeight(), viewport.width(), viewport.height());

        final PageManager internalPageManager = new PageManager();
        internalPageManager.add(pageInfo);
        internalPageManager.setViewportRect(viewport);
        internalPageManager.scaleToPage(pageInfo.getName());
        final PageInfo visiblePage = internalPageManager.getFirstVisiblePage();
        final RectF visibleRect = new RectF(visiblePage.getPositionRect());
        visibleRect.intersect(viewport);

        BitmapUtils.clear(bitmap.getBitmap());
        readerRenderer.draw(pageInfo.getName(),
                scale,
                pageInfo.getPageDisplayOrientation(),
                bitmap,
                visiblePage.getDisplayRect(),
                visiblePage.getPositionRect(),
                visibleRect);
    }

    static public boolean addToCache(final BitmapSoftLruCache cache, final String key, final ReaderBitmapImpl bitmap) {
        cache.put(key, new SoftReference<>(bitmap.getBitmap()));
        return true;
    }

    static public boolean checkCache(final BitmapSoftLruCache cache, final String key, final ReaderBitmapImpl bitmap) {
        SoftReference<Bitmap> result = cache.get(key);
        if (result == null) {
            return false;
        }

        boolean succ = bitmap.attachWith(key, result.get());
        cache.put(key, null);
        return succ;
    }

    static public void clear(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().clear();
    }

    static public void addPage(final ReaderLayoutManager layoutManager, final String location) {
        RectF size = layoutManager.getReaderDocument().getPageOriginSize(location);
        PageInfo pageInfo = new PageInfo(location, size.width(), size.height());
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

    static public void addSinglePage(final ReaderLayoutManager layoutManager, final String position) {
        LayoutProviderUtils.clear(layoutManager);
        LayoutProviderUtils.addPage(layoutManager, position);
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
        String currentPagePosition = layoutManager.getCurrentPageName();
        return layoutManager.getNavigator().nextPage(currentPagePosition);
    }

    static public String prevPage(final ReaderLayoutManager layoutManager) {
        String currentPagePosition = layoutManager.getCurrentPageName();
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
