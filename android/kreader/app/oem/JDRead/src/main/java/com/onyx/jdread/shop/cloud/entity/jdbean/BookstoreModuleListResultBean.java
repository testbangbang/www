package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by hehai on 17-3-30.
 */

public class BookstoreModuleListResultBean {
    public String code;
    public String message;
    public List<MainThemeListBean> mainThemeList;

    public static class MainThemeListBean {

        public String created;
        public String createdStr;
        public String creator;
        public int id;
        public int isShow;
        public String modified;
        public String modifiedStr;
        public String showName;
        public int sort;
        public int status;
        public String statusStr;
        public int sysId;
        public List<ModulesBean> modules;
    }
}
