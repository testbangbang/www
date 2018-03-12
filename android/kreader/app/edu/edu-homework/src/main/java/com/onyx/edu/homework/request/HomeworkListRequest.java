package com.onyx.edu.homework.request;

import android.support.annotation.NonNull;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.homework.Homework;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.data.model.homework.QuestionOption;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.edu.homework.data.DataProvider;
import com.onyx.edu.homework.data.HomeworkState;
import com.onyx.edu.homework.db.DBDataProvider;
import com.onyx.edu.homework.db.HomeworkModel;
import com.onyx.edu.homework.db.QuestionModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkListRequest extends BaseCloudRequest {

    private String publicHomeworkId;
    private String personalHomeworkId;
    private Homework homework;
    private HomeworkState homeworkState = HomeworkState.BEFORE_SUBMIT;

    public HomeworkListRequest(String publicHomeworkId, String personalHomeworkId) {
        this.publicHomeworkId = publicHomeworkId;
        this.personalHomeworkId = personalHomeworkId;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (NetworkUtil.isWiFiConnected(getContext())) {
            loadFromCloud(parent);
        }else {
            loadFromLocal();
        }
        loadHomeworkState();
    }

    private void loadFromLocal() {
        List<QuestionModel> questionModels = DBDataProvider.loadQuestions(personalHomeworkId);
        if (CollectionUtils.isNullOrEmpty(questionModels)) {
            return;
        }
        List<Question> questions = new ArrayList<>();
        for (QuestionModel questionModel : questionModels) {
            Question question = new Question();
            DataProvider.loadQuestionFromModel(question, questionModel);
            loadUserSelectOption(question, questionModel);
            questions.add(question);
        }

        HomeworkModel homeworkModel = DBDataProvider.loadHomework(publicHomeworkId);
        homework = new Homework(personalHomeworkId);
        homework.setQuestions(questions);
        DataProvider.loadHomeworkFromModel(homework, homeworkModel);
    }

    private void loadFromCloud(CloudManager parent) throws Exception {
        Response<Homework> response = executeCall(ServiceFactory.getHomeworkService(parent.getCloudConf().getApiBase()).getHomeworks(publicHomeworkId));
        homework = response.body();
        if (response.isSuccessful()) {
            saveHomework(homework);
        }else {
            loadFromLocal();
        }
    }

    private void saveHomework(Homework homework) {
        HomeworkModel homeworkModel = DBDataProvider.loadHomework(personalHomeworkId);
        if (homeworkModel == null) {
            homeworkModel = HomeworkModel.create(personalHomeworkId);
        }
        homeworkModel.loadFromHomework(homework);
        homeworkModel.save();
        syncQuestions(homework.questions);
    }

    private void syncQuestions(List<Question> questions) {
        for (Question question : questions) {
            QuestionModel model = DBDataProvider.loadQuestion(question.getQuestionId(), personalHomeworkId);
            if (model == null) {
                model = QuestionModel.create(question.getQuestionId(),
                        personalHomeworkId);
            }

            model.loadFromQuestion(question);
            DBDataProvider.saveQuestion(model);
            loadUserSelectOption(question, model);
            loadQuestionReview(question, model);
            syncQuestionUniqueId(question, model);
        }
    }

    private void syncQuestionUniqueId(Question question, QuestionModel model) {
        question.setUniqueId(model.getUniqueId());
    }

    private void loadQuestionReview(Question question, QuestionModel model) {
        question.setReview(model.getReview());
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

    private void loadHomeworkState() {
        HomeworkModel homeworkModel = DBDataProvider.loadHomework(personalHomeworkId);
        if (homeworkModel == null) {
            homeworkModel = HomeworkModel.create(personalHomeworkId);
        }
        int state = homeworkModel.getState();
        homeworkState = HomeworkState.getHomeworkState(state);
    }

    public Homework getHomework() {
        return homework;
    }

    public HomeworkState getHomeworkState() {
        return homeworkState;
    }
}
