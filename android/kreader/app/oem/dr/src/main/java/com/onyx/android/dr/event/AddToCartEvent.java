package com.onyx.android.dr.event;

/**
 * Created by hehai on 17-9-12.
 */

public class AddToCartEvent {
    private String bookId;

    public AddToCartEvent(String cloudId) {
        this.bookId = cloudId;
    }

    public String getBookId() {
        return bookId;
    }
}
