package com.onyx.android.sdk.data.model.v2;

/**
 * Created by zhouzhiming on 2017/9/15.
 */
public class JoinGroupBean {
    public String updatedAt;
    public String createdAt;
    public String user;
    public String group;
    public String _id;
    public int status;
    private String[] groups;

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }
}
