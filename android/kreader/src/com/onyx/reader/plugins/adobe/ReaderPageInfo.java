package com.onyx.reader.plugins.adobe;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 12/27/13
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderPageInfo {
    public int pageNaturalWidth;
    public int pageNaturalHeight;
    public RectF autoCropContentRegion;    // region when zoom to page.
    public double autoCropScale;
    public int orientation;
    public RectF manualRegion;

    // page position in document coordinate system.
    public Rect pageRectInDoc = new Rect();

    // viewport in document coordinate system.
    public Rect viewportRectInDoc = new Rect();
    public Rect pageRectInScreen = new Rect();
    public double pageDisplayScale;
    public int pageNumber;
    public String location;


    /**
     *
     * @param pn page number
     * @param internalLocation the persistent page location
     * @param scale actual display scale.
     * @param viewportLeft the viewport left in document coordinate system.
     * @param viewportTop the viewport top in document coordinate system.
     * @param viewportRight the viewport right in document coordinate system.
     * @param viewportBottom the viewport bottom in document coordinate system.
     * @param pageNaturalWidth the page natural width.
     * @param pageNaturalHeight the page natural height;
     * @param pageLeft the page left position in document coordinate system. in single mode, it's 0 always.
     * @param pageTop the page top position in document coordinate system. in single mode, it's 0 always.
     * @return
     */
    public static ReaderPageInfo createInfo(int pn, String internalLocation, double scale, int viewportLeft, int viewportTop,
                                            int viewportRight, int viewportBottom, int pageNaturalWidth, int pageNaturalHeight,
                                            int pageLeft, int pageTop) {
        ReaderPageInfo info = new ReaderPageInfo();
        info.pageDisplayScale = scale;
        info.location = internalLocation;
        info.pageNaturalWidth = pageNaturalWidth;
        info.pageNaturalHeight = pageNaturalHeight;
        info.pageNumber = pn;
        info.pageRectInDoc.set(pageLeft, pageTop, pageLeft + (int)(pageNaturalWidth * scale), pageTop + (int)(pageNaturalHeight * scale));
        info.viewportRectInDoc.set(viewportLeft, viewportTop, viewportRight, viewportBottom);
        info.pageRectInScreen.set(info.pageRectInDoc.left - info.viewportRectInDoc.left,
                info.pageRectInDoc.top - info.viewportRectInDoc.top,
                info.pageRectInDoc.left - info.viewportRectInDoc.left + (int)(pageNaturalWidth * scale),
                info.pageRectInDoc.top - info.viewportRectInDoc.top + (int)(pageNaturalHeight * scale));
        return info;
    }

    public void clearAutoCropInfo() {
        autoCropContentRegion = null;
        autoCropScale = 0;
    }

    public int autoCropContentRegionHeight(double scale) {
        return (int)(autoCropContentRegion.height() *  scale / autoCropScale);
    }

    // get document position from screen position
    public PointF documentPointFromScreenPoint(float screenX, float screenY) {
        return new PointF((float)((screenX - pageRectInScreen.left) / pageDisplayScale),
                (float)((screenY - pageRectInScreen.top) / pageDisplayScale));
    }

    public Point screenPointFromDocument(int docX, int docY) {
        return new Point(docX + pageRectInDoc.left - pageRectInScreen.left,
                docY + pageRectInDoc.top - pageRectInScreen.top);
    }
}
