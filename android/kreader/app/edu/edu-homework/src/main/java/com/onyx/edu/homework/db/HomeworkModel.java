package com.onyx.edu.homework.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by lxm on 2017/12/11.
 */

@Table(database = HomeworkDatabase.class)
public class HomeworkModel extends BaseModel {

    @PrimaryKey
    @Column
    @Unique
    private String uniqueId;

    // 0 doing 1 done 2 review
    @Column
    private int state;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public static HomeworkModel create(String uniqueId) {
        HomeworkModel model = new HomeworkModel();
        model.setUniqueId(uniqueId);
        return model;
    }
}
