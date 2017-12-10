package com.onyx.edu.homework.note;

import android.util.SparseIntArray;

import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.ui.data.MenuId;

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

/**
 * Created by lxm on 2017/12/8.
 */

public class ScribbleSubMenuMap {

    private static Map<Float, Integer> strokeMapping;
    private static SparseIntArray shapeTypeSparseArray;
    private static SparseIntArray bgSparseArray;

    public static int bgFromMenuID(int menuID) {
        final SparseIntArray array = getBgSparseArray();
        return array.get(menuID, MenuId.BG_EMPTY);
    }
    public static int shapeTypeFromMenuID(int menuID) {
        final SparseIntArray array = getShapeTypeSparseArray();
        return array.get(menuID, MenuId.NORMAL_PEN_STYLE);
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

    public static Map<Float, Integer> getStrokeMapping() {
        if (strokeMapping == null) {
            strokeMapping = new HashMap<>();
            strokeMapping.put(NoteModel.getDefaultStrokeWidth(), MenuId.THICKNESS_ULTRA_LIGHT);
            strokeMapping.put(4.0f, MenuId.THICKNESS_LIGHT);
            strokeMapping.put(6.0f, MenuId.THICKNESS_NORMAL);
            strokeMapping.put(8.0f, MenuId.THICKNESS_BOLD);
            strokeMapping.put(10.0f, MenuId.THICKNESS_ULTRA_BOLD);
        }
        return strokeMapping;
    }

    public static SparseIntArray getShapeTypeSparseArray() {
        if (shapeTypeSparseArray == null) {
            shapeTypeSparseArray = new SparseIntArray();
            shapeTypeSparseArray.put(MenuId.NORMAL_PEN_STYLE, SHAPE_PENCIL_SCRIBBLE);
            shapeTypeSparseArray.put(MenuId.BRUSH_PEN_STYLE, SHAPE_BRUSH_SCRIBBLE);
            shapeTypeSparseArray.put(MenuId.TRIANGLE_STYLE, SHAPE_TRIANGLE);
            shapeTypeSparseArray.put(MenuId.LINE_STYLE, SHAPE_LINE);
            shapeTypeSparseArray.put(MenuId.CIRCLE_STYLE, SHAPE_CIRCLE);
            shapeTypeSparseArray.put(MenuId.RECT_STYLE, SHAPE_RECTANGLE);
            shapeTypeSparseArray.put(MenuId.TRIANGLE_45_STYLE, SHAPE_TRIANGLE_45);
            shapeTypeSparseArray.put(MenuId.TRIANGLE_60_STYLE, SHAPE_TRIANGLE_60);
            shapeTypeSparseArray.put(MenuId.TRIANGLE_90_STYLE, SHAPE_TRIANGLE_90);
            shapeTypeSparseArray.put(MenuId.ERASE_PARTIALLY, SHAPE_ERASER);
        }
        return shapeTypeSparseArray;
    }

    public static SparseIntArray getBgSparseArray() {
        if (bgSparseArray == null) {
            bgSparseArray = new SparseIntArray();
            bgSparseArray.put(MenuId.BG_EMPTY,  NoteBackgroundType.EMPTY);
            bgSparseArray.put(MenuId.BG_LINE,  NoteBackgroundType.LINE);
            bgSparseArray.put(MenuId.BG_GRID,  NoteBackgroundType.GRID);
            bgSparseArray.put(MenuId.BG_MUSIC,  NoteBackgroundType.MUSIC);
            bgSparseArray.put(MenuId.BG_MATS,  NoteBackgroundType.MATS);
            bgSparseArray.put(MenuId.BG_ENGLISH,  NoteBackgroundType.ENGLISH);
            bgSparseArray.put(MenuId.BG_TABLE_GRID,  NoteBackgroundType.GRID);
            bgSparseArray.put(MenuId.BG_LINE_COLUMN,  NoteBackgroundType.COLUMN);
            bgSparseArray.put(MenuId.BG_LEFT_GRID,  NoteBackgroundType.LEFT_GRID);
            bgSparseArray.put(MenuId.BG_GRID_POINT,  NoteBackgroundType.GRID_POINT);
            bgSparseArray.put(MenuId.BG_LINE_1_6,  NoteBackgroundType.LINE_1_6);
            bgSparseArray.put(MenuId.BG_LINE_2_0,  NoteBackgroundType.LINE_2_0);
            bgSparseArray.put(MenuId.BG_CALENDAR,  NoteBackgroundType.CALENDAR);
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

    public static Integer menuIdFromStrokeWidth(final float width) {
        final Map<Float, Integer> map = getStrokeMapping();
        if (map.containsKey(width)) {
            return map.get(width);
        }
        return MenuId.THICKNESS_CUSTOM_BOLD;
    }
}
