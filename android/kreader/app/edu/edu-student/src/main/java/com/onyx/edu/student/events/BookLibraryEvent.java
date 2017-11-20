package com.onyx.edu.student.events;

import com.onyx.android.sdk.data.model.Library;

/**
 * Created by suicheng on 2017/5/19.
 */
public class BookLibraryEvent {
    public Library library;

    public BookLibraryEvent(Library library) {
        this.library = library;
    }
}
