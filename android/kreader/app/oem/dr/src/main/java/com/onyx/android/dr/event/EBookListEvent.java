package com.onyx.android.dr.event;

/**
 * Created by hehai on 17-8-3.
 */

public class EBookListEvent {
    private String language;

    public EBookListEvent(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }
}
