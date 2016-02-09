package com.onyx.kreader.host.wrapper;

import android.content.Context;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.api.ReaderViewOptions;

/**
 * Created by zhuzeng on 10/5/15.
 * Map between document and corresponding reader.
 */
public class ReaderManager {


    public static Reader createReader(final Context context, final String path, final ReaderPluginOptions pluginOptions, final ReaderViewOptions viewOptions) {
        Reader reader = new Reader(pluginOptions, viewOptions);
        reader.getReaderHelper().loadPlugin(context, path);
        return reader;
    }

}
