package com.onyx.android.sdk.data.model;

/**
 * Created by zhouzhiming on 2017/9/15.
 */
public class DeleteGroupMemberBean {
    public String message;
    private String[] users;
    private String[] groups;

    public String[] getUsers() {
        return users;
    }

    public void setUsers(String[] users) {
        this.users = users;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }
}
