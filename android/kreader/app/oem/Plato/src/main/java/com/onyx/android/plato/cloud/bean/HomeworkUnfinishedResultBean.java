package com.onyx.android.plato.cloud.bean;

import java.util.List;

/**
 * Created by hehai on 17-10-9.
 */

public class HomeworkUnfinishedResultBean {
    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        public int page;
        public int size;
        public String status;
        public List<ContentBean> content;
    }
}
