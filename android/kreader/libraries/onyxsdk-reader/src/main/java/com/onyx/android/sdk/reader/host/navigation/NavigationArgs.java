package com.onyx.android.sdk.reader.host.navigation;

import android.graphics.RectF;
import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.cropimage.data.PointMatrix;
import com.onyx.android.sdk.reader.host.math.PageUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 10/19/15.
 */
public class NavigationArgs {

    public Type type;
    public Map<Type, NavigationList> map = new HashMap();
    private boolean autoCropForEachBlock = false;

    public enum Type {
        ALL, ODD, EVEN,
    }

    public boolean isAutoCropForEachBlock() {
        return autoCropForEachBlock;
    }

    public void setAutoCropForEachBlock(boolean autoCropForEachBlock) {
        this.autoCropForEachBlock = autoCropForEachBlock;
    }

    public NavigationList rowsLeftToRight(final Type t, PointMatrix pointMatrix, final RectF limit) {
        NavigationList list = NavigationList.rowsLeftToRight(pointMatrix, limit);
        type = t;
        map.put(type, list);
        return list;
    }

    public NavigationList rowsLeftToRight(final Type t, int rows, int cols, final RectF limit) {
        NavigationList list = NavigationList.rowsLeftToRight(rows, cols, limit);
        type = t;
        map.put(type, list);
        return list;
    }

    public NavigationList rowsRightToLeft(final Type t, PointMatrix pointMatrix, final RectF limit) {
        NavigationList list = NavigationList.rowsRightToLeft(pointMatrix, limit);
        type = t;
        map.put(type, list);
        return list;
    }

    public NavigationList rowsRightToLeft(final Type t, int rows, int cols, final RectF limit) {
        NavigationList list = NavigationList.rowsRightToLeft(rows, cols, limit);
        type = t;
        map.put(type, list);
        return list;
    }

    public NavigationList columnsLeftToRight(final Type t, PointMatrix pointMatrix, final RectF limit) {
        NavigationList list = NavigationList.columnsLeftToRight(pointMatrix, limit);
        type = t;
        map.put(type, list);
        return list;
    }

    public NavigationList columnsLeftToRight(final Type t, int rows, int cols, final RectF limit) {
        NavigationList list = NavigationList.columnsLeftToRight(rows, cols, limit);
        type = t;
        map.put(type, list);
        return list;
    }

    public NavigationList columnsRightToLeft(final Type t, PointMatrix pointMatrix, final RectF limit) {
        NavigationList list = NavigationList.columnsRightToLeft(pointMatrix, limit);
        type = t;
        map.put(type, list);
        return list;
    }

    public NavigationList columnsRightToLeft(final Type t, int rows, int cols, final RectF limit) {
        NavigationList list = NavigationList.columnsRightToLeft(rows, cols, limit);
        type = t;
        map.put(type, list);
        return list;
    }

    public NavigationArgs() {
    }

    public Type getType() {
        return type;
    }

    public Map<Type, NavigationList> getMap() {
        return map;
    }

    @JSONField(serialize = false)
    public NavigationList getList() {
        return map.get(type);
    }

    public NavigationList getListByType(final Type t) {
        NavigationList list = map.get(t);
        if (list == null) {
            list = map.get(Type.ALL);
        }
        return list;
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
