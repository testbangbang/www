package com.onyx.reader.host.wrapper;

import com.onyx.reader.api.*;
import com.onyx.reader.plugins.adobe.AdobeReaderPlugin;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderHelper {

    public ReaderPlugin plugin;
    public ReaderDocument document;
    public ReaderNavigator navigator;
    public ReaderRenderer renderer;
    public ReaderScalingManager scalingManager;
    public ReaderSearchManager searchManager;

    public void loadPlugin(final String path) {
        plugin = new AdobeReaderPlugin();
    }

}
