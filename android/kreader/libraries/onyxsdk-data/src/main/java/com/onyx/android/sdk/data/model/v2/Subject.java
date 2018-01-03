package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.Table;

import java.io.Serializable;

/**
 * Created by suicheng on 2017/11/14.
 */
@Table(database = ContentDatabase.class, allFields = true)
public class Subject extends BaseData implements Serializable {
    public String _id;
    public String name;

    public String category;
    public String grade;
    public String stage;

    public static boolean isValid(Subject subject) {
        return subject != null && StringUtils.isNotBlank(subject._id) && StringUtils.isNotBlank(subject.name);
    }
}
