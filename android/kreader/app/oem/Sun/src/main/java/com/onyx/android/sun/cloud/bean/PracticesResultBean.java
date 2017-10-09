package com.onyx.android.sun.cloud.bean;

import java.util.List;

/**
 * Created by hehai on 17-10-9.
 */

public class PracticesResultBean {
    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {

        public int page;
        public int size;
        public String status;
        public List<ContentBean> content;

        public static class ContentBean {

            public int id;
            public String type;
            public String course;
            public String auth;
            public String title;
            public String deadline;
            public String status;
        }
    }
}
