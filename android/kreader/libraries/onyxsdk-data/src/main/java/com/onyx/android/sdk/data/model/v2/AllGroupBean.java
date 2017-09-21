package com.onyx.android.sdk.data.model.v2;

import java.util.List;

/**
 * Created by hehai on 17-7-28.
 */

public class AllGroupBean {
    public String _id;
    public String library;
    public String updatedAt;
    public String createdAt;
    public String name;
    public int applyCount;
    public int devicesCount;
    public int usersCount;
    public String role;
    public String parent;
    public List<String> ancestors;
    public List<String> admin;
}
