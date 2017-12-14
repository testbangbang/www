package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by hehai on 17-3-30.
 */

public class BookstoreModelResultBean {

    public String code;
    public String message;
    public ModelBookChildBean moduleBookChild;
    public List<ResultBookBean> resultList;

    public static class ModelBookChildBean {

        public int fid;
        public String modified;
        public String showInfo;
        public int id;
        public int showForm;
        public int showNum;
        public String showName;
        public String created;
        public int showType;
        public int ftype;
        public String note;
        public int isShow;
    }
}
