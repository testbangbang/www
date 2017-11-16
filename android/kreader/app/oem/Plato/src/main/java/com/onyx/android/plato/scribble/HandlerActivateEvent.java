package com.onyx.android.plato.scribble;

/**
 * Created by solskjaer49 on 2017/7/20 15:18.
 */

public class HandlerActivateEvent {
    private static final String TAG = HandlerActivateEvent.class.getSimpleName();

    public HandlerActivateEvent(String noteTitle, int currentPage, int totalPage) {
        this.noteTitle = noteTitle;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    private String noteTitle;
    private int currentPage;
    private int totalPage;


}
