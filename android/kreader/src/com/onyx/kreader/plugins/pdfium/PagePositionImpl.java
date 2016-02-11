package com.onyx.kreader.plugins.pdfium;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.kreader.api.ReaderPagePosition;


/**
 * Created by zhuzeng on 10/5/15.
 */
public class PagePositionImpl implements ReaderPagePosition {

    private int pageNumber;
    private String internal;

    @JSONField(serialize = false)
    private PdfiumReaderPlugin parent;

    public static PagePositionImpl createFromInternalString(final PdfiumReaderPlugin p, final String s) {
        int pn = 0;
        return new PagePositionImpl(p, pn, s);
    }

    public static PagePositionImpl createFromPersistentString(final PdfiumReaderPlugin p, final String s) {
        PagePositionImpl impl = JSON.parseObject(s, PagePositionImpl.class);
        if (impl != null) {
            impl.parent = p;
        }
        return impl;
    }

    public static PagePositionImpl createFromPageNumber(final PdfiumReaderPlugin p, int pn) {
        return new PagePositionImpl(p, pn, null);
    }

    public PagePositionImpl() {
        pageNumber = -1;
    }

    public PagePositionImpl(final PdfiumReaderPlugin p, int pn, final String s) {
        parent = p;
        pageNumber = pn;
        internal = s;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pn) {
        pageNumber = pn;
    }

    public void setInternal(final String str) {
        internal = str;
    }

    public final String getInternal() {
        return internal;
    }

    public String asString() {
        return JSON.toJSONString(this);
    }

    public boolean fromrString(final String string) {
        PagePositionImpl impl = JSON.parseObject(string, PagePositionImpl.class);
        if (impl != null) {
            pageNumber = impl.getPageNumber();
            internal = impl.internal;
            return true;
        }
        return false;
    }

    public int compare(final ReaderPagePosition another) {
        return 0;
    }

}
