package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by hehai on 17-3-30.
 */

public class BookModelResultBean {

    public int amount;
    public String code;
    public int currentPage;
    public long currentTime;
    public long endTime;
    public String message;
    public int pageSize;
    public int totalPage;
    public ModelBookChildBean moduleBookChild;
    public List<ResultBookBean> resultList;

    public static class ModelBookChildBean {
        public int fid;
        public String picAddress1All;
        public int sort;
        public String picAddress;
        public String picAddress1;
        public String appLink;
        public int showMoreStatus;
        public String creator;
        public String modified;
        public String showInfo;
        public int id;
        public int showNum;
        public String uploadDomainName;
        public String showName;
        public String created;
        public int showMoreType;
        public int limitTime;
        public int showType;
        public int ftype;
        public String picAddressAll;
        public String note;
        public int showForm;
        public int isShow;
    }
}
