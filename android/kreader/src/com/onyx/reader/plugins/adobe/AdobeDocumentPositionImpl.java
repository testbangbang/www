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

    public String getPageName() {
        return "";
    }

    public String save() {
        return "";
    }

    public void restore(final String string) {

    }

    public int compare(final ReaderDocumentPosition another) {
        return 0;
    }

}
