package com.onyx.android.edu.ui.exerciserespond;

import android.support.annotation.NonNull;

import com.onyx.android.edu.base.BaseQuestionView;
import com.onyx.android.edu.bean.PaperResult;
import com.onyx.android.edu.db.model.Chapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ming on 16/6/24.
 */
public class ExerciseRespondPresenter implements ExerciseRespondContract.ExerciseRespondPresenter {

    private final ExerciseRespondContract.ExerciseRespondView exerciseRespondView;
    private boolean showAnswer = false;
    private Chapter chapter;

    public ExerciseRespondPresenter(@NonNull ExerciseRespondContract.ExerciseRespondView testPaperExerciseRespondView, boolean showAnswer){
        this.showAnswer = showAnswer;
        exerciseRespondView = testPaperExerciseRespondView;
        exerciseRespondView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadPapers();
    }

    @Override
    public void unSubscribe() {

    }

    @Override
    public void loadPapers() {
//        List<Chapter> chapters = new Select().from(Chapter.class).queryList();
//        chapter = chapters.get(0);
        exerciseRespondView.showPaper(null, showAnswer);
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

    public boolean isShowAnswer() {
        return showAnswer;
    }

    public void setShowAnswer(boolean showAnswer) {
        this.showAnswer = showAnswer;
    }
}
