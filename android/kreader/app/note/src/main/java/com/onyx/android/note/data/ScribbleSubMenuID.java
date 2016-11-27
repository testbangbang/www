package com.onyx.android.note.data;

import android.support.annotation.IntDef;

import com.onyx.android.sdk.scribble.data.NoteModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

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
    public static final int PEN_COLOR_BLACK = 19;
    public static final int PEN_COLOR_RED = 20;
    public static final int PEN_COLOR_YELLOW = 21;
    public static final int PEN_COLOR_BLUE = 22;
    public static final int PEN_COLOR_GREEN = 23;
    public static final int PEN_COLOR_MAGENTA = 24;

    private static Map<Float, Integer> strokeMapping;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({THICKNESS_ULTRA_LIGHT, THICKNESS_LIGHT, THICKNESS_NORMAL, THICKNESS_BOLD,
            THICKNESS_ULTRA_BOLD, NORMAL_PEN_STYLE, BRUSH_PEN_STYLE, LINE_STYLE, TRIANGLE_STYLE,
            CIRCLE_STYLE, RECT_STYLE, ERASE_PARTIALLY, ERASE_TOTALLY, BG_EMPTY, BG_GRID, BG_LINE, BG_MATS, BG_ENGLISH, BG_MUSIC,
            PEN_COLOR_BLACK, PEN_COLOR_RED, PEN_COLOR_YELLOW, PEN_COLOR_BLUE, PEN_COLOR_GREEN, PEN_COLOR_MAGENTA})
    // Create an interface for validating int types
    public @interface ScribbleSubMenuIDDef {
    }

    public
    @ScribbleSubMenuIDDef
    static int translate(int val) {
        return val;
    }

    public static Map<Float, Integer> getStrokeMapping() {
        if (strokeMapping == null) {
            strokeMapping = new HashMap<>();
            strokeMapping.put(NoteModel.getDefaultStrokeWidth(), ScribbleSubMenuID.THICKNESS_ULTRA_LIGHT);
            strokeMapping.put(5.0f, ScribbleSubMenuID.THICKNESS_LIGHT);
            strokeMapping.put(7.0f, ScribbleSubMenuID.THICKNESS_NORMAL);
            strokeMapping.put(9.0f, ScribbleSubMenuID.THICKNESS_BOLD);
            strokeMapping.put(11.0f, ScribbleSubMenuID.THICKNESS_ULTRA_BOLD);
        }
        return strokeMapping;
    }

    public static float strokeWidthFromMenuId(final int menuId) {
        final Map<Float, Integer> map = getStrokeMapping();
        for(Map.Entry<Float, Integer> entry : map.entrySet()) {
            if (entry.getValue() == menuId) {
                return entry.getKey();
            }
        }
        return NoteModel.getDefaultStrokeWidth();
    }

    public static Integer menuIdFromStrokeWidth(final float width) {
        final Map<Float, Integer> map = getStrokeMapping();
        if (map.containsKey(width)) {
            return map.get(width);
        }
        return ScribbleSubMenuID.THICKNESS_ULTRA_LIGHT;
    }
}
