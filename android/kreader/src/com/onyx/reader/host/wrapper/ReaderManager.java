package com.onyx.reader.host.wrapper;

import com.onyx.reader.api.ReaderPluginOptions;
import com.onyx.reader.api.ReaderViewOptions;

/**
 * Created by zhuzeng on 10/5/15.
 * Map between document and corresponding reader.
 */
public class ReaderManager {


    public static Reader createReader(final String path, final ReaderPluginOptions pluginOptions, final ReaderViewOptions viewOptions) {
        Reader reader = new Reader();
        reader.getReaderHelper().pluginOptions = pluginOptions;
        reader.getReaderHelper().viewOptions = viewOptions;
        reader.getReaderHelper().updateRenderBitmap(viewOptions.getViewWidth(), viewOptions.getViewHeight());
        reader.getReaderHelper().loadPlugin(path);
        return reader;
    }

}
