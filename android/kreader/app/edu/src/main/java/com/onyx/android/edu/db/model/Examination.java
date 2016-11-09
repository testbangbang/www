package com.onyx.android.edu.db.model;

import com.onyx.android.edu.db.dataprovider.ChapterDataProvider;
import com.onyx.android.edu.db.manage.AppDatabase;
import com.onyx.android.edu.db.typeconverter.LongListConverter;
import com.onyx.android.edu.db.typeconverter.SectionConverter;
import com.onyx.android.edu.db.typeconverter.SubjectConverter;
import com.onyx.android.edu.db.typeconverter.TextbookConverter;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

/**
 * Created by ming on 16/7/7.
 */
public class Examination extends BaseDbModel{
    //标题
    @Column
    public String title;
    //细节
    @Column
    public String detail;
    //作者
    @Column
    public String author;
    //1:作业 2:考试 3:题库
    @Column
    public Integer type;
    //教材
    @Column(typeConverter = TextbookConverter.class)
    public Textbook textbook;
    //科目
    @Column(typeConverter = SubjectConverter.class)
    public Subject subject;
    //分段
    @Column(typeConverter = SectionConverter.class)
    public LearnSection learnSection;
    //章节集合
    @Column(typeConverter = LongListConverter.class)
    public List chapterIds;

}
