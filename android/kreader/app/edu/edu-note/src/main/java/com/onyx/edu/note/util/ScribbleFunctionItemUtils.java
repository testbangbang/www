package com.onyx.edu.note.util;

import android.support.annotation.IdRes;
import android.util.SparseIntArray;

import com.onyx.edu.note.R;
import com.onyx.edu.note.data.ScribbleMainMenuID;
import com.onyx.edu.note.data.ScribbleSubMenuID;

/**
 * Created by solskjaer49 on 2017/7/11 16:28.
 */

public class ScribbleFunctionItemUtils {
    //TODO:here store icon res only,if one day need support string,use SparseArray<E> instead.
    private static SparseIntArray sMainMenuItemIDIconSparseArray;
    private static SparseIntArray sSubMenuItemIDIconSparseArray;

    //TODO:temp build here.if custom needed can be add config in json.
    private static void buildIDIconSparseArray() {
        buildMainMenuIDIconSparseArray();
        buildSubMenuIDIconSparseArray();
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
//        sSubMenuItemIDIconSparseArray.put(ScribbleMainMenuID.PEN_STYLE, R.drawable.);
//        sSubMenuItemIDIconSparseArray.put(ScribbleMainMenuID.BG, R.drawable.);
//        sSubMenuItemIDIconSparseArray.put(ScribbleMainMenuID.ERASER, R.drawable.);
//        sSubMenuItemIDIconSparseArray.put(ScribbleMainMenuID.PEN_WIDTH, R.drawable.);
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


}
