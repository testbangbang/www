package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.db.ContentDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by zhuzeng on 8/26/16.
 */
@Table(database = ContentDatabase.class)
public class Library extends BaseData {

    @Column
    private String name = null;

    @Column
    private String description = null;

    @Column
    private String queryString = null;

    @Column
    private String extraAttributes = null;

    @Column
    @Index
    private String parentUniqueId = null;

    public String getParentUniqueId() {
        return parentUniqueId;
    }

    public void setParentUniqueId(String parentUniqueId) {
        this.parentUniqueId = parentUniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getExtraAttributes() {
        return extraAttributes;
    }

    public void setExtraAttributes(String extraAttributes) {
        this.extraAttributes = extraAttributes;
    }

}
