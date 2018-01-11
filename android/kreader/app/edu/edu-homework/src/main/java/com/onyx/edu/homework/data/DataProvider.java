package com.onyx.edu.homework.data;

import com.onyx.android.sdk.data.model.homework.Homework;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.edu.homework.db.HomeworkModel;
import com.onyx.edu.homework.db.QuestionModel;

/**
 * Created by lxm on 2018/1/9.
 */

public class DataProvider {

    public static void loadQuestionFromModel(Question question, QuestionModel model) {
        if (model == null) {
            return;
        }
        question._id = model.getQuestionId();
        question.uniqueId = model.getUniqueId();
        question.analysis = model.getAnalysis();
        question.answers = model.getAnswerContent();
        question.content = model.getContent();
        question.correctOptions = model.getAnswer();
        question.QuesType = model.getType();
        question.difficulty = model.getDifficulty();
        question.options = model.getOptions();
        question.review = model.getReview();
    }

    public static void loadHomeworkFromModel(Homework homework, HomeworkModel model) {
        if (model == null) {
            return;
        }
        homework._id = model.getUniqueId();
        homework.beginTime = model.getBeginTime();
        homework.endTime = model.getEndTime();
        homework.subject = model.getSubject();
    }

}
