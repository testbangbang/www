package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.db.OnyxCloudDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by zhuzeng on 11/20/15.
 */
@Table(database = OnyxCloudDatabase.class)
public class Category extends BaseData {

    static public final String NAME_TAG = "name";
    static public final String VALUE_TAG = "value";

    @Column
    public String name;
    @Column
    public String value;
    @Column
    public long count; // / the product count.
    @Column
    public long parentId;

    public Category() {

    }

}
