package com.onyx.android.sdk.scribble.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PageNameList {

    private List<String> pageNameList = new ArrayList<String>();

    public PageNameList() {
    }

    public void add(final String name) {
        pageNameList.add(name);
    }

    public void remove(final String name) {
        pageNameList.remove(name);
    }

    public int size() {
        return pageNameList.size();
    }

    public String get(int i) {
        return pageNameList.get(i);
    }

    public void addAll(final List<String> list) {
        pageNameList.addAll(list);
    }

    public void clear() {
        pageNameList.clear();
    }

    public List<String> getPageNameList() {
        return pageNameList;
    }

    public void setPageNameList(final List<String> set) {
        pageNameList = set;
    }

}
