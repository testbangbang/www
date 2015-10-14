package com.onyx.reader.host.layout;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderBitmap;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.host.math.EntryInfo;
import com.onyx.reader.host.math.EntryManager;
import com.onyx.reader.host.wrapper.Reader;

import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class LayoutProviderUtils {

    static public void init(final ReaderLayoutManager layoutManager) {

    }

    static public void drawVisiblePages(final ReaderLayoutManager layoutManager, final ReaderBitmap bitmap) {
        final EntryManager entryManager = layoutManager.getEntryManager();
        final Reader reader = layoutManager.getReader();
        final List<EntryInfo> visiblePages = layoutManager.getEntryManager().updateVisiblePages();
        reader.getRenderer().clear(bitmap);
        for(EntryInfo entryInfo : visiblePages) {
            ReaderDocumentPosition documentPosition = reader.getNavigator().createPositionFromString(entryInfo.getName());
            reader.getNavigator().gotoPosition(documentPosition);
            reader.getRenderer().setScale(entryManager.getActualScale());
            final RectF entryViewport = entryInfo.viewportInPage(entryManager.getViewportRect());
            reader.getRenderer().setViewport(entryViewport.left, entryViewport.top);
            final RectF rect = entryInfo.visibleRectInViewport(entryManager.getViewportRect());
            reader.getRenderer().draw(bitmap, (int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
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

    static public void update(final ReaderLayoutManager layoutManager) {
        layoutManager.getEntryManager().update();
    }

    static public boolean moveViewportByPosition(final ReaderLayoutManager layoutManager, final ReaderDocumentPosition location) {
        return layoutManager.getEntryManager().moveViewportByPosition(location.save());
    }

    static public void pan(final ReaderLayoutManager layoutManager, final float dx, final float dy) {
        layoutManager.getEntryManager().panViewport(dx, dy);
    }

}
