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
