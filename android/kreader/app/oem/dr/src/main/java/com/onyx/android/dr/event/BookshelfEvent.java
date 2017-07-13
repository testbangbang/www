package com.onyx.android.dr.event;

/**
 * Created by hehai on 17-7-13.
 */

public class BookshelfEvent {
    private String language;

    public BookshelfEvent(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }
}
