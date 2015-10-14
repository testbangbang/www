package com.onyx.reader.plugins.utils;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class PluginUtils {

    static public boolean isNullOrEmpty(final String string) {
        return (string == null || string.length() <= 0);
    }

    static public boolean isNonBlank(final String string) {
        return (string != null && string.length() > 0);
    }


    public static List<RectF> rectangles(double result[]) {
        if (result == null || result.length <= 0) {
            return null;
        }

        int size = result.length / 4;
        List<RectF> list = new ArrayList<RectF>(size);
        for(int i = 0; i < size; ++i) {
            float left = (float)result[i * 4];
            float top = (float)result[i * 4 + 1];
            float width = (float)result[i * 4 + 2];
            float height = (float)result[i * 4 + 3];
            RectF rect = new RectF(left, top, left + width - 1, top + height - 1);
            list.add(rect);
        }
        return list;
    }
}
