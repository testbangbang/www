package com.onyx.edu.note.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 2017/7/11 15:31.
 */

public class ScribbleFunctionMenuIDType {
    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({FUNCTION_BAR_MENU, SUB_MENU, TOOL_BAR_MENU})
    // Create an interface for validating int types
    public @interface ScribbleMenuIDTypeDef {
    }

    public static final int FUNCTION_BAR_MENU = 0;
    public static final int SUB_MENU = 1;
    public static final int TOOL_BAR_MENU = 2;

    public
    @ScribbleFunctionMenuIDType.ScribbleMenuIDTypeDef
    static int translate(int val) {
        return val;
    }
}
