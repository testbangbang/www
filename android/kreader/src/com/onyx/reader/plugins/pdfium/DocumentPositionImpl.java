package com.onyx.reader.plugins.pdfium;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.reader.api.ReaderDocumentPosition;




/**
 * Created by zhuzeng on 10/5/15.
 */
public class DocumentPositionImpl implements ReaderDocumentPosition {

    private int pageNumber;
    private String internal;

    @JSONField(serialize = false)
    private PdfiumReaderPlugin parent;

    public static DocumentPositionImpl createFromInternalString(final PdfiumReaderPlugin p, final String s) {
        int pn = 0;
        return new DocumentPositionImpl(p, pn, s);
    }

    public static DocumentPositionImpl createFromPersistentString(final PdfiumReaderPlugin p, final String s) {
        DocumentPositionImpl impl = JSON.parseObject(s, DocumentPositionImpl.class);
        if (impl != null) {
            impl.parent = p;
        }
        return impl;
    }

    public static DocumentPositionImpl createFromPageNumber(final PdfiumReaderPlugin p, int pn) {
        return new DocumentPositionImpl(p, pn, null);
    }

    public DocumentPositionImpl() {
        pageNumber = -1;
    }

    public DocumentPositionImpl(final PdfiumReaderPlugin p, int pn, final String s) {
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

    public String save() {
        return JSON.toJSONString(this);
    }

    public boolean restore(final String string) {
        DocumentPositionImpl impl = JSON.parseObject(string, DocumentPositionImpl.class);
        if (impl != null) {
            pageNumber = impl.getPageNumber();
            internal = impl.internal;
            return true;
        }
        return false;
    }

    public int compare(final ReaderDocumentPosition another) {
        return 0;
    }

}
