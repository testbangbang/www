package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/6/17.
 */

public class CloudGroup extends BaseData {
    public String _id;
    public String name;
    public String parent;
    public List<CloudGroup> children;
    public List<String> ancestors;
    public String library;
    public int uniqueTypeId;

    public static String joinGroupListName(List<CloudGroup> groupList) {
        return joinGroupListName(groupList, DELIMITER);
    }

    public static String joinGroupListName(List<CloudGroup> groupList, String delimiter) {
        if (CollectionUtils.isNullOrEmpty(groupList)) {
            return null;
        }
        List<String> nameList = new ArrayList<>();
        for (CloudGroup group : groupList) {
            nameList.add(group.name);
        }
        return StringUtils.join(nameList, delimiter);
    }
}
