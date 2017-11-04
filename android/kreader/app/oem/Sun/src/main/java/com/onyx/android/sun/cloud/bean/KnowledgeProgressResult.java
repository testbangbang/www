package com.onyx.android.sun.cloud.bean;

import java.util.List;

/**
 * Created by jackdeng on 2017/11/4.
 */

public class KnowledgeProgressResult {
    public int code;
    public String msg;
    public List<DataBean> data;

    public static class DataBean {
        public String KN;
        public int KNId;
        public float process;
    }
}
