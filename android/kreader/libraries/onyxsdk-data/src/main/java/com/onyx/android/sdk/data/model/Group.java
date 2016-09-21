package com.onyx.android.sdk.data.model;

import java.util.List;

/**
 * Created by suicheng on 2016/9/20.
 */
public class Group {
    public long id;
    public String name;
    public String description;
    public long parentId;
    public long creatorId;
    public int status = 0;

    public List<Group> children;
    public List<Member> members;
}
