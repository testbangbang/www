package com.onyx.android.note.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 16/6/27 11:04.
 */

public class DataItemType {
    public static final int TYPE_INVALID = -1;
    public static final int TYPE_CREATE = 0;
    public static final int TYPE_GOTO_UP = 1;
    public static final int TYPE_LIBRARY = 2;
    public static final int TYPE_DOCUMENT = 3;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({TYPE_INVALID, TYPE_CREATE, TYPE_GOTO_UP, TYPE_LIBRARY, TYPE_DOCUMENT})
    // Create an interface for validating int types
    public @interface DataItemTypeDef {
    }

    public static boolean isValidDataItemType(int data) {
        return !(data < TYPE_CREATE || data > TYPE_DOCUMENT);
    }

    public
    @DataItemTypeDef
    static int translate(int val) {
        return val;
    }
}
