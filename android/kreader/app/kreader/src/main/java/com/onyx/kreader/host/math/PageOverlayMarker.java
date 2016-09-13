package com.onyx.kreader.host.math;

import android.graphics.PointF;
import android.graphics.RectF;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by joy on 9/12/16.
 */
public class PageOverlayMarker {
    private static String lastPage;
    private static RectF lastViewport;

    public static void saveCurrentPageAndViewport(final Reader reader) {
        lastPage = reader.getReaderLayoutManager().getCurrentPageName();
        lastViewport = new RectF(reader.getReaderLayoutManager().getPageManager().getViewportRect());
    }

    public static void markLastViewportOverlayPointWhenNecessary(final Reader reader, final ReaderViewInfo viewInfo) {
        if (reader.getReaderLayoutManager().getPageManager().getVisiblePages().size() > 1) {
            return;
        }
        String page = reader.getReaderLayoutManager().getCurrentPageName();
        if (lastPage.compareTo(page) != 0) {
            return;
        }
        RectF viewport = new RectF(reader.getReaderLayoutManager().getPageManager().getViewportRect());
        if (lastViewport.equals(viewport)) {
            return;
        }
        if (!RectF.intersects(lastViewport, viewport)) {
            return;
        }
        viewInfo.setLastViewportOverlayPosition(getOverlayPoint(lastViewport, viewport));
    }

    /**
     *
     * @param oldViewport
     * @param newViewport
     * @return
     */
    private static PointF getOverlayPoint(RectF oldViewport, RectF newViewport) {
        float y;
        if (oldViewport.top < newViewport.top) {
            y = oldViewport.bottom;
        } else {
            y = oldViewport.top;
        }
        return new PointF(0, y - newViewport.top);
    }

}
