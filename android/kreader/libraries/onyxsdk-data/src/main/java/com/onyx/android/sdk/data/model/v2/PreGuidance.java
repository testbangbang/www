package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.converter.ListStringConverter;
import com.onyx.android.sdk.data.converter.PreGuidanceDetailConverter;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.BaseData;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

/**
 * Created by suicheng on 2017/10/20.
 */
@Table(database = ContentDatabase.class, allFields = true)
public class PreGuidance extends BaseData {
    public String _id;
    public String parent;
    @Column(typeConverter = PreGuidanceDetailConverter.class)
    public PreGuidanceDetail child;
    public String ref;
    @Column(typeConverter = ListStringConverter.class)
    public List<String> refChapters;
    @Column(typeConverter = ListStringConverter.class)
    public List<String> category;
    public int ordinal;
    public int status;
}
