package com.onyx.android.edu.ui.exerciserespond;


import com.onyx.android.edu.base.BasePresenter;
import com.onyx.android.edu.base.BaseQuestionView;
import com.onyx.android.edu.base.BaseView;
import com.onyx.android.edu.bean.PaperResult;
import com.onyx.android.edu.db.model.Chapter;
import com.onyx.libedu.model.ChooseQuestionVariable;
import com.onyx.libedu.model.Question;
import com.onyx.libedu.model.QuestionAnalytical;

import java.util.List;

/**
 * Created by ming on 16/6/24.
 */
public interface ExerciseRespondContract {

    interface ExerciseRespondView extends BaseView<ExerciseRespondPresenter> {
        void showQuestions(List<Question> questions, ChooseQuestionVariable variable, boolean showAnswer);
        void showToast();
    }

    interface ExerciseRespondPresenter extends BasePresenter {
        PaperResult getPaperResult(List<BaseQuestionView> selectViewList);
        void insertAnswerAndScore(String bookId, long questionId, String answer, String score);
    }
}
