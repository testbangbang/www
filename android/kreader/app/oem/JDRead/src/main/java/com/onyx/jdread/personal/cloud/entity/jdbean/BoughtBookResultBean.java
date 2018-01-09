package com.onyx.jdread.personal.cloud.entity.jdbean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/1/8.
 */

public class BoughtBookResultBean {
    public int amount;
    public int code;
    public int currentPage;
    public int pageSize;
    public int totalPage;
    public List<JDBook> resultList = new ArrayList<JDBook>();
}
