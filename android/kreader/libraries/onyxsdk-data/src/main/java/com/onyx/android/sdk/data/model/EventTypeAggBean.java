package com.onyx.android.sdk.data.model;

/**
 * Created by ming on 2017/2/16.
 */

public class EventTypeAggBean {

    private int total;
    private int read;
    private int finish;
    private int annotation;
    private int lookupDic;
    private int pageChange;
    private int close;
    private int textSelect;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public int getAnnotation() {
        return annotation;
    }

    public void setAnnotation(int annotation) {
        this.annotation = annotation;
    }

    public int getLookupDic() {
        return lookupDic;
    }

    public void setLookupDic(int lookupDic) {
        this.lookupDic = lookupDic;
    }

    public int getPageChange() {
        return pageChange;
    }

    public void setPageChange(int pageChange) {
        this.pageChange = pageChange;
    }

    public int getClose() {
        return close;
    }

    public void setClose(int close) {
        this.close = close;
    }

    public int getTextSelect() {
        return textSelect;
    }

    public void setTextSelect(int textSelect) {
        this.textSelect = textSelect;
    }
}
