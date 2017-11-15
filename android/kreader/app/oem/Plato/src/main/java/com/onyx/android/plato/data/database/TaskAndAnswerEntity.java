package com.onyx.android.plato.data.database;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by li on 2017/10/18.
 */
@Table(database = SunDatabase.class)
public class TaskAndAnswerEntity extends BaseModel implements Comparable<TaskAndAnswerEntity> {
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String taskId;
    @Column
    public String type;
    @Column
    public String questionId;
    @Column
    public String question;
    @Column
    public String userAnswer;

    @Override
    public int compareTo(@NonNull TaskAndAnswerEntity entity) {
        if (!this.type.equals(entity.type)) {
            return 0;
        }
        return Integer.parseInt(this.questionId) - Integer.parseInt(entity.questionId);
    }
}
