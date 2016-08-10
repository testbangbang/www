package com.onyx.android.edu.testpaper;

import android.support.annotation.NonNull;

import com.onyx.android.edu.base.BaseQuestionView;
import com.onyx.android.edu.bean.PaperResult;
import com.onyx.android.edu.db.model.Chapter;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ming on 16/6/24.
 */
public class TestPaperPresenter implements TestPaperContract.Presenter{


    private final TestPaperContract.View mTestPaperView;
    private boolean mShowAnswer = false;
    private Chapter mChapter;

    public TestPaperPresenter(@NonNull TestPaperContract.View testPaperView, boolean showAnswer){
        mShowAnswer = showAnswer;
        mTestPaperView = testPaperView;
        mTestPaperView.setPresenter(this);
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
        List<Chapter> chapters = new Select().from(Chapter.class).queryList();
        mChapter = chapters.get(0);
        mTestPaperView.showPaper(mChapter,mShowAnswer);
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
        return mShowAnswer;
    }

    public void setShowAnswer(boolean showAnswer) {
        mShowAnswer = showAnswer;
    }
}
