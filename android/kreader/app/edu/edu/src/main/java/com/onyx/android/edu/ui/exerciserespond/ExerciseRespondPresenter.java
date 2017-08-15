package com.onyx.android.edu.ui.exerciserespond;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.onyx.android.edu.EduApp;
import com.onyx.android.edu.base.BaseQuestionView;
import com.onyx.android.edu.bean.PaperResult;
import com.onyx.android.edu.db.model.Chapter;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.libedu.EduCloudManager;
import com.onyx.libedu.db.PaperQuestionAndAnswer;
import com.onyx.libedu.model.ChooseQuestionVariable;
import com.onyx.libedu.model.Question;
import com.onyx.libedu.request.cloud.GetAnswerAndAnalyzeRequest;
import com.onyx.libedu.request.cloud.GetQuestionsRequest;
import com.onyx.libedu.request.cloud.InsertUserAnswerRequest;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ming on 16/6/24.
 */
public class ExerciseRespondPresenter implements ExerciseRespondContract.ExerciseRespondPresenter {

    private final ExerciseRespondContract.ExerciseRespondView exerciseRespondView;
    private String bookId;
    private boolean showAnswer = false;
    private List<Question> questions;
    private ChooseQuestionVariable chooseQuestionVariable;
    private EduCloudManager eduCloudManager;

    public ExerciseRespondPresenter(@NonNull ExerciseRespondContract.ExerciseRespondView testPaperExerciseRespondView, String bookId){
        exerciseRespondView = testPaperExerciseRespondView;
        exerciseRespondView.setPresenter(this);
        this.bookId = bookId;
    }

    @Override
    public void subscribe() {
        eduCloudManager = new EduCloudManager();
        getDate();
        //exerciseRespondView.showQuestions(questions, chooseQuestionVariable, showAnswer);
    }

    private void getDate() {
        final GetQuestionsRequest rq = new GetQuestionsRequest(bookId);
        eduCloudManager.submitRequest(EduApp.instance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                questions = rq.getQuestions();
                if(questions != null && questions.size() > 0) {
                    exerciseRespondView.showQuestions(questions, chooseQuestionVariable, showAnswer);
                } else {
                    exerciseRespondView.showToast();
                }
            }
        });
    }

    @Override
    public void unSubscribe() {

    }

    @Override
    public PaperResult getPaperResult(List<BaseQuestionView> selectViewList) {
        float score = 0;
        List<Boolean> result = new ArrayList<>();
        for (BaseQuestionView view : selectViewList) {
            result.add(view.isRight());
            score = score + view.getScore();
        }
        PaperResult paperResult = new PaperResult();
        paperResult.setResult(result);
        paperResult.setScore(score);
        return paperResult;
    }

    @Override
    public void insertAnswerAndScore(long questionId, String answer, String score) {
        InsertUserAnswerRequest rq = new InsertUserAnswerRequest(questionId, answer, score);
        eduCloudManager.submitRequest(EduApp.instance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

}
