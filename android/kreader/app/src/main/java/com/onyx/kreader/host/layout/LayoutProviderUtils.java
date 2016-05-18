package com.onyx.kreader.host.layout;

import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.kreader.api.ReaderRenderer;
import com.onyx.kreader.cache.BitmapLruCache;
import com.onyx.kreader.common.Benchmark;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.navigation.NavigationList;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.StringUtils;

import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class LayoutProviderUtils {

    private static final String TAG = LayoutProviderUtils.class.getSimpleName();

    static boolean enableCache = true;

    /**
     * draw all visible pages. For each page:render the visible part of page. in screen coordinates system.
     * Before draw, make sure all visible pages have been calculated correctly.
     * @param layoutManager
     * @param bitmap
     */
    static public void drawVisiblePages(final Reader reader, final ReaderLayoutManager layoutManager, final ReaderBitmapImpl bitmap, final ReaderViewInfo readerViewInfo) {
        final ReaderRenderer renderer = reader.getRenderer();
        final BitmapLruCache cache = reader.getBitmapLruCache();
        List<PageInfo> visiblePages = layoutManager.getPageManager().collectVisiblePages();
        final String key = PositionSnapshot.cacheKey(visiblePages);
        boolean hitCache = false;
        if (enableCache && checkCache(cache, key, bitmap)) {
            hitCache = true;
        }

        final RectF pageRect = new RectF();
        final RectF visibleRect = new RectF();
        for (PageInfo pageInfo : visiblePages) {
            String documentPosition = pageInfo.getName();
            final RectF displayRect = pageInfo.getDisplayRect();
            final RectF positionRect = pageInfo.getPositionRect();

            pageRect.set(positionRect);
            pageRect.offset(0, -positionRect.top);

            visibleRect.set(positionRect);
            visibleRect.intersect(layoutManager.getPageManager().getViewportRect());
            PageUtils.translateCoordinates(visibleRect, positionRect);
            if (!hitCache) {
                bitmap.clear();
                Debug.d(TAG, "page: " + documentPosition + ", scale: " + pageInfo.getActualScale() +
                        ", bitmap: " + bitmap.getBitmap().getWidth() + ", " + bitmap.getBitmap().getHeight() +
                        ", display rect: " + displayRect +
                        ", page rect: " + pageRect +
                        ", visible rect: " + visibleRect);
                if (renderer.draw(documentPosition, pageInfo.getActualScale(),
                        pageInfo.getPageDisplayOrientation(), bitmap, displayRect,
                        pageRect, visibleRect)) {
                    if (enableCache && StringUtils.isNotBlank(key)) {
                        addToCache(cache, key, bitmap);
                    }
                }
            }
            readerViewInfo.copyPageInfo(pageInfo);
        }

        updateReaderViewInfo(readerViewInfo, layoutManager);
    }

    static public void updateReaderViewInfo(final ReaderViewInfo readerViewInfo, final ReaderLayoutManager layoutManager) {
        readerViewInfo.supportScalable = layoutManager.isSupportScale();
        readerViewInfo.supportReflow = layoutManager.isSupportReflow();
        readerViewInfo.canGoBack = layoutManager.canGoBack();
        readerViewInfo.canGoForward = layoutManager.canGoForward();
    }

    static public boolean addToCache(final BitmapLruCache cache, final String key, final ReaderBitmapImpl bitmap) {
        final Bitmap result = cache.get(key);
        if (result == null) {
            Bitmap copy = bitmap.getBitmap().copy(bitmap.getBitmap().getConfig(), false);
            cache.put(key, copy);
            return true;
        }
        return false;
    }

    static public boolean checkCache(final BitmapLruCache cache, final String key, final ReaderBitmapImpl bitmap) {
        final Bitmap result = cache.get(key);
        if (result == null) {
            return false;
        }
        bitmap.copyFrom(result);
        return true;
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
        layoutManager.getPageManager().setViewportPosition(null, 0, 0);
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
