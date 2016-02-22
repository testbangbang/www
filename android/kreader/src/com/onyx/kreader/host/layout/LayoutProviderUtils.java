package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderRenderer;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PositionSnapshot;
import com.onyx.kreader.host.navigation.NavigationList;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderCacheManager;
import com.onyx.kreader.utils.StringUtils;

import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class LayoutProviderUtils {

    static boolean enableCache = true;

    /**
     * draw all visible pages. For each page:render the visible part of page. in screen coordinates system.
     * Before draw, make sure all visible pages have been calculated correctly.
     * @param layoutManager
     * @param bitmap
     */
    static public void drawVisiblePages(final ReaderLayoutManager layoutManager, final ReaderBitmapImpl bitmap) {
        final Reader reader = layoutManager.getReader();
        final ReaderRenderer renderer = reader.getRenderer();
        final ReaderCacheManager cacheManager = reader.getReaderCacheManager();
        final String layoutType = layoutManager.getCurrentLayoutType();
        List<PageInfo> visiblePages = layoutManager.getPageManager().collectVisiblePages();
        final String key = PositionSnapshot.cacheKey(visiblePages);
        if (enableCache && checkCache(cacheManager, key, bitmap)) {
            return;
        }

        renderer.clear(bitmap);
        for(PageInfo pageInfo : visiblePages) {
            String documentPosition = pageInfo.getName();
            final RectF rect = pageInfo.getDisplayRect();
            renderer.draw(documentPosition, pageInfo.getActualScale(), pageInfo.getPageDisplayOrientation(), bitmap, (int) rect.left, (int) rect.top, (int) rect.width(), (int) rect.height());
        }

        if (enableCache && StringUtils.isNotBlank(key)) {
            addToCache(cacheManager, key, bitmap);
        }
    }

    static public boolean addToCache(final ReaderCacheManager cacheManager, final String key, final ReaderBitmapImpl bitmap) {
        final ReaderBitmapImpl cache = cacheManager.getBitmap(key);
        if (cache == null) {
            cacheManager.clear();
            cacheManager.addBitmap(key, bitmap);
            return true;
        }
        return false;
    }

    static public boolean checkCache(final ReaderCacheManager cacheManager, final String key, final ReaderBitmapImpl bitmap) {
        final ReaderBitmapImpl cache = cacheManager.getBitmap(key);
        if (cache == null) {
            return false;
        }
        bitmap.copyFrom(cache);
        return true;
    }

    static public void clear(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().clear();
    }

    static public void addPage(final ReaderLayoutManager layoutManager, final String location) {
        RectF size = layoutManager.getReaderHelper().getDocument().getPageOriginSize(location);
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

    static public void updatePageBoundingRect(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().updatePagesBoundingRect();
    }

    static public void updateVisiblePages(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().collectVisiblePages();
    }

    static public void resetViewportPosition(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().setViewportPosition(null, 0, 0);
    }

    static public void addNewSinglePage(final ReaderLayoutManager layoutManager, final String position) {
        LayoutProviderUtils.clear(layoutManager);
        LayoutProviderUtils.addPage(layoutManager, position);
        LayoutProviderUtils.updatePageBoundingRect(layoutManager);
        LayoutProviderUtils.resetViewportPosition(layoutManager);
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
        layoutManager.getPageManager().scaleByRect(pageName, subScreen);
        return true;
    }

    static public boolean lastSubScreen(final ReaderLayoutManager layoutManager, final String pageName, final NavigationList navigationList) {
        RectF subScreen = navigationList.last();
        if (subScreen == null) {
            return false;
        }
        layoutManager.getPageManager().scaleByRect(pageName, subScreen);
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
