package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by hehai on 17-3-30.
 */

public class BookstoreModuleResultBean {
    public int amount;
    public String code;
    public int currentPage;
    public long currentTime;
    public long endTime;
    public String message;
    public ModuleBookChildBean moduleBookChild;
    public int pageSize;
    public int totalPage;
    public List<ResultBookBean> resultList;

    public static class ModuleBookChildBean {

        public int fid;
        public String picAddress;
        public String picAddress4All;
        public String picAddress3;
        public String picAddress2;
        public String picAddress4;
        public String picAddress2All;
        public String creator;
        public String modified;
        public String showInfo;
        public int id;
        public int showForm;
        public int showNum;
        public String showName;
        public String created;
        public int limitTime;
        public int showType;
        public String picAddress3All;
        public int ftype;
        public String picAddressAll;
        public String note;
        public int isShow;
    }
}
