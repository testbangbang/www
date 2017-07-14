package com.onyx.edu.note.util;

import android.support.annotation.IdRes;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.onyx.edu.note.R;
import com.onyx.edu.note.data.ScribbleMainMenuID;
import com.onyx.edu.note.data.ScribbleSubMenuID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/7/11 16:28.
 */

public class ScribbleFunctionItemUtils {
    //TODO:here store icon res only,if one day need support string,use SparseArray<E> instead.
    private static SparseIntArray sMainMenuItemIDIconSparseArray;
    private static SparseIntArray sSubMenuItemIDIconSparseArray;
    private static SparseArray<List<Integer>> sMainMenuContainSubMenuIDListSparseArray;

    //TODO:temp build here.if custom needed can be add config in json.
    private static void buildIDIconSparseArray() {
        buildMainMenuIDIconSparseArray();
        buildSubMenuIDIconSparseArray();
    }

    private static void buildMainMenuSubMenuIDListSparseArray() {
        sMainMenuContainSubMenuIDListSparseArray = new SparseArray<>();
        sMainMenuContainSubMenuIDListSparseArray.put(ScribbleMainMenuID.PEN_WIDTH, buildSubMenuThicknessIDList());
        sMainMenuContainSubMenuIDListSparseArray.put(ScribbleMainMenuID.BG, buildSubMenuBGIDList());
        sMainMenuContainSubMenuIDListSparseArray.put(ScribbleMainMenuID.ERASER, buildSubMenuEraserIDList());
        sMainMenuContainSubMenuIDListSparseArray.put(ScribbleMainMenuID.PEN_STYLE, buildSubMenuPenStyleIDList());
    }

    private static List<Integer> buildSubMenuThicknessIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(ScribbleSubMenuID.THICKNESS_ULTRA_LIGHT);
        resultList.add(ScribbleSubMenuID.THICKNESS_LIGHT);
        resultList.add(ScribbleSubMenuID.THICKNESS_NORMAL);
        resultList.add(ScribbleSubMenuID.THICKNESS_BOLD);
        resultList.add(ScribbleSubMenuID.THICKNESS_CUSTOM_BOLD);
        return resultList;
    }

    private static List<Integer> buildSubMenuBGIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(ScribbleSubMenuID.BG_EMPTY);
        resultList.add(ScribbleSubMenuID.BG_LINE);
        resultList.add(ScribbleSubMenuID.BG_LEFT_GRID);
        resultList.add(ScribbleSubMenuID.BG_GRID_5_5);
        resultList.add(ScribbleSubMenuID.BG_GRID);
        resultList.add(ScribbleSubMenuID.BG_MATS);
        resultList.add(ScribbleSubMenuID.BG_MUSIC);
        resultList.add(ScribbleSubMenuID.BG_ENGLISH);
        resultList.add(ScribbleSubMenuID.BG_LINE_1_6);
        resultList.add(ScribbleSubMenuID.BG_LINE_2_0);
        resultList.add(ScribbleSubMenuID.BG_LINE_COLUMN);
        resultList.add(ScribbleSubMenuID.BG_TABLE_GRID);
        resultList.add(ScribbleSubMenuID.BG_CALENDAR);
        resultList.add(ScribbleSubMenuID.BG_GRID_POINT);
        return resultList;
    }

    private static List<Integer> buildSubMenuEraserIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(ScribbleSubMenuID.ERASE_PARTIALLY);
        resultList.add(ScribbleSubMenuID.ERASE_TOTALLY);
        return resultList;
    }

    private static List<Integer> buildSubMenuPenStyleIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(ScribbleSubMenuID.NORMAL_PEN_STYLE);
        resultList.add(ScribbleSubMenuID.BRUSH_PEN_STYLE);
        resultList.add(ScribbleSubMenuID.LINE_STYLE);
        resultList.add(ScribbleSubMenuID.TRIANGLE_STYLE);
        resultList.add(ScribbleSubMenuID.CIRCLE_STYLE);
        resultList.add(ScribbleSubMenuID.RECT_STYLE);
        resultList.add(ScribbleSubMenuID.TRIANGLE_45_STYLE);
        resultList.add(ScribbleSubMenuID.TRIANGLE_60_STYLE);
        resultList.add(ScribbleSubMenuID.TRIANGLE_90_STYLE);
        return resultList;
    }

    private static void buildMainMenuIDIconSparseArray() {
        sMainMenuItemIDIconSparseArray = new SparseIntArray();
        sMainMenuItemIDIconSparseArray.put(ScribbleMainMenuID.PEN_STYLE, R.drawable.ic_shape);
        sMainMenuItemIDIconSparseArray.put(ScribbleMainMenuID.BG, R.drawable.ic_template);
        sMainMenuItemIDIconSparseArray.put(ScribbleMainMenuID.ERASER, R.drawable.ic_eraser);
        sMainMenuItemIDIconSparseArray.put(ScribbleMainMenuID.PEN_WIDTH, R.drawable.ic_width);
    }

    private static void buildSubMenuIDIconSparseArray() {
        sSubMenuItemIDIconSparseArray = new SparseIntArray();
        buildSubMenuThicknessIDIconSparseArray();
        buildSubMenuEraseIDIconSparseArray();
        buildSubMenuPenStyleIDIconSparseArray();
        buildBGIDIconSparseArray();
    }

    private static void buildSubMenuThicknessIDIconSparseArray() {
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.THICKNESS_ULTRA_LIGHT, R.drawable.ic_width_1);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.THICKNESS_LIGHT, R.drawable.ic_width_2);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.THICKNESS_NORMAL, R.drawable.ic_width_3);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.THICKNESS_BOLD, R.drawable.ic_width_4);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.THICKNESS_ULTRA_BOLD, R.drawable.ic_width_5);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.THICKNESS_CUSTOM_BOLD, R.drawable.ic_width_6);
    }

    private static void buildSubMenuEraseIDIconSparseArray() {
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.ERASE_PARTIALLY, R.drawable.ic_eraser_part);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.ERASE_TOTALLY, R.drawable.ic_eraser_all);
    }

    private static void buildSubMenuPenStyleIDIconSparseArray() {
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.NORMAL_PEN_STYLE, R.drawable.ic_shape_pencil);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BRUSH_PEN_STYLE, R.drawable.ic_shape_brush);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.LINE_STYLE, R.drawable.ic_shape_line);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.TRIANGLE_STYLE, R.drawable.ic_shape_triangle);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.CIRCLE_STYLE, R.drawable.ic_shape_circle);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.RECT_STYLE, R.drawable.ic_shape_square);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.TRIANGLE_45_STYLE, R.drawable.ic_shape_triangle_45);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.TRIANGLE_60_STYLE, R.drawable.ic_shape_triangle_60);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.TRIANGLE_90_STYLE, R.drawable.ic_shape_triangle_90);
    }

    private static void buildBGIDIconSparseArray() {
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_EMPTY, R.drawable.ic_template_white);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_LINE, R.drawable.ic_template_line);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_LEFT_GRID, R.drawable.ic_template_left_grid);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_GRID_5_5, R.drawable.ic_template_grid_5_5);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_GRID, R.drawable.ic_template_grid);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_MATS, R.drawable.ic_template_mats);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_MUSIC, R.drawable.ic_template_music);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_ENGLISH, R.drawable.ic_template_english);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_LINE_1_6, R.drawable.ic_template_line_1_6);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_LINE_2_0, R.drawable.ic_template_line_2_0);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_LINE_COLUMN, R.drawable.ic_template_line_column);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_TABLE_GRID, R.drawable.ic_template_table_grid);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_CALENDAR, R.drawable.ic_template_line_calendar);
        sSubMenuItemIDIconSparseArray.put(ScribbleSubMenuID.BG_GRID_POINT, R.drawable.ic_template_grid_point);
    }

    public static @IdRes
    int getMainItemIDIconRes(@ScribbleMainMenuID.ScribbleMainMenuDef int mainMenuID) {
        if (sMainMenuItemIDIconSparseArray == null || sSubMenuItemIDIconSparseArray == null) {
            buildIDIconSparseArray();
        }
        return sMainMenuItemIDIconSparseArray.get(mainMenuID);
    }

    public static @IdRes
    int getSubItemIDIconRes(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        if (sMainMenuItemIDIconSparseArray == null || sSubMenuItemIDIconSparseArray == null) {
            buildIDIconSparseArray();
        }
        return sSubMenuItemIDIconSparseArray.get(subMenuID);
    }

    public static List<Integer> getSubMenuIDList(@ScribbleMainMenuID.ScribbleMainMenuDef int mainMenuID) {
        if (sMainMenuContainSubMenuIDListSparseArray == null) {
            buildMainMenuSubMenuIDListSparseArray();
        }
        return sMainMenuContainSubMenuIDListSparseArray.get(mainMenuID);
    }

}
