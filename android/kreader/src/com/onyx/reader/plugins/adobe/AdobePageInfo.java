package com.onyx.reader.plugins.adobe;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import com.onyx.reader.host.wrapper.ReaderPageInfo;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 12/27/13
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 * javap -s com.onyx.reader.plugins.adobe.AdobePageInfo
 * (ILjava/lang/String;FFFFFFFFF)Lcom/onyx/reader/host/wrapper/ReaderPageInfo;
 */
public class AdobePageInfo extends ReaderPageInfo {

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
    public static ReaderPageInfo createInfo(int pn,
                                            String internalLocation,
                                            float scale,
                                            float viewportLeft,
                                            float viewportTop,
                                            float viewportRight,
                                            float viewportBottom,
                                            float pageNaturalWidth,
                                            float pageNaturalHeight,
                                            float pageLeft,
                                            float pageTop) {
        ReaderPageInfo info = new ReaderPageInfo();
        info.pageDisplayScale = scale;
        info.location = internalLocation;
        info.pageNaturalRect.set(0, 0, pageNaturalWidth, pageNaturalHeight);
        info.pageNumber = pn;
        info.pageRectInDoc.set(pageLeft, pageTop, pageLeft + (int)(pageNaturalWidth * scale), pageTop + (int)(pageNaturalHeight * scale));
        info.viewportRectInDoc.set(viewportLeft, viewportTop, viewportRight, viewportBottom);
        info.pageRectInScreen.set(info.pageRectInDoc.left - info.viewportRectInDoc.left,
                info.pageRectInDoc.top - info.viewportRectInDoc.top,
                info.pageRectInDoc.left - info.viewportRectInDoc.left + (int)(pageNaturalWidth * scale),
                info.pageRectInDoc.top - info.viewportRectInDoc.top + (int)(pageNaturalHeight * scale));
        return info;
    }


}
