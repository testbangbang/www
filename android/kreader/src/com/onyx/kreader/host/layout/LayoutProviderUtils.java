package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderPagePosition;
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
     * draw all visible pages. For each page:
     * 1. ask plugin to goto that location through navigator interface
     * 2. set renderer scale and viewport
     * 3. render the visible part of page.
     * @param layoutManager
     * @param bitmap
     */
    static public void drawVisiblePages(final ReaderLayoutManager layoutManager, final ReaderBitmap bitmap) {
        final PageManager pageManager = layoutManager.getPageManager();
        final Reader reader = layoutManager.getReader();
        final ReaderRenderer renderer = reader.getRenderer();
        final ReaderNavigator navigator = reader.getNavigator();
        final List<PageInfo> visiblePages = layoutManager.getPageManager().updateVisiblePages();
        renderer.clear(bitmap);
        for(PageInfo pageInfo : visiblePages) {
            ReaderPagePosition documentPosition = navigator.createPositionFromString(pageInfo.getName());
            final RectF rect = pageInfo.visibleRectInViewport(pageManager.getViewportRect());
            renderer.draw(documentPosition, -1.0f, bitmap, (int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
        }
    }

    static public void clear(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().clear();
    }

    static public void addEntry(final ReaderLayoutManager layoutManager, final ReaderPagePosition location) {
        RectF size = layoutManager.getReaderHelper().getDocument().getPageNaturalSize(location);
        PageInfo pageInfo = new PageInfo(location.save(), size.width(), size.height());
        layoutManager.getPageManager().add(pageInfo);
    }

    static public void addAllEntry(final ReaderLayoutManager layoutManager) {
        int total = layoutManager.getNavigator().getTotalPage();
        LayoutProviderUtils.clear(layoutManager);
        for(int i = 0; i < total; ++i) {
            final ReaderPagePosition position = layoutManager.getNavigator().getPositionByPageNumber(i);
            LayoutProviderUtils.addEntry(layoutManager, position);
        }
        LayoutProviderUtils.updateHostRect(layoutManager);
    }

    static public void updateHostRect(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().updatePagesBoundingRect();
    }

    static public void resetViewportPosition(final ReaderLayoutManager layoutManager) {
        layoutManager.getPageManager().setViewport(0, 0);
    }

    static public void addNewSingleEntry(final ReaderLayoutManager layoutManager, final ReaderPagePosition position) {
        LayoutProviderUtils.clear(layoutManager);
        LayoutProviderUtils.addEntry(layoutManager, position);
        LayoutProviderUtils.updateHostRect(layoutManager);
        LayoutProviderUtils.resetViewportPosition(layoutManager);
    }

    static public boolean moveViewportByPosition(final ReaderLayoutManager layoutManager, final ReaderPagePosition location) {
        return layoutManager.getPageManager().moveViewportByPosition(location.save());
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

    static public ReaderPagePosition nextPage(final ReaderLayoutManager layoutManager) {
        PageInfo pageInfo = layoutManager.getPageManager().getFirstVisiblePage();
        if (pageInfo == null) {
            return null;
        }

        ReaderPagePosition current = layoutManager.getReader().getNavigator().createPositionFromString(pageInfo.getName());
        return layoutManager.getReader().getNavigator().nextPage(current);
    }

    static public ReaderPagePosition prevPage(final ReaderLayoutManager layoutManager) {
        PageInfo pageInfo = layoutManager.getPageManager().getFirstVisiblePage();
        if (pageInfo == null) {
            return null;
        }

        ReaderPagePosition current = layoutManager.getReader().getNavigator().createPositionFromString(pageInfo.getName());
        return layoutManager.getReader().getNavigator().prevPage(current);
    }

    static public ReaderPagePosition nextScreen(final ReaderLayoutManager layoutManager) {
        PageInfo pageInfo = layoutManager.getPageManager().getFirstVisiblePage();
        if (pageInfo == null) {
            return null;
        }

        ReaderPagePosition current = layoutManager.getReader().getNavigator().createPositionFromString(pageInfo.getName());
        return layoutManager.getReader().getNavigator().nextScreen(current);
    }

    static public ReaderPagePosition prevScreen(final ReaderLayoutManager layoutManager) {
        PageInfo pageInfo = layoutManager.getPageManager().getFirstVisiblePage();
        if (pageInfo == null) {
            return null;
        }

        ReaderPagePosition current = layoutManager.getReader().getNavigator().createPositionFromString(pageInfo.getName());
        return layoutManager.getReader().getNavigator().prevScreen(current);
    }
}
