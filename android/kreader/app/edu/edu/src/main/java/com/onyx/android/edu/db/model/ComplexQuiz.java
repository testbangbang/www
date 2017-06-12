package com.onyx.android.edu.db.model;

import com.onyx.android.edu.db.manage.AppDatabase;
import com.onyx.android.edu.db.typeconverter.AtomicQuizConverter;
import com.onyx.android.edu.utils.JsonUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

/**
 * Created by ming on 16/7/5.
 */
public class ComplexQuiz extends BaseDbModel{
    @Column(typeConverter = AtomicQuizConverter.class)
    public AtomicQuiz quiz;
    public String childQuizList; //子问题组

    public List<AtomicQuiz> getChildQuizList() {
        return JsonUtils.toBean(childQuizList, List.class);
    }
}
