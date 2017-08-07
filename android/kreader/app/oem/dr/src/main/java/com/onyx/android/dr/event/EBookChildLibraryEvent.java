package com.onyx.android.dr.event;

import com.onyx.android.sdk.data.model.Library;

/**
 * Created by hehai on 17-8-3.
 */

public class EBookChildLibraryEvent {
    private Library library;
    public EBookChildLibraryEvent(Library library) {
        this.library = library;
    }

    public Library getLibrary() {
        return library;
    }
}
