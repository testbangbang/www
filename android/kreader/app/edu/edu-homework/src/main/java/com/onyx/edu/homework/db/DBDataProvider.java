package com.onyx.edu.homework.db;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.List;

/**
 * Created by lxm on 2017/12/11.
 */

public class DBDataProvider {

    public static HomeworkModel loadHomework(String uniqueId) {
        Select select = new Select();
        Where<HomeworkModel> where = select.from(HomeworkModel.class).where(HomeworkModel_Table.uniqueId.eq(uniqueId));
        return where.querySingle();
    }

    public static void saveHomework(HomeworkModel model) {
        model.save();
    }

    public static QuestionModel loadQuestion(String homeworkId, String id) {
        Select select = new Select();
        Where<QuestionModel> where = select.from(QuestionModel.class).where(QuestionModel_Table.uniqueId.eq(id));
        return where.querySingle();
    }

    public static List<QuestionModel> loadQuestions(String homeworkId) {
        Select select = new Select();
        Where<QuestionModel> where = select.from(QuestionModel.class).where(QuestionModel_Table.homeworkId.eq(homeworkId));
        return where.queryList();
    }

    public static void saveQuestion(QuestionModel model) {
        model.save();
    }
}
