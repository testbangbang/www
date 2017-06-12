package com.onyx.android.edu.ui.exerciserespond;

import android.support.annotation.NonNull;

import com.onyx.android.edu.EduApp;
import com.onyx.android.edu.base.BaseQuestionView;
import com.onyx.android.edu.bean.PaperResult;
import com.onyx.android.edu.db.model.Chapter;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.libedu.EduCloudManager;
import com.onyx.libedu.model.ChooseQuestionVariable;
import com.onyx.libedu.model.Question;
import com.onyx.libedu.request.cloud.GetAnswerAndAnalyzeRequest;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ming on 16/6/24.
 */
public class ExerciseRespondPresenter implements ExerciseRespondContract.ExerciseRespondPresenter {

    private final ExerciseRespondContract.ExerciseRespondView exerciseRespondView;
    private boolean showAnswer = false;
    private List<Question> questions;
    private ChooseQuestionVariable chooseQuestionVariable;

    public ExerciseRespondPresenter(@NonNull ExerciseRespondContract.ExerciseRespondView testPaperExerciseRespondView, boolean showAnswer, List<Question>questions, ChooseQuestionVariable variable){
        this.showAnswer = showAnswer;
        this.questions = questions;
        this.chooseQuestionVariable = variable;
        exerciseRespondView = testPaperExerciseRespondView;
        exerciseRespondView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        exerciseRespondView.showQuestions(questions, chooseQuestionVariable, showAnswer);
    }

    @Override
    public void unSubscribe() {

    }

    @Override
    public PaperResult getPaperResult(List<BaseQuestionView> selectViewList) {
        PaperResult paperResult = new PaperResult();
        List<Boolean> result = new ArrayList<>();
        float score = 0;
        paperResult.setResult(result);
        paperResult.setScore(score);
        return paperResult;
    }

}
