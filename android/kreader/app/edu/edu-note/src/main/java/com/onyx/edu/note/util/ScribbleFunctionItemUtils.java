package com.onyx.edu.note.util;

import android.support.annotation.IdRes;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.onyx.edu.note.R;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.data.ScribbleToolBarMenuID;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_CALENDAR;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_EMPTY;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_ENGLISH;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_GRID;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_GRID_5_5;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_GRID_POINT;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_LEFT_GRID;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_LINE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_LINE_1_6;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_LINE_2_0;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_LINE_COLUMN;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_MATS;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_MUSIC;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_TABLE_GRID;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Eraser.ERASE_PARTIALLY;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Eraser.ERASE_TOTALLY;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.BRUSH_PEN_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.CIRCLE_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.LINE_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.NORMAL_PEN_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.RECT_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_45_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_60_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_90_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Thickness.THICKNESS_BOLD;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Thickness.THICKNESS_CUSTOM_BOLD;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Thickness.THICKNESS_LIGHT;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Thickness.THICKNESS_NORMAL;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_BOLD;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_LIGHT;

/**
 * Created by solskjaer49 on 2017/7/11 16:28.
 */

public class ScribbleFunctionItemUtils {
    //TODO:here store icon res only,if one day need support string,use SparseArray<E> instead.
    private static SparseIntArray sFunctionBarMenuItemIDIconSparseArray;
    private static SparseIntArray sSubMenuItemIDIconSparseArray;
    private static SparseIntArray sToolBarMenuItemIDIconSparseArray;
    private static SparseArray<List<Integer>> sFunctionBarMenuContainSubMenuIDListSparseArray;

    private static List<Integer> buildSubMenuThicknessIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(THICKNESS_ULTRA_LIGHT);
        resultList.add(THICKNESS_LIGHT);
        resultList.add(THICKNESS_NORMAL);
        resultList.add(THICKNESS_BOLD);
        resultList.add(THICKNESS_CUSTOM_BOLD);
        return resultList;
    }

    private static List<Integer> buildSubMenuBGIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(BG_EMPTY);
        resultList.add(BG_LINE);
        resultList.add(BG_LEFT_GRID);
        resultList.add(BG_GRID_5_5);
        resultList.add(BG_GRID);
        resultList.add(BG_MATS);
        resultList.add(BG_MUSIC);
        resultList.add(BG_ENGLISH);
        resultList.add(BG_LINE_1_6);
        resultList.add(BG_LINE_2_0);
        resultList.add(BG_LINE_COLUMN);
        resultList.add(BG_TABLE_GRID);
        resultList.add(BG_CALENDAR);
        resultList.add(BG_GRID_POINT);
        return resultList;
    }

    private static List<Integer> buildSubMenuEraserIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(ERASE_PARTIALLY);
        resultList.add(ERASE_TOTALLY);
        return resultList;
    }

    private static List<Integer> buildSubMenuPenStyleIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(NORMAL_PEN_STYLE);
        resultList.add(BRUSH_PEN_STYLE);
        resultList.add(LINE_STYLE);
        resultList.add(TRIANGLE_STYLE);
        resultList.add(CIRCLE_STYLE);
        resultList.add(RECT_STYLE);
        resultList.add(TRIANGLE_45_STYLE);
        resultList.add(TRIANGLE_60_STYLE);
        resultList.add(TRIANGLE_90_STYLE);
        return resultList;
    }

    //TODO:temp build here.if custom needed can be add config in json.
    private static void buildIDIconSparseArray() {
        if (sFunctionBarMenuItemIDIconSparseArray == null) {
            buildFunctionMenuIDIconSparseArray();
        }
        if (sToolBarMenuItemIDIconSparseArray == null) {
            buildToolBarMenuIDIconSparseArray();
        }
        if (sSubMenuItemIDIconSparseArray == null) {
            buildSubMenuIDIconSparseArray();
        }
    }

    private static void buildToolBarMenuIDIconSparseArray() {
        sToolBarMenuItemIDIconSparseArray = new SparseIntArray();
        sToolBarMenuItemIDIconSparseArray.put(ScribbleToolBarMenuID.SWITCH_SCRIBBLE_MODE, R.drawable.ic_vector);
        sToolBarMenuItemIDIconSparseArray.put(ScribbleToolBarMenuID.UNDO, R.drawable.ic_undo);
        sToolBarMenuItemIDIconSparseArray.put(ScribbleToolBarMenuID.SAVE, R.drawable.ic_save);
        sToolBarMenuItemIDIconSparseArray.put(ScribbleToolBarMenuID.REDO, R.drawable.ic_redo);
        sToolBarMenuItemIDIconSparseArray.put(ScribbleToolBarMenuID.SETTING, R.drawable.ic_setting);
        sToolBarMenuItemIDIconSparseArray.put(ScribbleToolBarMenuID.EXPORT, R.drawable.ic_export);
    }

    private static void buildFunctionBarMenuSubMenuIDListSparseArray() {
        sFunctionBarMenuContainSubMenuIDListSparseArray = new SparseArray<>();
        sFunctionBarMenuContainSubMenuIDListSparseArray.put(ScribbleFunctionBarMenuID.PEN_WIDTH, buildSubMenuThicknessIDList());
        sFunctionBarMenuContainSubMenuIDListSparseArray.put(ScribbleFunctionBarMenuID.BG, buildSubMenuBGIDList());
        sFunctionBarMenuContainSubMenuIDListSparseArray.put(ScribbleFunctionBarMenuID.ERASER, buildSubMenuEraserIDList());
        sFunctionBarMenuContainSubMenuIDListSparseArray.put(ScribbleFunctionBarMenuID.PEN_STYLE, buildSubMenuPenStyleIDList());
    }

    private static void buildFunctionMenuIDIconSparseArray() {
        sFunctionBarMenuItemIDIconSparseArray = new SparseIntArray();
        sFunctionBarMenuItemIDIconSparseArray.put(ScribbleFunctionBarMenuID.PEN_STYLE, R.drawable.ic_shape);
        sFunctionBarMenuItemIDIconSparseArray.put(ScribbleFunctionBarMenuID.BG, R.drawable.ic_template);
        sFunctionBarMenuItemIDIconSparseArray.put(ScribbleFunctionBarMenuID.ERASER, R.drawable.ic_eraser);
        sFunctionBarMenuItemIDIconSparseArray.put(ScribbleFunctionBarMenuID.PEN_WIDTH, R.drawable.ic_width);
        sFunctionBarMenuItemIDIconSparseArray.put(ScribbleFunctionBarMenuID.DELETE, R.drawable.ic_delet_big);
        sFunctionBarMenuItemIDIconSparseArray.put(ScribbleFunctionBarMenuID.SPACE, R.drawable.ic_space);
        sFunctionBarMenuItemIDIconSparseArray.put(ScribbleFunctionBarMenuID.ENTER, R.drawable.ic_enter);
        sFunctionBarMenuItemIDIconSparseArray.put(ScribbleFunctionBarMenuID.KEYBOARD, R.drawable.ic_keyboard);
        sFunctionBarMenuItemIDIconSparseArray.put(ScribbleFunctionBarMenuID.COLOR, R.drawable.ic_color);
    }

    private static void buildSubMenuIDIconSparseArray() {
        sSubMenuItemIDIconSparseArray = new SparseIntArray();
        buildSubMenuThicknessIDIconSparseArray();
        buildSubMenuEraseIDIconSparseArray();
        buildSubMenuPenStyleIDIconSparseArray();
        buildBGIDIconSparseArray();
    }

    private static void buildSubMenuThicknessIDIconSparseArray() {
        sSubMenuItemIDIconSparseArray.put(THICKNESS_ULTRA_LIGHT, R.drawable.ic_width_1);
        sSubMenuItemIDIconSparseArray.put(THICKNESS_LIGHT, R.drawable.ic_width_2);
        sSubMenuItemIDIconSparseArray.put(THICKNESS_NORMAL, R.drawable.ic_width_3);
        sSubMenuItemIDIconSparseArray.put(THICKNESS_BOLD, R.drawable.ic_width_4);
        sSubMenuItemIDIconSparseArray.put(THICKNESS_ULTRA_BOLD, R.drawable.ic_width_5);
        sSubMenuItemIDIconSparseArray.put(THICKNESS_CUSTOM_BOLD, R.drawable.ic_width_6);
    }

    private static void buildSubMenuEraseIDIconSparseArray() {
        sSubMenuItemIDIconSparseArray.put(ERASE_PARTIALLY, R.drawable.ic_eraser_part);
        sSubMenuItemIDIconSparseArray.put(ERASE_TOTALLY, R.drawable.ic_eraser_all);
    }

    private static void buildSubMenuPenStyleIDIconSparseArray() {
        sSubMenuItemIDIconSparseArray.put(NORMAL_PEN_STYLE, R.drawable.ic_shape_pencil);
        sSubMenuItemIDIconSparseArray.put(BRUSH_PEN_STYLE, R.drawable.ic_shape_brush);
        sSubMenuItemIDIconSparseArray.put(LINE_STYLE, R.drawable.ic_shape_line);
        sSubMenuItemIDIconSparseArray.put(TRIANGLE_STYLE, R.drawable.ic_shape_triangle);
        sSubMenuItemIDIconSparseArray.put(CIRCLE_STYLE, R.drawable.ic_shape_circle);
        sSubMenuItemIDIconSparseArray.put(RECT_STYLE, R.drawable.ic_shape_square);
        sSubMenuItemIDIconSparseArray.put(TRIANGLE_45_STYLE, R.drawable.ic_shape_triangle_45);
        sSubMenuItemIDIconSparseArray.put(TRIANGLE_60_STYLE, R.drawable.ic_shape_triangle_60);
        sSubMenuItemIDIconSparseArray.put(TRIANGLE_90_STYLE, R.drawable.ic_shape_triangle_90);
    }

    private static void buildBGIDIconSparseArray() {
        sSubMenuItemIDIconSparseArray.put(BG_EMPTY, R.drawable.ic_template_white);
        sSubMenuItemIDIconSparseArray.put(BG_LINE, R.drawable.ic_template_line);
        sSubMenuItemIDIconSparseArray.put(BG_LEFT_GRID, R.drawable.ic_template_left_grid);
        sSubMenuItemIDIconSparseArray.put(BG_GRID_5_5, R.drawable.ic_template_grid_5_5);
        sSubMenuItemIDIconSparseArray.put(BG_GRID, R.drawable.ic_template_grid);
        sSubMenuItemIDIconSparseArray.put(BG_MATS, R.drawable.ic_template_mats);
        sSubMenuItemIDIconSparseArray.put(BG_MUSIC, R.drawable.ic_template_music);
        sSubMenuItemIDIconSparseArray.put(BG_ENGLISH, R.drawable.ic_template_english);
        sSubMenuItemIDIconSparseArray.put(BG_LINE_1_6, R.drawable.ic_template_line_1_6);
        sSubMenuItemIDIconSparseArray.put(BG_LINE_2_0, R.drawable.ic_template_line_2_0);
        sSubMenuItemIDIconSparseArray.put(BG_LINE_COLUMN, R.drawable.ic_template_line_column);
        sSubMenuItemIDIconSparseArray.put(BG_TABLE_GRID, R.drawable.ic_template_table_grid);
        sSubMenuItemIDIconSparseArray.put(BG_CALENDAR, R.drawable.ic_template_line_calendar);
        sSubMenuItemIDIconSparseArray.put(BG_GRID_POINT, R.drawable.ic_template_grid_point);
    }

    public static @IdRes
    int getFunctionBarItemIDIconRes(@ScribbleFunctionBarMenuID.ScribbleFunctionBarMenuDef int functionBarMenuID) {
        buildIDIconSparseArray();
        return sFunctionBarMenuItemIDIconSparseArray.get(functionBarMenuID);
    }


    public static @IdRes
    int getToolBarItemIDIconRes(@ScribbleToolBarMenuID.ScribbleToolBarMenuDef int toolBarMenuID) {
        buildIDIconSparseArray();
        return sToolBarMenuItemIDIconSparseArray.get(toolBarMenuID);
    }

    public static @IdRes
    int getSubItemIDIconRes(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        buildIDIconSparseArray();
        return sSubMenuItemIDIconSparseArray.get(subMenuID);
    }

    private static void checkSparseArray() {
    }

    public static List<Integer> getSubMenuIDList(@ScribbleFunctionBarMenuID.ScribbleFunctionBarMenuDef int functionBarMenuID) {
        if (sFunctionBarMenuContainSubMenuIDListSparseArray == null) {
            buildFunctionBarMenuSubMenuIDListSparseArray();
        }
        return sFunctionBarMenuContainSubMenuIDListSparseArray.get(functionBarMenuID);
    }

}
