package com.onyx.jdread.personal.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by li on 2018/1/30.
 */

public class PersonalNoteBean {
    public String result_code;
    public DataBean data;
    public String message;

    public static class DataBean {
        public int total;
        public int total_page;
        public List<NoteBean> items;
    }
}
