package com.onyx.android.sdk.ui.data;

import android.graphics.Rect;

/**
 * Created by solskjaer49 on 14-4-21.
 */

public class PageStatusInfo {
    public Rect pageRect;
    public Rect viewportRect;
    public String pageReadStatus;
    public String batteryStatus;
    public String currentDocumentTittle;
    public int currentPage;
    public int totalPage;

    public PageStatusInfo(Rect pageRect, Rect viewportRect, int currentPage, int totalPage,
                          int batteryLevel,String documentTittle) {
        this.pageRect = pageRect;
        this.viewportRect = viewportRect;
        this.currentPage=currentPage;
        this.totalPage=totalPage;
        this.pageReadStatus = String.format("%d/%d", currentPage, totalPage);
        this.batteryStatus = String.format("%d%%", batteryLevel);
        this.currentDocumentTittle =documentTittle;
    }
}