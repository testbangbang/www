package com.onyx.android.edu.db.model;

import com.onyx.android.edu.db.dataprovider.QuizDataProvider;
import com.onyx.android.edu.db.manage.AppDatabase;
import com.onyx.android.edu.db.typeconverter.QuizIdMapConverter;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;
import java.util.Map;

/**
 * Created by ming on 16/7/1.
 * 章节表
 */
public class Chapter extends BaseDbModel{

    @Column //章节名称
    private String name;
    @Column //父章节名称
    private String fatherName;
    @Column(typeConverter = QuizIdMapConverter.class)
    private Map quizIds; //key 为ID value为问题类型

    private List<Object> quizList;

    public Map getQuizIds() {
        return quizIds;
    }

    public void setQuizIds(Map quizIds) {
        this.quizIds = quizIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }
}
