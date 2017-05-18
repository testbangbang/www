package com.onyx.android.edu.ui.chapter;

import com.onyx.android.edu.EduApp;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.libedu.EduCloudManager;
import com.onyx.libedu.model.BookNode;
import com.onyx.libedu.model.ChooseQuestionVariable;
import com.onyx.libedu.model.KnowledgePoint;
import com.onyx.libedu.model.Question;
import com.onyx.libedu.request.cloud.GetAnswerAndAnalyzeRequest;
import com.onyx.libedu.request.cloud.GetChapterQuestionsRequest;
import com.onyx.libedu.request.cloud.GetKnowledgePointQuestionsRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/6/28.
 */
public class ChapterTypePresenter implements ChapterTypeContract.ChapterTypePresenter {

    private ChapterTypeContract.ChapterTypeView chapterTypeView;
    private List<BookNode> bookNodes;
    private List<KnowledgePoint> knowledgePoints;
    private ChooseQuestionVariable chooseQuestionVariable;
    private EduCloudManager eduCloudManager;
    private List<Question> questions;

    public ChapterTypePresenter(ChapterTypeContract.ChapterTypeView chapterTypeView){
        this.chapterTypeView = chapterTypeView;
        this.chapterTypeView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        eduCloudManager = new EduCloudManager();
    }

    @Override
    public void unSubscribe() {

    }

    public void setBookNodes(List<BookNode> bookNodes) {
        this.bookNodes = bookNodes;
    }

    @Override
    public List<BookNode> getBookNodes() {
        return bookNodes;
    }

    @Override
    public ChooseQuestionVariable getChooseQuestionVariable() {
        return chooseQuestionVariable;
    }

    public void setChooseQuestionVariable(ChooseQuestionVariable chooseQuestionVariable) {
        this.chooseQuestionVariable = chooseQuestionVariable;
    }

    public void setKnowledgePoints(List<KnowledgePoint> knowledgePoints) {
        this.knowledgePoints = knowledgePoints;
    }

    @Override
    public List<KnowledgePoint> getKnowledgePoints() {
        return knowledgePoints;
    }

    @Override
    public void loadChapterQuestions(BookNode bookNode1, BookNode bookNode2) {
        final GetChapterQuestionsRequest chapterQuestionsRequest = new GetChapterQuestionsRequest(chooseQuestionVariable);
        chapterQuestionsRequest.setBookNodeId1(bookNode1.getId());
        chapterQuestionsRequest.setBookNodeId2(bookNode2.getId());
        chapterQuestionsRequest.setStartPage(0);
        chapterQuestionsRequest.setCountPerPage(10);
        eduCloudManager.submitRequest(EduApp.instance(), chapterQuestionsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                questions = chapterQuestionsRequest.getQuestions();
                loadQuestionAnalytical(0);
            }
        });
    }

    @Override
    public void loadKnowledgePointQuestions(KnowledgePoint knowledgePoint1, KnowledgePoint knowledgePoint2) {
        final GetKnowledgePointQuestionsRequest knowledgePointQuestionsRequest = new GetKnowledgePointQuestionsRequest(chooseQuestionVariable);
        knowledgePointQuestionsRequest.setKnowledgePointId1(knowledgePoint1.getId());
        knowledgePointQuestionsRequest.setKnowledgePointId2(knowledgePoint2.getId());
        knowledgePointQuestionsRequest.setStartPage(0);
        knowledgePointQuestionsRequest.setCountPerPage(10);
        eduCloudManager.submitRequest(EduApp.instance(), knowledgePointQuestionsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                questions = knowledgePointQuestionsRequest.getQuestions();
                loadQuestionAnalytical(0);
            }
        });
    }

    private void loadQuestionAnalytical(final int index) {
        if (questions == null || index >= questions.size()) {
            chapterTypeView.openQuestions(questions);
            return;
        }
        final Question question = questions.get(index);
        final GetAnswerAndAnalyzeRequest answerAndAnalyzeRequest = new GetAnswerAndAnalyzeRequest(chooseQuestionVariable.getSubject().getId(), question.getId());
        eduCloudManager.submitRequest(EduApp.instance(), answerAndAnalyzeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                question.setQuestionAnalytical(answerAndAnalyzeRequest.getAnalytical());
                loadQuestionAnalytical(index + 1);
            }
        });
    }
}
