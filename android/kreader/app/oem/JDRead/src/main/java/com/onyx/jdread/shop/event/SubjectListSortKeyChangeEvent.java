package com.onyx.jdread.shop.event;

/**
 * Created by jackdeng on 2018/1/3.
 */

public class SubjectListSortKeyChangeEvent {
    public int sortKey;

    public SubjectListSortKeyChangeEvent(int sortKey) {
        this.sortKey = sortKey;
    }
}
