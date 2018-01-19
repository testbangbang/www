package com.onyx.jdread.library.event;

/**
 * Created by hehai on 18-1-17.
 */

public class SearchBookKeyEvent {
    private String searchKey;

    public SearchBookKeyEvent(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchKey() {
        return searchKey;
    }
}
