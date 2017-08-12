package com.onyx.android.dr.event;

import com.onyx.android.sdk.data.model.Library;

import java.util.List;

/**
 * Created by hehai on 17-7-12.
 */

public class MainLibraryTabEvent {
    private Library library;

    public MainLibraryTabEvent(Library library) {
        this.library = library;
    }

    public Library getLibrary() {
        return library;
    }
}
