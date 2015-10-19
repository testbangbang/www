package com.onyx.reader.host.navigation;

import com.onyx.reader.api.ReaderException;

/**
 * Created by zhuzeng on 10/18/15.
 */
public class NavigationProviderBase {

    public boolean prev()  throws ReaderException {
        return false;
    }

    public boolean next() throws ReaderException {
        return false;
    }

    public boolean first() throws ReaderException {
        return false;
    }

    public boolean last() throws ReaderException {
        return false;
    }

    public boolean navigateTo(int index) throws ReaderException {
        return false;
    }

    public boolean pan(int dx, int dy) {
        return false;
    }

}
