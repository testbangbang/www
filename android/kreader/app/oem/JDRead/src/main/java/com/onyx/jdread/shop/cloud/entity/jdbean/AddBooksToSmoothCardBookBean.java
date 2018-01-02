package com.onyx.jdread.shop.cloud.entity.jdbean;


import java.util.List;

/**
 * Created by 12 on 2017/4/6.
 */

public class AddBooksToSmoothCardBookBean {
    public String code;
    public String cardEndTime;
    public String cardNO;
    public List<BookList> ebookList;

    private class BookList {
        public long ebookId;
        public String status;
    }
}
