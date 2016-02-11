package com.onyx.kreader.plugins.adobe;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.kreader.api.ReaderPagePosition;




/**
 * Created by zhuzeng on 10/5/15.
 */
public class AdobePagePositionImpl implements ReaderPagePosition {

    private int pageNumber;
    private String internal;

    @JSONField(serialize = false)
    private AdobeReaderPlugin parent;

    public static AdobePagePositionImpl createFromInternalString(final AdobeReaderPlugin p, final String s) {
        int pn = p.getPluginImpl().getPageNumberByLocationNative(s);
        return new AdobePagePositionImpl(p, pn, s);
    }

    public static AdobePagePositionImpl createFromPersistentString(final AdobeReaderPlugin p, final String s) {
        AdobePagePositionImpl impl = JSON.parseObject(s, AdobePagePositionImpl.class);
        if (impl != null) {
            impl.parent = p;
        }
        return impl;
    }

    public static AdobePagePositionImpl createFromPageNumber(final AdobeReaderPlugin p, int pn) {
        return new AdobePagePositionImpl(p, pn, null);
    }

    public AdobePagePositionImpl() {
        pageNumber = -1;
    }

    public AdobePagePositionImpl(final AdobeReaderPlugin p, int pn, final String s) {
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
        AdobePagePositionImpl impl = JSON.parseObject(string, AdobePagePositionImpl.class);
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
