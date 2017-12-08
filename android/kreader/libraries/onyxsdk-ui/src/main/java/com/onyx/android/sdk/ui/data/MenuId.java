package com.onyx.android.sdk.ui.data;

/**
 * Created by lxm on 2017/9/1.
 */

public class MenuId {

    public static final int PEN_WIDTH = 0;
    public static final int PEN_STYLE = 1;
    public static final int ERASER = 2;
    public static final int BG = 3;
    public static final int COLOR = 4;
    public static final int DELETE = 5;
    public static final int SPACE = 6;
    public static final int ENTER = 7;
    public static final int KEYBOARD = 8;
    public static final int ADD_PAGE = 9;
    public static final int DELETE_PAGE = 10;
    public static final int PREV_PAGE = 11;
    public static final int NEXT_PAGE = 12;
    public static final int SHAPE_SELECT = 13;
    public static final int PAGE = 14;
    public static final int SCRIBBLE_TITLE = 15;

    public static final int NORMAL_PEN_STYLE = 100;
    public static final int BRUSH_PEN_STYLE = 101;
    public static final int LINE_STYLE = 102;
    public static final int TRIANGLE_STYLE = 103;
    public static final int CIRCLE_STYLE = 104;
    public static final int RECT_STYLE = 105;
    public static final int TRIANGLE_45_STYLE = 106;
    public static final int TRIANGLE_60_STYLE = 107;
    public static final int TRIANGLE_90_STYLE = 108;

    public static final int ERASE_PARTIALLY = 200;
    public static final int ERASE_TOTALLY = 201;

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

    public static final int PEN_COLOR_BLACK = 400;
    public static final int PEN_COLOR_RED = 401;
    public static final int PEN_COLOR_YELLOW = 402;
    public static final int PEN_COLOR_BLUE = 403;
    public static final int PEN_COLOR_GREEN = 404;
    public static final int PEN_COLOR_MAGENTA = 405;

    public static final int THICKNESS_ULTRA_LIGHT = 500;
    public static final int THICKNESS_LIGHT = 501;
    public static final int THICKNESS_NORMAL = 502;
    public static final int THICKNESS_BOLD = 503;
    public static final int THICKNESS_ULTRA_BOLD = 504;
    public static final int THICKNESS_CUSTOM_BOLD = 505;

    public static final int SWITCH_TO_NORMAL_SCRIBBLE_MODE = 1000;
    public static final int SWITCH_TO_SPAN_SCRIBBLE_MODE = 1001;
    public static final int UNDO = 1002;
    public static final int REDO = 1003;
    public static final int SAVE = 1004;
    public static final int SETTING = 1005;
    public static final int EXPORT = 1006;

    private static final int SUB_MENU_RANGE = 99;
    private static final int MAX_NOTE_SUB_MENU_ID = 999;

    public static boolean isSubMenuId(int menuID) {
        return menuID >= NORMAL_PEN_STYLE && menuID <= MAX_NOTE_SUB_MENU_ID;
    }

    public static boolean isThicknessGroup(int menuID) {
        return menuID >= THICKNESS_ULTRA_LIGHT && menuID <= THICKNESS_ULTRA_LIGHT + SUB_MENU_RANGE;
    }

    public static boolean isPenStyleGroup(int menuID) {
        return menuID >= NORMAL_PEN_STYLE && menuID <= NORMAL_PEN_STYLE + SUB_MENU_RANGE;
    }

    public static boolean isEraserGroup(int menuID) {
        return menuID >= ERASE_PARTIALLY && menuID <= ERASE_PARTIALLY + SUB_MENU_RANGE;
    }

    public static boolean isBackgroundGroup(int menuID) {
        return menuID >= BG_EMPTY && menuID <= BG_EMPTY + SUB_MENU_RANGE;
    }

    public static boolean isPenColorGroup(int menuID) {
        return menuID >= PEN_COLOR_BLACK && menuID <= PEN_COLOR_BLACK + SUB_MENU_RANGE;
    }
}
