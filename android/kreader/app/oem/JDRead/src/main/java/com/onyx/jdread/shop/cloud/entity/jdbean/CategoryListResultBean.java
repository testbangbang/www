package com.onyx.jdread.shop.cloud.entity.jdbean;

import android.databinding.BaseObservable;

import java.util.List;

/**
 * Created by hehai on 17-3-31.
 */

public class CategoryListResultBean {
    public int result_Code;
    public String message;
    public List<CategoryBeanLevelOne> data;

    public static class CategoryBeanLevelOne {
        public int id;
        public String name;
        public String image_url;
        public int level;
        public int type;
        public List<CategoryBeanLevelTwo> sub_category;

        public static class CategoryBeanLevelTwo extends BaseObservable {
            public int id;
            public String name;
            public String image_url;
            public int level;
            public int type;
            public boolean isSelect;
            public List<CategoryBeanLevelTwo> sub_category;
        }
    }
}
