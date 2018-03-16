package com.onyx.edu.homework.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.homework.HomeworkReviewResult;
import com.onyx.android.sdk.data.model.homework.HomeworkSubmitAnswer;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.data.model.homework.QuestionOption;
import com.onyx.android.sdk.data.model.homework.QuestionReview;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.scribble.data.ShapeDataProvider;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.scribble.data.ShapeState;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.Debug;
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
    private String publicHomeworkId;
    private String personalHomeworkId;
    private HomeworkState currentState;

    public GetHomeworkReviewsRequest(List<Question> questions, String publicHomeworkId, String personalHomeworkId) {
        this.questions = questions;
        this.publicHomeworkId = publicHomeworkId;
        this.personalHomeworkId = personalHomeworkId;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        HomeworkModel model = DBDataProvider.loadHomework(personalHomeworkId);
        if (model == null) {
            model = HomeworkModel.create(personalHomeworkId);
        }
        int state = model.getState();
        currentState = HomeworkState.getHomeworkState(state);
        Response<HomeworkReviewResult> response = executeCall(ServiceFactory.getHomeworkService(parent.getCloudConf().getApiBase()).getAnwsers(publicHomeworkId));
        if (response.isSuccessful()) {
            HomeworkReviewResult result = response.body();
            if (result.checked) {
                onReviewed(result.anwsers, model);
            }

        }
        state = model.getState();
        currentState = HomeworkState.getHomeworkState(state);
    }

    private void onReviewed(List<HomeworkSubmitAnswer> reviews, @NonNull HomeworkModel model) {
        saveQuestionReview(questions, reviews);
        updateShapeState(questions);
        model.setState(HomeworkState.REVIEW.ordinal());
        DBDataProvider.saveHomework(model);
    }

    private void updateShapeState(List<Question> questions) {
        if (CollectionUtils.isNullOrEmpty(questions)) {
            return;
        }
        for (Question question : questions) {
            if (question.isChoiceQuestion()) {
                continue;
            }
            List<ShapeModel> shapeList = ShapeDataProvider.loadShapeList(getContext(), question.uniqueId);
            if (!CollectionUtils.isNullOrEmpty(shapeList)) {
                for (ShapeModel shapeModel : shapeList) {
                    if (shapeModel.getState() == ShapeState.NORMAL) {
                        shapeModel.setState(ShapeState.REVIEWED);
                    }
                }
                ShapeDataProvider.saveShapeList(getContext(), shapeList);
            }
        }
    }

    private void saveQuestionReview(List<Question> questions, List<HomeworkSubmitAnswer> reviews) {
        if (questions == null) {
            return;
        }
        for (Question question : questions) {
            QuestionModel model = DBDataProvider.loadQuestion(question.getUniqueId());
            if (model == null) {
                model = QuestionModel.create(question.getQuestionId(),
                        personalHomeworkId);
            }
            setQuestionReview(question, model, reviews);
            DBDataProvider.saveQuestion(model);
        }
    }

    private void setQuestionReview(Question question, QuestionModel model, List<HomeworkSubmitAnswer> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return;
        }
        HomeworkSubmitAnswer answer = findReview(reviews, question.getQuestionId());
        QuestionReview review = null;
        if (answer != null) {
            review = QuestionReview.create(answer);
        }
        model.setReview(review);
        if (CollectionUtils.isNullOrEmpty(model.getValues()) && null != answer) {
            model.setValues(answer.value);
            loadUserSelectOption(question, model);
        }
        question.setReview(review);
    }

    @Nullable
    private HomeworkSubmitAnswer findReview(@NonNull List<HomeworkSubmitAnswer> reviews, String questionId) {
        for (HomeworkSubmitAnswer review : reviews) {
            if (review.question.equals(questionId)) {
                return review;
            }
        }
        return null;
    }


    public HomeworkState getCurrentState() {
        return currentState;
    }

    private void loadUserSelectOption(Question question, QuestionModel model) {
        List<String> values = model.getValues();
        if (values == null) {
            return;
        }
        List<QuestionOption> options = question.options;
        if (options == null) {
            return;
        }
        for (String value : values) {
            QuestionOption option = findQuestionOption(options, value);
            if (option != null) {
                option.setChecked(true);
            }
        }
    }

    private QuestionOption findQuestionOption(@NonNull List<QuestionOption> options, String optionId) {
        for (QuestionOption option : options) {
            if (option._id.equals(optionId)) {
                return option;
            }
        }
        return null;
    }
}
