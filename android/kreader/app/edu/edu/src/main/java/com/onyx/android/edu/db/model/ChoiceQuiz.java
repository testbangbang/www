package com.onyx.android.edu.db.model;

import com.onyx.android.edu.db.manage.AppDatabase;
import com.onyx.android.edu.db.typeconverter.AtomicQuizConverter;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by ming on 16/7/5.
 */
public class ChoiceQuiz extends BaseDbModel{
    @Column
    public Integer type; // 1：单选 2：多选
    @Column(typeConverter = AtomicQuizConverter.class)
    public AtomicQuiz quiz;
}
