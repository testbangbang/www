package com.onyx.android.plato.requests.local;

import com.onyx.android.plato.cloud.bean.ExerciseBean;
import com.onyx.android.plato.cloud.bean.ExerciseMessageBean;
import com.onyx.android.plato.cloud.bean.Question;
import com.onyx.android.plato.cloud.bean.QuestionData;
import com.onyx.android.plato.cloud.bean.QuestionViewBean;
import com.onyx.android.plato.requests.requestTool.BaseLocalRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2017/11/16.
 */

public class ResolveAdapterDataRequest extends BaseLocalRequest {
    private int taskId;
    private List<QuestionData> questionDataList;
    private List<ExerciseMessageBean> questionMessages;
    private int currentParentId;
    private List<QuestionViewBean> questionList = new ArrayList<>();

    public ResolveAdapterDataRequest(List<QuestionData> questionDataList, List<ExerciseMessageBean> questionMessages, int taskId) {
        this.questionDataList = questionDataList;
        this.questionMessages = questionMessages;
        this.taskId = taskId;
    }

    public List<QuestionViewBean> getQuestionList() {
        return questionList;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        if (questionList != null) {
            questionList.clear();
        }
        for (QuestionData questionData : questionDataList) {
            List<ExerciseBean> exercises = questionData.exercises;
            if (exercises != null && exercises.size() > 0) {
                for (int i = 0; i < exercises.size(); i++) {
                    ExerciseBean exercise = exercises.get(i);
                    List<Question> questions = exercise.exercises;
                    if (questions != null && questions.size() > 0) {
                        for (int j = 0; j < questions.size(); j++) {
                            Question question = questions.get(j);
                            QuestionViewBean exerciseBean = new QuestionViewBean();
                            exerciseBean.setTaskId(taskId);
                            exerciseBean.setShow(i == 0 && j == 0);
                            exerciseBean.setAllScore(questionData.allScore);
                            exerciseBean.setExeNumber(questionData.exeNumber);
                            exerciseBean.setShowType(questionData.showType);
                            exerciseBean.setParentId(exercise.id);
                            exerciseBean.setShowReaderComprehension(currentParentId != exercise.id);
                            currentParentId = exercise.id;
                            exerciseBean.setScene(exercise.scene);
                            exerciseBean.setId(question.id);
                            exerciseBean.setContent(question.content);
                            exerciseBean.setExerciseSelections(question.exerciseSelections);
                            exerciseBean.setUserAnswer(question.userAnswer);
                            this.questionList.add(exerciseBean);
                        }
                    }
                }
            }
        }

        if (questionMessages != null && questionMessages.size() > 0) {
            for (QuestionViewBean bean : questionList) {
                for (ExerciseMessageBean message : questionMessages) {
                    if (bean.getId() == message.exerciseId) {
                        bean.setState(message.state);
                        bean.setAnswer(message.answer);
                        bean.setScore(message.score);
                        bean.setValue(message.value);
                        bean.setKnowledgeDtoList(message.knowledgeDtoList);
                        bean.setAccuracy(message.accuracy);
                        bean.setCorrect(message.correct);
                        bean.setExerciseFavored(message.exerciseFavored);
                    }
                }
            }
        }
    }
}
