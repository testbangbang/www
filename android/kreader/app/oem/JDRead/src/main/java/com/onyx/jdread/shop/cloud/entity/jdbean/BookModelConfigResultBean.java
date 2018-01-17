package com.onyx.jdread.shop.cloud.entity.jdbean;

import android.databinding.BaseObservable;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jackdeng on 2018/1/10.
 */

public class BookModelConfigResultBean {

    public DataBean data;
    public int resultCode;
    public String message;

    public static class DataBean {
        public HashMap<Integer,AdvBean> adv;
        public HashMap<Integer,ResultBookBean> ebook;
        public List<ModulesBean> modules;

        public static class AdvBean extends BaseObservable{
            public int id;
            public String show_name;
            public String pic_address;
            public int relate_type;
            public Object relate_link;
            public String spread;
            public String upload_domain_name;
            public String pic_address_all;
        }

        public static class ModulesBean {
            public int id;
            public int module_type;
            public int f_type;
            public String show_name;
            public String pic_address;
            public int show_type;
            public String show_info;
            public String note;
            public List<ItemsBean> items;
            public List<ResultBookBean> bookList;

            public static class ItemsBean {
                public int id;
                public String type;
            }
        }
    }
}