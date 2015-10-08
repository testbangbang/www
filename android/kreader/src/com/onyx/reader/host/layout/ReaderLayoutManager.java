package com.onyx.reader.host.layout;

import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/7/15.
 */
public class ReaderLayoutManager {
    private Reader reader;

    public ReaderLayoutManager(final Reader r) {
        reader = r;
    }

    public Reader getReader() {
        return reader;
    }

}
