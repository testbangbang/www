package com.onyx.kreader.host.layout;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderDocumentPosition;
import com.onyx.kreader.api.ReaderNavigator;
import com.onyx.kreader.api.ReaderRenderer;
import com.onyx.kreader.host.math.EntryInfo;
import com.onyx.kreader.host.math.EntryManager;
import com.onyx.kreader.host.navigation.NavigationList;
import com.onyx.kreader.host.wrapper.Reader;

import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class LayoutProviderUtils {

    static public void init(final ReaderLayoutManager layoutManager) {

    }

    /**
     * draw all visible pages. For each page:
     * 1. ask plugin to goto that location through navigator interface
     * 2. set renderer scale and viewport
     * 3. render the visible part of page.
     * @param layoutManager
     * @param bitmap
     */
    static public void drawVisiblePages(final ReaderLayoutManager layoutManager, final ReaderBitmap bitmap) {
        final EntryManager entryManager = layoutManager.getEntryManager();
        final Reader reader = layoutManager.getReader();
        final ReaderRenderer renderer = reader.getRenderer();
        final ReaderNavigator navigator = reader.getNavigator();
        final List<EntryInfo> visiblePages = layoutManager.getEntryManager().updateVisiblePages();
        renderer.clear(bitmap);
        for(EntryInfo entryInfo : visiblePages) {
            ReaderDocumentPosition documentPosition = reader.getNavigator().createPositionFromString(entryInfo.getName());
            navigator.gotoPosition(documentPosition);
            renderer.setScale(entryManager.getActualScale());
            final RectF entryViewport = entryInfo.viewportInPage(entryManager.getViewportRect());
            renderer.setViewport(entryViewport.left, entryViewport.top);
            final RectF rect = entryInfo.visibleRectInViewport(entryManager.getViewportRect());
            renderer.draw(bitmap, (int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
        }
    }

    static public void clear(final ReaderLayoutManager layoutManager) {
        layoutManager.getEntryManager().clear();
    }

    static public void addEntry(final ReaderLayoutManager layoutManager, final ReaderDocumentPosition location) {
        RectF size = layoutManager.getReaderHelper().getDocument().getPageNaturalSize(location);
        EntryInfo entryInfo = new EntryInfo(size.width(), size.height());
        layoutManager.getEntryManager().add(location.save(), entryInfo);
    }

    static public void addAllEntry(final ReaderLayoutManager layoutManager) {
        int total = layoutManager.getNavigator().getTotalPage();
        LayoutProviderUtils.clear(layoutManager);
        for(int i = 0; i < total; ++i) {
            final ReaderDocumentPosition position = layoutManager.getReaderHelper().getNavigator().getPositionByPageNumber(i);
            LayoutProviderUtils.addEntry(layoutManager, position);
        }
        LayoutProviderUtils.updateHostRect(layoutManager);
    }

    static public void updateHostRect(final ReaderLayoutManager layoutManager) {
        layoutManager.getEntryManager().updateHostRect();
    }

    static public void resetViewportPosition(final ReaderLayoutManager layoutManager) {
        layoutManager.getEntryManager().setViewport(0, 0);
    }

    static public void addNewSingleEntry(final ReaderLayoutManager layoutManager, final ReaderDocumentPosition position) {
        LayoutProviderUtils.clear(layoutManager);
        LayoutProviderUtils.addEntry(layoutManager, position);
        LayoutProviderUtils.updateHostRect(layoutManager);
        LayoutProviderUtils.resetViewportPosition(layoutManager);
    }

    static public boolean moveViewportByPosition(final ReaderLayoutManager layoutManager, final ReaderDocumentPosition location) {
        return layoutManager.getEntryManager().moveViewportByPosition(location.save());
    }

    static public void pan(final ReaderLayoutManager layoutManager, final float dx, final float dy) {
        layoutManager.getEntryManager().panViewport(dx, dy);
    }

    static public boolean firstSubScreen(final ReaderLayoutManager layoutManager, final NavigationList navigationList) {
        RectF subScreen = navigationList.first();
        if (subScreen == null) {
            return false;
        }
        layoutManager.getEntryManager().scaleByRatio(subScreen);
        return true;
    }

    static public boolean lastSubScreen(final ReaderLayoutManager layoutManager, final NavigationList navigationList) {
        RectF subScreen = navigationList.last();
        if (subScreen == null) {
            return false;
        }
        layoutManager.getEntryManager().scaleByRatio(subScreen);
        return true;
    }

    static public ReaderDocumentPosition nextPage(final ReaderLayoutManager layoutManager) {
        EntryInfo entryInfo = layoutManager.getEntryManager().getFirstVisibleEntry();
        if (entryInfo == null) {
            return null;
        }

        ReaderDocumentPosition current = layoutManager.getReader().getNavigator().createPositionFromString(entryInfo.getName());
        return layoutManager.getReader().getNavigator().nextPage(current);
    }

    static public ReaderDocumentPosition prevPage(final ReaderLayoutManager layoutManager) {
        EntryInfo entryInfo = layoutManager.getEntryManager().getFirstVisibleEntry();
        if (entryInfo == null) {
            return null;
        }

        ReaderDocumentPosition current = layoutManager.getReader().getNavigator().createPositionFromString(entryInfo.getName());
        return layoutManager.getReader().getNavigator().prevPage(current);
    }

    static public ReaderDocumentPosition nextScreen(final ReaderLayoutManager layoutManager) {
        EntryInfo entryInfo = layoutManager.getEntryManager().getFirstVisibleEntry();
        if (entryInfo == null) {
            return null;
        }

        ReaderDocumentPosition current = layoutManager.getReader().getNavigator().createPositionFromString(entryInfo.getName());
        return layoutManager.getReader().getNavigator().nextScreen(current);
    }

    static public ReaderDocumentPosition prevScreen(final ReaderLayoutManager layoutManager) {
        EntryInfo entryInfo = layoutManager.getEntryManager().getFirstVisibleEntry();
        if (entryInfo == null) {
            return null;
        }

        ReaderDocumentPosition current = layoutManager.getReader().getNavigator().createPositionFromString(entryInfo.getName());
        return layoutManager.getReader().getNavigator().prevScreen(current);
    }
}
