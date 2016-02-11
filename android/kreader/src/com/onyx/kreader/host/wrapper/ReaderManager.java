package com.onyx.kreader.host.wrapper;

import android.content.Context;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.api.ReaderViewOptions;

import java.util.HashMap;

/**
 * Created by zhuzeng on 10/5/15.
 * Map between document and corresponding reader.
 */
public class ReaderManager {

    static HashMap<String, Reader> readerHashMap = new HashMap<String, Reader>();

    public static Reader getReader(final String path) {
        if (readerHashMap.containsKey(path)) {
            return readerHashMap.get(path);
        }
        return null;
    }

    public static boolean releaseReader(final String path) {
        if (readerHashMap.containsKey(path)) {
            readerHashMap.remove(path);
            return true;
        }
        return false;
    }

    public static Reader createReader(final Context context, final String path, final ReaderPluginOptions pluginOptions, final ReaderViewOptions viewOptions) {
        Reader reader = new Reader();
        reader.init(pluginOptions, viewOptions);
        reader.getReaderHelper().selectPlugin(context, path);
        readerHashMap.put(path, reader);
        return reader;
    }

}
