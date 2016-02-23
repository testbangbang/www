package com.onyx.kreader.host.navigation;

import android.graphics.RectF;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 10/19/15.
 */
public class NavigationArgs {

    private Type type;
    private Map<Type, NavigationList> map = new HashMap();

    public enum Type {
        ALL, ODD, EVEN,
    }

    static public NavigationArgs rowsLeftToRight(final Type type, int rows, int cols, final RectF limit) {
        NavigationArgs navigationArgs = new NavigationArgs(type, NavigationList.rowsLeftToRight(rows, cols, limit));
        return navigationArgs;
    }

    public NavigationArgs(final Type t, final NavigationList list) {
        type = t;
        map.put(type, list);
    }

    public NavigationList getList() {
        return map.get(type);
    }



}
