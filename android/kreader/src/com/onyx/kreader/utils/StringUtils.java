package com.onyx.kreader.utils;


/**
 * Created by zhuzeng on 10/16/15.
 */
public class StringUtils {

    static public final String UTF16LE = "UTF-16LE";
    static public final String UTF16BE = "UTF-16BE";

    static public boolean isNullOrEmpty(final String string) {
        return (string == null || string.length() <= 0);
    }

    static public boolean isNonBlank(final String string) {
        return (string != null && string.length() > 0);
    }

    static public String utf16le(final byte [] data) {
        String string = "";
        try {
            string = new String(data, UTF16LE);
        } catch (Exception e) {
        }
        return string.trim();
    }

    static public String utf16be(final byte [] data) {
        String string = "";
        try {
            string = new String(data, UTF16BE);
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
