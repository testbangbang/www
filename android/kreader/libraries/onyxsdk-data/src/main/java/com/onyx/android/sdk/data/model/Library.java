package com.onyx.android.sdk.data.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.HashMap;
import java.util.Map;

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

    @ColumnIgnore
    private Map<String, String> bookCovers = new HashMap<>();

    @ColumnIgnore
    private Map<String, String> libraryCovers = new HashMap<>();

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

    public Map<String, String> getBookCovers() {
        return bookCovers;
    }

    public void setBookCovers(Map<String, String> bookCovers) {
        this.bookCovers = bookCovers;
    }

    public Map<String, String> getLibraryCovers() {
        return libraryCovers;
    }

    public void setLibraryCovers(Map<String, String> libraryCovers) {
        this.libraryCovers = libraryCovers;
    }

    @JSONField(deserialize = false, serialize = false)
    public String getBookCoverUrl(String key) {
        if (CollectionUtils.isNullOrEmpty(bookCovers)) {
            return null;
        }
        return bookCovers.get(key);
    }

    @JSONField(deserialize = false, serialize = false)
    public String getLibraryCoverUrl(String key) {
        if (CollectionUtils.isNullOrEmpty(libraryCovers)) {
            return null;
        }
        return libraryCovers.get(key);
    }
}
