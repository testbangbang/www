package com.onyx.android.note.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 16/8/4 15:44.
 */

public class ScribbleSubMenuID {
    public static final int THICKNESS_ULTRA_LIGHT = 0;
    public static final int THICKNESS_LIGHT = 1;
    public static final int THICKNESS_NORMAL = 2;
    public static final int THICKNESS_BOLD = 3;
    public static final int THICKNESS_ULTRA_BOLD = 4;
    public static final int NORMAL_PEN_STYLE = 5;
    public static final int BRUSH_PEN_STYLE = 6;
    public static final int LINE_STYLE = 7;
    public static final int TRIANGLE_STYLE = 8;
    public static final int CIRCLE_STYLE = 9;
    public static final int RECT_STYLE = 10;
    public static final int ERASE_PARTIALLY = 11;
    public static final int ERASE_TOTALLY = 12;
    public static final int BG_EMPTY = 13;
    public static final int BG_GRID = 14;
    public static final int BG_LINE = 15;
    public static final int BG_MATS = 16;
    public static final int BG_ENGLISH = 17;
    public static final int BG_MUSIC = 18;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({THICKNESS_ULTRA_LIGHT, THICKNESS_LIGHT, THICKNESS_NORMAL, THICKNESS_BOLD,
            THICKNESS_ULTRA_BOLD, NORMAL_PEN_STYLE, BRUSH_PEN_STYLE, LINE_STYLE, TRIANGLE_STYLE,
            CIRCLE_STYLE, RECT_STYLE, ERASE_PARTIALLY, ERASE_TOTALLY, BG_EMPTY, BG_GRID, BG_LINE, BG_MATS, BG_ENGLISH, BG_MUSIC})
    // Create an interface for validating int types
    public @interface ScribbleSubMenuIDDef {
    }

    public
    @ScribbleSubMenuIDDef
    static int translate(int val) {
        return val;
    }
}
