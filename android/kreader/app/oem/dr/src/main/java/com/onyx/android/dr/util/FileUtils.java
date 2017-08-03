package com.onyx.android.dr.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by zhouzhiming on 2017/8/3.
 */
public class FileUtils {

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
