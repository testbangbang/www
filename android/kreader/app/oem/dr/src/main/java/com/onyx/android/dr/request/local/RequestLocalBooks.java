package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.BookDetailEntity;
import com.onyx.android.dr.util.Scanner;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.List;

/**
 * Created by hehai on 17-6-26.
 */
public class RequestLocalBooks extends BaseDataRequest {
    private List<BookDetailEntity> books;

    public List<BookDetailEntity> getBooks() {
        return books;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        books = getLocalBooks();
    }

    private List<BookDetailEntity> getLocalBooks() {
        return Scanner.instance().getLocalLibrary();
    }
}
