package com.onyx.jdread.shop.cloud.entity.jdbean;

import android.databinding.BaseObservable;

import java.util.List;

/**
 * Created by hehai on 17-3-31.
 */

public class CategoryListResultBean {
    public String code;
    public List<CatListBean> catList;

    public static class CatListBean extends BaseObservable{

        public int amount;
        public int catId;
        public String catName;
        public int catType;
        public int isLeaf;
        public String shortName;
        public List<ChildListBean> childList;

        public static class ChildListBean {

            public int amount;
            public int catId;
            public String catName;
            public int catType;
            public int isLeaf;
        }

        public void onItemClicked() {

        }
    }
}
