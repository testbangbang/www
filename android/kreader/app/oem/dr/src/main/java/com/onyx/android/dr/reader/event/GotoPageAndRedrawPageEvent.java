package com.onyx.android.dr.reader.event;

/**
 * Created by hehai on 17-7-21.
 */

public class GotoPageAndRedrawPageEvent {
    private int page;
    private String pagePosition;

    public GotoPageAndRedrawPageEvent(int page, String pagePosition) {
        this.page = page;
        this.pagePosition = pagePosition;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(String pagePosition) {
        this.pagePosition = pagePosition;
    }
}
