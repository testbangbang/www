package com.onyx.android.plato.cloud.bean;

import java.util.List;

/**
 * Created by li on 2017/10/10.
 */

public class HomeworkFinishedResultBean {
    public int code;
    public String msg;
    public FinishData data;

    static public class FinishData {
        public int page;
        public int size;
        public String status;
        public List<FinishContent> content;
    }
}
