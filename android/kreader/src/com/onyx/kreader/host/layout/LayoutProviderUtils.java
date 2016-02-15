package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderNavigator;
import com.onyx.kreader.api.ReaderRenderer;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PageManager;
import com.onyx.kreader.host.navigation.NavigationList;
import com.onyx.kreader.host.wrapper.Reader;

import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class LayoutProviderUtils {

    /**
     * draw all visible pages. For each page:render the visible part of page. in screen coordinates system.
     * Before draw, make sure all visible pages have been calculated correctly.
     * @param layoutManager
     * @param bitmap
     */
    static public void drawVisiblePages(final ReaderLayoutManager layoutManager, final ReaderBitmap bitmap) {
        final Reader reader = layoutManager.getReader();
        final ReaderRenderer renderer = reader.getRenderer();
        List<PageInfo> visiblePages = layoutManager.getPageManager().getVisiblePages();
        if (visiblePages == null || visiblePages.size() <= 0) {
            visiblePages = layoutManager.getPageManager().updateVisiblePages();
        }
        renderer.clear(bitmap);
        for(PageInfo pageInfo : visiblePages) {
            String documentPosition = pageInfo.getName();
            final RectF rect = pageInfo.getDisplayRect();
            renderer.draw(documentPosition, pageInfo.getActualScale(), bitmap, (int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
        }
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
        layoutManager.getPageManager().updateVisiblePages();
    }

    static public void resetViewportPosition(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().setViewport(0, 0);
    }

    static public void addNewSinglePage(final ReaderLayoutManager layoutManager, final String position) {
        LayoutProviderUtils.clear(layoutManager);
        LayoutProviderUtils.addPage(layoutManager, position);
        LayoutProviderUtils.updatePageBoundingRect(layoutManager);
        LayoutProviderUtils.resetViewportPosition(layoutManager);
    }

    static public boolean moveViewportByPosition(final ReaderLayoutManager layoutManager, final String location) {
        return layoutManager.getPageManager().moveViewportByPosition(location);
    }

    static public void pan(final ReaderLayoutManager layoutManager, final float dx, final float dy) {
        layoutManager.getPageManager().panViewport(dx, dy);
    }

    static public boolean firstSubScreen(final ReaderLayoutManager layoutManager, final NavigationList navigationList) {
        RectF subScreen = navigationList.first();
        if (subScreen == null) {
            return false;
        }
        layoutManager.getPageManager().scaleByRatio(subScreen);
        return true;
    }

    static public boolean lastSubScreen(final ReaderLayoutManager layoutManager, final NavigationList navigationList) {
        RectF subScreen = navigationList.last();
        if (subScreen == null) {
            return false;
        }
        layoutManager.getPageManager().scaleByRatio(subScreen);
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
        return layoutManager.getReader().getNavigator().nextPage(currentPagePosition);
    }

    static public String prevPage(final ReaderLayoutManager layoutManager) {
        String currentPagePosition = layoutManager.getCurrentPagePosition();
        return layoutManager.getReader().getNavigator().prevPage(currentPagePosition);
    }

    static public String nextScreen(final ReaderLayoutManager layoutManager) {
        PageInfo pageInfo = layoutManager.getPageManager().getFirstVisiblePage();
        if (pageInfo == null) {
            return null;
        }

        return layoutManager.getReader().getNavigator().nextScreen(pageInfo.getName());
    }

    static public String prevScreen(final ReaderLayoutManager layoutManager) {
        PageInfo pageInfo = layoutManager.getPageManager().getFirstVisiblePage();
        if (pageInfo == null) {
            return null;
        }

        return layoutManager.getReader().getNavigator().prevScreen(pageInfo.getName());
    }
}
