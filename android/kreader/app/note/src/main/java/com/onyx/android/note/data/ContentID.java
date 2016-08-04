package com.onyx.android.note.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 16/6/24 11:59.
 */

public class ContentID {
    public static final int TYPE_NEW_PAGE = 0;
    public static final int TYPE_NOTE = 1;
    public static final int TYPE_FOLDER = 2;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({TYPE_NEW_PAGE, TYPE_NOTE, TYPE_FOLDER})
    // Create an interface for validating int types
    public @interface ContentIDDef {
    }

    public
    @ContentIDDef
    static int translate(int val) {
        return val;
    }
}
