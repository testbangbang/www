package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.converter.ListCloudGroupConverter;
import com.onyx.android.sdk.data.converter.ListStringConverter;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.TypeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/6/17.
 */
@Table(database = ContentDatabase.class, allFields = true)
public class CloudGroup extends BaseData {
    public String _id;
    public String name;
    public String parent;
    @Column(typeConverter = ListCloudGroupConverter.class)
    public List<CloudGroup> children;
    @Column(typeConverter = ListStringConverter.class)
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
