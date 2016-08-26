package com.onyx.android.sdk.dataprovider;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.Date;

/**
 * Created by zhuzeng on 6/1/16.
 */
@Table(database = ContentDatabase.class)
public class BaseData extends BaseModel {

    public static final int INVALID_ID = -1;
    public static final String DELIMITER = ",";

    @Column
    @PrimaryKey(autoincrement = true)   @Index
    long id = INVALID_ID;

    @Column
    @Index
    String uniqueId = null;

    @Column
    private Date createdAt = null;

    @Column
    private Date updatedAt = null;

    public long getId() {
        return id;
    }

    public void setId(long value) {
        id = value;
    }

    public final String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final String value) {
        uniqueId = value;
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

    private void beforeSave() {
        Date now = new Date();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @Override
    public void save() {
        beforeSave();
        super.save();
    }

    @Override
    public void save(DatabaseWrapper databaseWrapper) {
        beforeSave();
        super.save(databaseWrapper);
    }
}
