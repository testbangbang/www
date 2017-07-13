package com.onyx.android.dr.event;

import com.onyx.android.sdk.data.model.Library;

import java.util.List;

/**
 * Created by hehai on 17-7-12.
 */

public class MainLibraryTabEvent {
    private Library library;
    private List<String> Languages;

    public MainLibraryTabEvent(Library library, List<String> languages) {
        this.library = library;
        Languages = languages;
    }

    public Library getLibrary() {
        return library;
    }

    public List<String> getLanguages() {
        return Languages;
    }
}
