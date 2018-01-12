package com.onyx.jdread.shop.cloud.entity.jdbean;

import android.databinding.BaseObservable;

import java.util.List;

/**
 * Created by hehai on 17-3-31.
 */

public class CategoryListResultBean {
    public int result_code;
    public String message;
    public List<CategoryBeanLevelOne> data;

    public static class CategoryBeanLevelOne {
        public int id;
        public String name;
        public String image_url;
        public List<CategoryBeanLevelTwo> sub_category;

        public static class CategoryBeanLevelTwo extends BaseObservable {
            public int id;
            public String name;
            public String image_url;
            public List<CategoryBeanLevelTwo> sub_category;
        }
    }

    public String code;

    public static class CatListBean extends BaseObservable {

        public int amount;
        public int catId;
        public String catName;
        public int catType;
        public int isLeaf;
        public String shortName;
        public List<ChildListBean> childList;
        public boolean isSelect;

        public static class ChildListBean {

            public int amount;
            public int catId;
            public String catName;
            public int catType;
            public int isLeaf;
        }
    }
}
