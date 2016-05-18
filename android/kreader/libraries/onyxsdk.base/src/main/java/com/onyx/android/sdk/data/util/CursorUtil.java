package com.onyx.android.sdk.data.util;

import android.database.Cursor;

/**
 * Created by joy on 12/8/14.
 */
public class CursorUtil {
    private static final String TAG = CursorUtil.class.getSimpleName();

    /**
     * return null if failed
     *
     * @param cursor
     * @param columnIndex
     * @return
     */
    public static String getString(Cursor cursor, int columnIndex) {
        if (!isValidColumnIndex(cursor, columnIndex)) {
            return null;
        }
        return cursor.getString(columnIndex);
    }

    /**
     * return null if failed
     *
     * @param cursor
     * @param columnIndex
     * @return
     */
    public static Integer getInt(Cursor cursor, int columnIndex) {
        if (!isValidColumnIndex(cursor, columnIndex)) {
            return null;
        }
        return cursor.getInt(columnIndex);
    }

    /**
     * return null if failed
     *
     * @param cursor
     * @param columnIndex
     * @return
     */
    public static Long getLong(Cursor cursor, int columnIndex) {
        if (!isValidColumnIndex(cursor, columnIndex)) {
            return null;
        }
        return cursor.getLong(columnIndex);
    }

    public static byte[] getBlob(Cursor cursor, int columnIndex) {
        if (!isValidColumnIndex(cursor, columnIndex)) {
            return null;
        }
        return cursor.getBlob(columnIndex);
    }

    private static boolean isValidColumnIndex(Cursor cursor, int columnIndex) {
        return columnIndex >= 0 && !cursor.isNull(columnIndex);
    }
}
