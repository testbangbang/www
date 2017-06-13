package com.onyx.edu.note.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 2017/6/24 17:38.
 */

public class ScribbleAction {
    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({INVALID, CREATE, EDIT})
    // Create an interface for validating int types
    public @interface ScribbleActionDef {
    }

    public static final int INVALID = -1;
    public static final int CREATE = 0;
    public static final int EDIT = 1;

    public static boolean isValidAction(@ScribbleActionDef int action) {
        return action >= CREATE && action <= EDIT;
    }

    public
    @ScribbleActionDef
    static int translate(int val) {
        return val;
    }
}
