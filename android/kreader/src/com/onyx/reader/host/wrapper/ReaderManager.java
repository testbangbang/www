package com.onyx.reader.host.wrapper;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderManager {




    public static Reader createReader(final String path) {
        Reader reader = new Reader();
        reader.getHelper().loadPlugin(path);
        return reader;
    }

}
