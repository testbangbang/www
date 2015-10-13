package com.onyx.reader.plugins.adobe;

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
     * @param viewportLeft the viewportInPage left in document coordinate system.
     * @param viewportTop the viewportInPage top in document coordinate system.
     * @param viewportRight the viewportInPage right in document coordinate system.
     * @param viewportBottom the viewportInPage bottom in document coordinate system.
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
        info.pageRectInHost.set(pageLeft, pageTop, pageLeft + (pageNaturalWidth * scale), pageTop + (pageNaturalHeight * scale));
        info.viewportRectInHost.set(viewportLeft, viewportTop, viewportRight, viewportBottom);
        info.pageRectInScreen.set(info.pageRectInHost.left - info.viewportRectInHost.left,
                info.pageRectInHost.top - info.viewportRectInHost.top,
                info.pageRectInHost.left - info.viewportRectInHost.left + (pageNaturalWidth * scale),
                info.pageRectInHost.top - info.viewportRectInHost.top + (pageNaturalHeight * scale));
        return info;
    }


}
