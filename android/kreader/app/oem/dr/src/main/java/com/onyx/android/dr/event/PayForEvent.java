package com.onyx.android.dr.event;

/**
 * Created by hehai on 17-9-9.
 */

public class PayForEvent {
    private String bookId;

    public PayForEvent(String bookId) {
        this.bookId = bookId;
    }

    public String getBookId() {
        return bookId;
    }
}
