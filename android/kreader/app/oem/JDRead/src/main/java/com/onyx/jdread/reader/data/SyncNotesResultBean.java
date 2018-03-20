package com.onyx.jdread.reader.data;

import java.util.List;

/**
 * Created by li on 2018/3/20.
 */

public class SyncNotesResultBean {
    public int result_code;
    public String message;
    public List<DataBean> data;

    public static class DataBean {
        public String id;
        public String action;
        public boolean success;
    }
}
