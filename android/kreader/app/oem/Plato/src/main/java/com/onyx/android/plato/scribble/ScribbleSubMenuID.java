package com.onyx.android.plato.scribble;

import android.support.annotation.IntDef;
import android.util.SparseIntArray;

import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_BRUSH_SCRIBBLE;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_CIRCLE;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_ERASER;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_LINE;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_PENCIL_SCRIBBLE;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_RECTANGLE;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_TRIANGLE;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_TRIANGLE_45;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_TRIANGLE_60;
import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_TRIANGLE_90;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_CALENDAR;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_EMPTY;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_ENGLISH;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_GRID;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_GRID_5_5;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_GRID_POINT;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_LEFT_GRID;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_LINE;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_LINE_1_6;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_LINE_2_0;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_LINE_COLUMN;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_MATS;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_MUSIC;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Background.BG_TABLE_GRID;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Eraser.ERASE_PARTIALLY;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Eraser.ERASE_TOTALLY;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenColor.PEN_COLOR_BLACK;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenColor.PEN_COLOR_BLUE;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenColor.PEN_COLOR_GREEN;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenColor.PEN_COLOR_MAGENTA;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenColor.PEN_COLOR_RED;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenColor.PEN_COLOR_YELLOW;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenStyle.BRUSH_PEN_STYLE;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenStyle.CIRCLE_STYLE;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenStyle.LINE_STYLE;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenStyle.NORMAL_PEN_STYLE;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenStyle.RECT_STYLE;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenStyle.TRIANGLE_45_STYLE;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenStyle.TRIANGLE_60_STYLE;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenStyle.TRIANGLE_90_STYLE;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.PenStyle.TRIANGLE_STYLE;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Thickness.THICKNESS_BOLD;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Thickness.THICKNESS_CUSTOM_BOLD;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Thickness.THICKNESS_LIGHT;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Thickness.THICKNESS_NORMAL;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_BOLD;
import static com.onyx.android.plato.scribble.ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_LIGHT;


/**
 * Created by solskjaer49 on 16/8/4 15:44.
 */

public class ScribbleSubMenuID {

    public class Thickness {
        public static final int THICKNESS_ULTRA_LIGHT = 500;
        public static final int THICKNESS_LIGHT = 501;
        public static final int THICKNESS_NORMAL = 502;
        public static final int THICKNESS_BOLD = 503;
        public static final int THICKNESS_ULTRA_BOLD = 504;
        public static final int THICKNESS_CUSTOM_BOLD = 505;
    }

    public class PenStyle {
        public static final int NORMAL_PEN_STYLE = 100;
        public static final int BRUSH_PEN_STYLE = 101;
        public static final int LINE_STYLE = 102;
        public static final int TRIANGLE_STYLE = 103;
        public static final int CIRCLE_STYLE = 104;
        public static final int RECT_STYLE = 105;
        public static final int TRIANGLE_45_STYLE = 106;
        public static final int TRIANGLE_60_STYLE = 107;
        public static final int TRIANGLE_90_STYLE = 108;
    }

    public class Eraser {
        public static final int ERASE_PARTIALLY = 200;
        public static final int ERASE_TOTALLY = 201;
    }

    public class Background {
        public static final int BG_EMPTY = 300;
        public static final int BG_GRID = 301;
        public static final int BG_LINE = 302;
        public static final int BG_MATS = 303;
        public static final int BG_ENGLISH = 304;
        public static final int BG_MUSIC = 305;
        public static final int BG_TABLE_GRID = 306;
        public static final int BG_LEFT_GRID = 307;
        public static final int BG_LINE_COLUMN = 308;
        public static final int BG_GRID_5_5 = 309;
        public static final int BG_GRID_POINT = 310;
        public static final int BG_LINE_1_6 = 311;
        public static final int BG_LINE_2_0 = 312;
        public static final int BG_CALENDAR = 313;
    }

    public class PenColor {
        public static final int PEN_COLOR_BLACK = 400;
        public static final int PEN_COLOR_RED = 401;
        public static final int PEN_COLOR_YELLOW = 402;
        public static final int PEN_COLOR_BLUE = 403;
        public static final int PEN_COLOR_GREEN = 404;
        public static final int PEN_COLOR_MAGENTA = 405;
    }

    private static Map<Float, Integer> strokeMapping;
    private static SparseIntArray shapeTypeSparseArray;
    private static SparseIntArray bgSparseArray;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({THICKNESS_ULTRA_LIGHT, THICKNESS_LIGHT, THICKNESS_NORMAL, THICKNESS_BOLD,
            THICKNESS_ULTRA_BOLD, THICKNESS_CUSTOM_BOLD,
            NORMAL_PEN_STYLE, BRUSH_PEN_STYLE, LINE_STYLE, TRIANGLE_STYLE,
            CIRCLE_STYLE, RECT_STYLE, TRIANGLE_45_STYLE, TRIANGLE_60_STYLE, TRIANGLE_90_STYLE,
            ERASE_PARTIALLY, ERASE_TOTALLY,
            BG_EMPTY, BG_GRID, BG_LINE, BG_TABLE_GRID, BG_MATS, BG_ENGLISH, BG_MUSIC, BG_LEFT_GRID, BG_LINE_COLUMN,
            BG_GRID_5_5, BG_GRID_POINT, BG_LINE_1_6, BG_LINE_2_0, BG_CALENDAR,
            PEN_COLOR_BLACK, PEN_COLOR_RED, PEN_COLOR_YELLOW, PEN_COLOR_BLUE,
            PEN_COLOR_GREEN, PEN_COLOR_MAGENTA
    })
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
            strokeMapping.put(NoteModel.getDefaultStrokeWidth(), THICKNESS_ULTRA_LIGHT);
            strokeMapping.put(4.0f, THICKNESS_LIGHT);
            strokeMapping.put(6.0f, THICKNESS_NORMAL);
            strokeMapping.put(8.0f, THICKNESS_BOLD);
            strokeMapping.put(10.0f, THICKNESS_ULTRA_BOLD);
        }
        return strokeMapping;
    }

    public static SparseIntArray getShapeTypeSparseArray() {
        if (shapeTypeSparseArray == null) {
            shapeTypeSparseArray = new SparseIntArray();
            shapeTypeSparseArray.put(NORMAL_PEN_STYLE, SHAPE_PENCIL_SCRIBBLE);
            shapeTypeSparseArray.put(BRUSH_PEN_STYLE, SHAPE_BRUSH_SCRIBBLE);
            shapeTypeSparseArray.put(TRIANGLE_STYLE, SHAPE_TRIANGLE);
            shapeTypeSparseArray.put(LINE_STYLE, SHAPE_LINE);
            shapeTypeSparseArray.put(CIRCLE_STYLE, SHAPE_CIRCLE);
            shapeTypeSparseArray.put(RECT_STYLE, SHAPE_RECTANGLE);
            shapeTypeSparseArray.put(TRIANGLE_45_STYLE, SHAPE_TRIANGLE_45);
            shapeTypeSparseArray.put(TRIANGLE_60_STYLE, SHAPE_TRIANGLE_60);
            shapeTypeSparseArray.put(TRIANGLE_90_STYLE, SHAPE_TRIANGLE_90);
            shapeTypeSparseArray.put(ERASE_PARTIALLY, SHAPE_ERASER);
        }
        return shapeTypeSparseArray;
    }

    public static SparseIntArray getBgSparseArray() {
        if (bgSparseArray == null) {
            bgSparseArray = new SparseIntArray();
            bgSparseArray.put(BG_EMPTY,  NoteBackgroundType.EMPTY);
            bgSparseArray.put(BG_LINE,  NoteBackgroundType.LINE);
            bgSparseArray.put(BG_GRID,  NoteBackgroundType.GRID);
            bgSparseArray.put(BG_MUSIC,  NoteBackgroundType.MUSIC);
            bgSparseArray.put(BG_MATS,  NoteBackgroundType.MATS);
            bgSparseArray.put(BG_ENGLISH,  NoteBackgroundType.ENGLISH);
            bgSparseArray.put(BG_TABLE_GRID,  NoteBackgroundType.GRID);
            bgSparseArray.put(BG_LINE_COLUMN,  NoteBackgroundType.COLUMN);
            bgSparseArray.put(BG_LEFT_GRID,  NoteBackgroundType.LEFT_GRID);
            bgSparseArray.put(BG_GRID_POINT,  NoteBackgroundType.GRID_POINT);
            bgSparseArray.put(BG_LINE_1_6,  NoteBackgroundType.LINE_1_6);
            bgSparseArray.put(BG_LINE_2_0,  NoteBackgroundType.LINE_2_0);
            bgSparseArray.put(BG_CALENDAR,  NoteBackgroundType.CALENDAR);
        }
        return bgSparseArray;
    }

    public static int menuIdFromShapeType(int shapeType) {
        final SparseIntArray array = getShapeTypeSparseArray();
        for (int i = 0; i < array.size(); i++) {
            int key = array.keyAt(i);
            int obj = array.get(key);
            if (obj == shapeType) {
                return key;
            }
        }
        return -1;
    }

    public static int shapeTypeFromMenuID(int menuID) {
        final SparseIntArray array = getShapeTypeSparseArray();
        return array.get(menuID, SHAPE_PENCIL_SCRIBBLE);
    }

    public static int menuIdFromBg(int bg) {
        final SparseIntArray array = getBgSparseArray();
        for (int i = 0; i < array.size(); i++) {
            int key = array.keyAt(i);
            int obj = array.get(key);
            if (obj == bg) {
                return key;
            }
        }
        return NoteBackgroundType.EMPTY;
    }

    public static int bgFromMenuID(int menuID) {
        final SparseIntArray array = getBgSparseArray();
        return array.get(menuID, BG_EMPTY);
    }

    public static float strokeWidthFromMenuId(final int menuId) {
        final Map<Float, Integer> map = getStrokeMapping();
        for (Map.Entry<Float, Integer> entry : map.entrySet()) {
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
        return THICKNESS_CUSTOM_BOLD;
    }

    public static boolean isSubMenuId(int menuID) {
        return menuID >= NORMAL_PEN_STYLE && menuID <= THICKNESS_CUSTOM_BOLD;
    }

    public static boolean isThicknessGroup(@ScribbleSubMenuIDDef int menuID) {
        return menuID >= THICKNESS_ULTRA_LIGHT && menuID <= THICKNESS_CUSTOM_BOLD;
    }

    public static boolean isPenStyleGroup(@ScribbleSubMenuIDDef int menuID) {
        return menuID >= NORMAL_PEN_STYLE && menuID <= TRIANGLE_90_STYLE;
    }

    public static boolean isEraserGroup(@ScribbleSubMenuIDDef int menuID) {
        return menuID >= ERASE_PARTIALLY && menuID <= ERASE_TOTALLY;
    }

    public static boolean isBackgroundGroup(@ScribbleSubMenuIDDef int menuID) {
        return menuID >= BG_EMPTY && menuID <= BG_CALENDAR;
    }

    public static boolean isPenColorGroup(@ScribbleSubMenuIDDef int menuID) {
        return menuID >= PEN_COLOR_BLACK && menuID <= PEN_COLOR_MAGENTA;
    }
}
