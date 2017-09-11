package com.onyx.android.dr.event;

/**
 * Created by hehai on 17-9-8.
 */

public class BookDetailEvent {
    private String bookId;

    public BookDetailEvent(String cloudId) {
        this.bookId = cloudId;
    }

    public String getBookId() {
        return bookId;
    }
}
