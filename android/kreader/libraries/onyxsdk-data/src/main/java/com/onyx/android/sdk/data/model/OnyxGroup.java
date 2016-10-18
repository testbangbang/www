package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.converter.ListGroupConverter;
import com.onyx.android.sdk.data.converter.ListMemberConverter;
import com.onyx.android.sdk.data.db.OnyxCloudDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

/**
 * Created by suicheng on 2016/9/23.
 */
@Table(database = OnyxCloudDatabase.class)
public class OnyxGroup extends BaseData {
    @Column
    public String name;
    @Column
    public String description;
    @Column
    public String parentGuid;
    @Column
    public String creatorId;
    @Column
    public int status = 0;

    @Column(typeConverter = ListGroupConverter.class)
    public List<OnyxGroup> children;
    @Column(typeConverter = ListMemberConverter.class)
    public List<Member> members;

    public OnyxGroup() {
    }

    public OnyxGroup(String name) {
        this.name = name;
    }
}
