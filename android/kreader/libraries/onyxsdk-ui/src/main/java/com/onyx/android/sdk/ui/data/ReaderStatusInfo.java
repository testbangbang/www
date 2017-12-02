package com.onyx.android.sdk.ui.data;

import android.graphics.Rect;

/**
 * Created by solskjaer49 on 14-4-21.
 */

public class ReaderStatusInfo {
    public Rect pageRect;
    public Rect viewportRect;
    public String pageReadStatus;
    public String batteryStatus;
    public int batteryLevel;
    public boolean batteryCharging;
    public String currentDocumentTittle;
    public int currentPage;
    public int totalPage;

    public ReaderStatusInfo(Rect pageRect, Rect viewportRect, int currentPage, int totalPage,
                            int batteryLevel, String documentTittle, boolean batteryCharging) {
        this.pageRect = pageRect;
        this.viewportRect = viewportRect;
        this.currentPage=currentPage;
        this.totalPage=totalPage;
        this.pageReadStatus = String.format("%d/%d", currentPage, totalPage);
        this.batteryStatus = String.format("%d%%", batteryLevel);
        this.batteryLevel = batteryLevel;
        this.batteryCharging = batteryCharging;

        this.currentDocumentTittle =documentTittle;
    }
}