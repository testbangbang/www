package com.onyx.android.sdk.data;

import java.io.Serializable;

/**
 * Created by zhuzeng on 27/03/2017.
 */

public class QueryPagination extends GPaginator implements Serializable {


    public static QueryPagination create(int rows, int cols) {
        final QueryPagination pagination = new QueryPagination(rows, cols);
        return pagination;
    }

    private QueryPagination(int rows, int cols) {
        super(rows, cols, 0);
    }



}
