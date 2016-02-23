package com.onyx.kreader.host.navigation;

import android.graphics.RectF;
import com.onyx.kreader.host.math.PageUtils;

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

    static public NavigationArgs rowsRightToLeft(final Type type, int rows, int cols, final RectF limit) {
        NavigationArgs navigationArgs = new NavigationArgs(type, NavigationList.rowsRightToLeft(rows, cols, limit));
        return navigationArgs;
    }

    static public NavigationArgs columnsLeftToRight(final Type type, int rows, int cols, final RectF limit) {
        NavigationArgs navigationArgs = new NavigationArgs(type, NavigationList.columnsLeftToRight(rows, cols, limit));
        return navigationArgs;
    }

    static public NavigationArgs columnsRightToLeft(final Type type, int rows, int cols, final RectF limit) {
        NavigationArgs navigationArgs = new NavigationArgs(type, NavigationList.columnsRightToLeft(rows, cols, limit));
        return navigationArgs;
    }

    public NavigationArgs(final Type t, final NavigationList list) {
        type = t;
        map.put(type, list);
    }

    public NavigationList getList() {
        return map.get(type);
    }

    static public RectF rectInViewport(final RectF subScreen, final RectF limit, final RectF viewport) {
        RectF result = new RectF(subScreen);
        if (limit != null) {
            result.intersect(limit);
        }
        float scale = PageUtils.scaleToPage(result.width(), result.height(), viewport.width(), viewport.height());
        float left = (viewport.width() - result.width() * scale) / 2;
        float top = (viewport.height() - result.height() * scale) / 2;
        float right = left + result.width() * scale;
        float bottom = top + result.height() * scale;
        result.set(left, top, right, bottom);
        return result;
    }



}
