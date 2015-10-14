package com.onyx.reader.plugins.adobe;

import com.onyx.reader.api.ReaderDocumentPosition;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class AdobeDocumentPositionImpl implements ReaderDocumentPosition {

    private int page;
    private String internal;

    public AdobeDocumentPositionImpl(int p) {
        page = p;
    }

    public AdobeDocumentPositionImpl(final String string) {
        page = -1;
        internal = string;
    }

    public int getPageNumber() {
        return page;
    }

    public String getPageName() {
        return save();
    }

    public String save() {
        return String.valueOf(page);
    }

    public void restore(final String string) {
        page = Integer.parseInt(string);
    }

    public int compare(final ReaderDocumentPosition another) {
        return 0;
    }

}
