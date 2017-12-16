package com.onyx.edu.homework.request;

import android.support.annotation.NonNull;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.HomeworkReviewResult;
import com.onyx.android.sdk.data.model.HomeworkSubmitAnswer;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.data.model.QuestionReview;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.edu.homework.data.Homework;
import com.onyx.edu.homework.data.HomeworkState;
import com.onyx.edu.homework.db.DBDataProvider;
import com.onyx.edu.homework.db.HomeworkModel;
import com.onyx.edu.homework.db.QuestionModel;

import java.util.List;

import retrofit2.Response;

/**
 * Created by lxm on 2017/12/13.
 */

public class GetHomeworkReviewsRequest extends BaseCloudRequest {

    private List<Question> questions;
    private String homeworkId;
    private HomeworkState currentState;

    public GetHomeworkReviewsRequest(List<Question> questions, String homeworkId) {
        this.questions = questions;
        this.homeworkId = homeworkId;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        HomeworkModel model = DBDataProvider.loadHomework(homeworkId);
        if (model == null) {
            model = HomeworkModel.create(homeworkId);
        }
        Response<HomeworkReviewResult> response = executeCall(ServiceFactory.getHomeworkService(parent.getCloudConf().getApiBase()).getAnwsers(homeworkId));
        if (response.isSuccessful()) {
            HomeworkReviewResult result = response.body();
            if (result.checked) {
                onReviewed(result.anwsers, model);
            }

        }
        int state = model.getState();
        currentState = HomeworkState.getHomeworkState(state);
    }

    private void onReviewed(List<HomeworkSubmitAnswer> reviews, @NonNull HomeworkModel model) {
        saveQuestionReview(questions, reviews);
        model.setState(HomeworkState.REVIEW.ordinal());
        DBDataProvider.saveHomework(model);
    }

    private void saveQuestionReview(List<Question> questions, List<HomeworkSubmitAnswer> reviews) {
        if (questions == null) {
            return;
        }
        for (Question question : questions) {
            QuestionModel model = DBDataProvider.loadQuestion(question.getUniqueId());
            if (model == null) {
                model = QuestionModel.create(question.getUniqueId(),
                        question.getQuestionId(),
                        homeworkId);
            }
            setQuestionReview(question, model, reviews);
            DBDataProvider.saveQuestion(model);
        }
    }

    private void setQuestionReview(Question question, QuestionModel model, List<HomeworkSubmitAnswer> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return;
        }
        QuestionReview review = findReview(reviews, question.getUniqueId());
        model.setReview(review);
        question.setReview(review);
    }

    private QuestionReview findReview(@NonNull List<HomeworkSubmitAnswer> reviews, String questionUniqueId) {
        for (HomeworkSubmitAnswer review : reviews) {
            if (review.uniqueId.equals(questionUniqueId)) {
                return QuestionReview.create(review);
            }
        }
        return null;
    }


    public HomeworkState getCurrentState() {
        return currentState;
    }
}
