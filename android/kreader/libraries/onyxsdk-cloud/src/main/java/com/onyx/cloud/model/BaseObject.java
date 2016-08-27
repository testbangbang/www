package com.onyx.cloud.model;

import java.util.Date;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by zhuzeng on 8/17/15.
 */
public class BaseObject extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    @Index
    public long id;

    @Column
    @Unique
    @Index
    public String idString;

	@Column
    public Date updatedAt;
	@Column
    public Date createdAt;

    public BaseObject() {

    }

    public String getIdString() {
        return idString;
    }

    public void checkSave() {
        save();
    }

}
