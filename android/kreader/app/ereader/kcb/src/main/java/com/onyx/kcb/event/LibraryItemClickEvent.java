package com.onyx.kcb.event;

import com.onyx.android.sdk.data.model.Library;

/**
 * Created by hehai on 17-11-15.
 */

public class LibraryItemClickEvent {
    private Library library;
    public LibraryItemClickEvent(Library library) {
        this.library = library;
    }

    public Library getLibrary() {
        return library;
    }
}
