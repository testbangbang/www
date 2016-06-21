package com.onyx.android.sdk.scribble.data;

import android.graphics.RectF;
import com.raizlabs.android.dbflow.annotation.*;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;
import java.util.UUID;

/**
 * Created by zhuzeng on 6/21/16.
 */
@Table(database = ShapeDatabase.class)
public class ShapeLibraryModel extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    @Index
    long id;

    @Column
    Date createdAt = null;

    @Column
    Date updatedAt = null;

    @Column
    @Unique
    String documentUniqueId;

    @Column
    String parentUniqueId;

    @Column
    String subPageName;

    @Column
    String title;

    @Column
    String extraAttributes;

    public ShapeLibraryModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long value) {
        id = value;
    }

    public void setCreatedAt(final Date d) {
        createdAt = d;
    }

    public final Date getCreatedAt() {
        return createdAt;
    }

    public final Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date d) {
        updatedAt = d;
    }

    public String getDocumentUniqueId() {
        return documentUniqueId;
    }

    public void setDocumentUniqueId(final String id) {
        documentUniqueId = id;
    }

    public String getParentUniqueId() {
        return parentUniqueId;
    }

    public void setParentUniqueId(final String name) {
        parentUniqueId = name;
    }

    public String getSubPageName() {
        return subPageName;
    }

    public void setSubPageName(final String spn) {
        subPageName = spn;
    }

    public String getExtraAttributes() {
        return extraAttributes;
    }

    public void setExtraAttributes(final String attributes) {
        extraAttributes = attributes;
    }

    public void setTitle(final String id) {
        title = id;
    }

    public String getTitle() {
        return title;
    }
}
