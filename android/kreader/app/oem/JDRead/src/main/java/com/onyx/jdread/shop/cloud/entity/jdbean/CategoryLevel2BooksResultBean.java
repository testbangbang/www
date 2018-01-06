package com.onyx.jdread.shop.cloud.entity.jdbean;


import java.util.List;

/**
 * Created by hehai on 17-3-31.
 */

public class CategoryLevel2BooksResultBean {

    public String code;
    public int currentPage;
    public boolean isSuccess;
    public int pageSize;
    public int resultCount;
    public int totalPage;
    public List<Integer> bookIdList;
    public List<ResultBookBean> bookList;
}