package com.onyx.reader.plugins.adobe;

import com.onyx.reader.api.ReaderDocumentPosition;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class AdobeDocumentPositionImpl implements ReaderDocumentPosition {

    private int page;

    public AdobeDocumentPositionImpl(int p) {
        page = p;
    }


    public int getPageNumber() {
        return page;
    }

    /**
     * Get the page name.
     * @return get page name string.
     */
    public String getPageName() {
        return "";
    }

    /**
     * Get persistent string representation of object. For example, plugin may use json to persistent
     * object into json string.
     * @return position persistent representation.
     */
    public String save() {
        return "";
    }

    /**
     * Restore the position from persistent string.
     * @param string the persistent representation.
     */
    public void restore(final String string) {

    }

}
