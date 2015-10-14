package com.onyx.reader.plugins.adobe;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.reader.api.ReaderDocumentPosition;




/**
 * Created by zhuzeng on 10/5/15.
 */
public class AdobeDocumentPositionImpl implements ReaderDocumentPosition {

    private int pageNumber;
    private String internal;

    @JSONField(serialize = false)
    private AdobeReaderPlugin parent;

    public static AdobeDocumentPositionImpl createFromInternalString(final AdobeReaderPlugin p, final String s) {
        int pn = p.getPluginImpl().getPageNumberByLocationNative(s);
        return new AdobeDocumentPositionImpl(p, pn, s);
    }

    public static AdobeDocumentPositionImpl createFromPersistentString(final AdobeReaderPlugin p, final String s) {
        AdobeDocumentPositionImpl impl = JSON.parseObject(s, AdobeDocumentPositionImpl.class);
        if (impl != null) {
            impl.parent = p;
        }
        return impl;
    }

    public static AdobeDocumentPositionImpl createFromPageNumber(final AdobeReaderPlugin p, int pn) {
        return new AdobeDocumentPositionImpl(p, pn, null);
    }

    public AdobeDocumentPositionImpl() {
        pageNumber = -1;
    }

    public AdobeDocumentPositionImpl(final AdobeReaderPlugin p, int pn, final String s) {
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
        AdobeDocumentPositionImpl impl = JSON.parseObject(string, AdobeDocumentPositionImpl.class);
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
