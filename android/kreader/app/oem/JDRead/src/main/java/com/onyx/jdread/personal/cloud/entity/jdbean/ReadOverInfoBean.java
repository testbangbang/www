package com.onyx.jdread.personal.cloud.entity.jdbean;

/**
 * Created by li on 2018/1/2.
 */

public class ReadOverInfoBean {
    private int read_books_count;
    private int notes_count;
    private int share_notes_count;
    private int share_books_count;
    private int reading_length;
    private int book_comments_count;

    public int getRead_books_count() {
        return read_books_count;
    }

    public void setRead_books_count(int read_books_count) {
        this.read_books_count = read_books_count;
    }

    public int getNotes_count() {
        return notes_count;
    }

    public void setNotes_count(int notes_count) {
        this.notes_count = notes_count;
    }

    public int getShare_notes_count() {
        return share_notes_count;
    }

    public void setShare_notes_count(int share_notes_count) {
        this.share_notes_count = share_notes_count;
    }

    public int getShare_books_count() {
        return share_books_count;
    }

    public void setShare_books_count(int share_books_count) {
        this.share_books_count = share_books_count;
    }

    public int getReading_length() {
        return reading_length;
    }

    public void setReading_length(int reading_length) {
        this.reading_length = reading_length;
    }

    public int getBook_comments_count() {
        return book_comments_count;
    }

    public void setBook_comments_count(int book_comments_count) {
        this.book_comments_count = book_comments_count;
    }
}
