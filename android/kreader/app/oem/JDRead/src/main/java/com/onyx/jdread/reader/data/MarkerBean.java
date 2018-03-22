package com.onyx.jdread.reader.data;

import java.util.List;

/**
 * Created by li on 2018/3/21.
 */

public class MarkerBean {
    public int format;
    public int ebook_id;
    public String import_book_id;
    public int version;
    public List<ListBean> list;

    public static class ListBean {
        public int id;
        public String action;
        public int data_type;
        public String created_at;
        public int offset_in_para;
        public int force;
        public int offset;
        public String quote_text;
        public int para_idx;
        public int pdf_scaling;
        public int pdf_scaling_left;
        public int pdf_scaling_top;
        public int chapter_id;
        public String epub_chapter_title;
        public String percent;
    }
}
