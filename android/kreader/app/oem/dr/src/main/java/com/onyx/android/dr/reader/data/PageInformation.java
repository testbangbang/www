package com.onyx.android.dr.reader.data;

/**
 * Created by huxiaomao on 17/5/5.
 */

public class PageInformation {
    private static final String TAG = PageInformation.class.getSimpleName();
    private int displayWidth;
    private int displayHeight;
    private int currentPage = 0;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setNextPage() {
        this.currentPage++;
    }

    public void setPrevPage() {
        this.currentPage--;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
    }
}
