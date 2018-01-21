package com.onyx.jdread.reader.data;

import android.content.Intent;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class NoteInfo {
    public static final String BOOK_NAME = "bookName";
    public String bookName;
    public static final String PAGE_POSITION = "pagePosition";
    public String pagePosition;
    public static final String CHAPTER_NAME = "chapterName";
    public String chapterName;
    public static final String CREATE_DATE = "createDate";
    public String createDate;
    public static final String SRC_NOTE = "srcNote";
    public String srcNote;
    public static final String IS_SRC_NOTE_MODIFY = "isSrcNoteModify";
    public boolean isSrcNoteModify;
    public static final String NEW_NOTE = "newNote";
    public String newNote;
    public static final String IS_CREATE = "isCreate";
    public boolean isCreate = false;


    public Intent getIntent() {
        Intent intent = new Intent();
        intent.putExtra(BOOK_NAME, bookName);
        intent.putExtra(PAGE_POSITION, pagePosition);
        intent.putExtra(CHAPTER_NAME, chapterName);
        intent.putExtra(CREATE_DATE, createDate);
        intent.putExtra(SRC_NOTE, srcNote);
        intent.putExtra(IS_SRC_NOTE_MODIFY, isSrcNoteModify);
        intent.putExtra(NEW_NOTE, newNote);
        intent.putExtra(IS_CREATE, isCreate);
        return intent;
    }

    public static NoteInfo parserIntent(Intent intent) {
        NoteInfo noteInfo = new NoteInfo();
        if (!intent.hasExtra(PAGE_POSITION)) {
            return null;
        }
        noteInfo.pagePosition = intent.getStringExtra(PAGE_POSITION);
        if (intent.hasExtra(CHAPTER_NAME)) {
            noteInfo.chapterName = intent.getStringExtra(CHAPTER_NAME);
        }
        if (intent.hasExtra(BOOK_NAME)) {
            noteInfo.bookName = intent.getStringExtra(BOOK_NAME);
        }
        if (intent.hasExtra(CREATE_DATE)) {
            noteInfo.createDate = intent.getStringExtra(CREATE_DATE);
        }
        if (!intent.hasExtra(SRC_NOTE)) {
            return null;
        }
        noteInfo.srcNote = intent.getStringExtra(SRC_NOTE);
        if (intent.hasExtra(IS_SRC_NOTE_MODIFY)) {
            noteInfo.isSrcNoteModify = intent.getBooleanExtra(IS_SRC_NOTE_MODIFY, false);
        }
        if (intent.hasExtra(NEW_NOTE)) {
            noteInfo.newNote = intent.getStringExtra(NEW_NOTE);
        }
        if (intent.hasExtra(IS_CREATE)) {
            noteInfo.isCreate = intent.getBooleanExtra(IS_CREATE, false);
        }
        return noteInfo;
    }
}
