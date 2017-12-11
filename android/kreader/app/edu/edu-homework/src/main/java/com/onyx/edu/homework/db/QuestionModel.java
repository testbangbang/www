package com.onyx.edu.homework.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by lxm on 2017/12/11.
 */

@Table(database = HomeworkDatabase.class)
public class QuestionModel extends BaseModel {

    @PrimaryKey
    @Column
    @Unique
    private String uniqueId;

    @Column
    private String homeworkId;

    @Column(typeConverter = ConverterListString.class)
    private List values;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(String homeworkId) {
        this.homeworkId = homeworkId;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
