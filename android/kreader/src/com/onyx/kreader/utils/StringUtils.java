package com.onyx.kreader.utils;


import android.graphics.Rect;
import android.util.Log;
import com.onyx.kreader.text.LayoutRun;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/16/15.
 */
public class StringUtils {

    static public final String UTF16LE = "UTF-16LE";
    static public final String UTF16BE = "UTF-16BE";
    static public final String UTF16 = "UTF-16";

    static public boolean isNullOrEmpty(final String string) {
        return (string == null || string.trim().length() <= 0);
    }

    static public boolean isNotBlank(final String string) {
        return (string != null && string.trim().length() > 0);
    }

    static public String utf16le(final byte [] data) {
        String string = "";
        try {
            string = new String(data, UTF16LE);
        } catch (Exception e) {
        }
        return string.trim();
    }

    static public String utf16(final byte [] data) {
        String string = "";
        try {
            string = new String(data, UTF16);
        } catch (Exception e) {
        }
        return string.trim();
    }

    static public byte[] utf16leBuffer(final String text) {
        byte [] buffer = null;
        try {
            buffer = text.getBytes(UTF16LE);
        } catch (Exception e) {
        }
        return buffer;
    }


}
