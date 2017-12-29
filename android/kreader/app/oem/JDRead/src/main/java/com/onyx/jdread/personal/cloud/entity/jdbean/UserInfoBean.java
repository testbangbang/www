package com.onyx.jdread.personal.cloud.entity.jdbean;

import java.util.List;

public class UserInfoBean {
    private String code;
    private boolean isSchoolBaiTiaoUser;
    private List<UserList> list;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<UserList> getList() {
        return list;
    }

    public void setList(List<UserList> list) {
        this.list = list;
    }

    public boolean getIsSchoolBaiTiaoUser() {
        return isSchoolBaiTiaoUser;
    }

    public void setIsSchoolBaiTiaoUser(boolean isBaiTiao) {
        this.isSchoolBaiTiaoUser = isBaiTiao;
    }
}
