package com.onyx.jdread.shop.event;

/**
 * Created by jackdeng on 2018/1/9.
 */

public class ViewAllClickEvent {
    public int fid;
    public String subjectName;
    public ViewAllClickEvent(int fid, String subjectName) {
        this.fid = fid;
        this.subjectName = subjectName;
    }
}