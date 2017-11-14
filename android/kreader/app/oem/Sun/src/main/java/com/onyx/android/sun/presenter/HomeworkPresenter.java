package com.onyx.android.sun.presenter;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.ContentBean;
import com.onyx.android.sun.cloud.bean.ExerciseBean;
import com.onyx.android.sun.cloud.bean.FinishContent;
import com.onyx.android.sun.cloud.bean.GetStudyReportDetailResultBean;
import com.onyx.android.sun.cloud.bean.GetSubjectBean;
import com.onyx.android.sun.cloud.bean.HomeworkFinishedResultBean;
import com.onyx.android.sun.cloud.bean.HomeworkRequestBean;
import com.onyx.android.sun.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.sun.cloud.bean.PracticeAnswerBean;
import com.onyx.android.sun.cloud.bean.Question;
import com.onyx.android.sun.cloud.bean.QuestionData;
import com.onyx.android.sun.cloud.bean.SubjectBean;
import com.onyx.android.sun.cloud.bean.SubmitPracticeRequestBean;
import com.onyx.android.sun.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.sun.cloud.bean.TaskBean;
import com.onyx.android.sun.common.CloudApiContext;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.data.FillHomeworkData;
import com.onyx.android.sun.data.HomeworkData;
import com.onyx.android.sun.data.database.TaskAndAnswerEntity;
import com.onyx.android.sun.interfaces.HomeworkView;
import com.onyx.android.sun.requests.cloud.GetExerciseTypeRequest;
import com.onyx.android.sun.requests.cloud.GetStudyReportDetailRequest;
import com.onyx.android.sun.requests.cloud.GetSubjectRequest;
import com.onyx.android.sun.requests.cloud.HomeworkFinishedRequest;
import com.onyx.android.sun.requests.cloud.HomeworkUnfinishedRequest;
import com.onyx.android.sun.requests.cloud.SubmitPracticeRequest;
import com.onyx.android.sun.requests.cloud.TaskDetailRequest;
import com.onyx.android.sun.requests.local.FillAnswerRequest;
import com.onyx.android.sun.requests.local.GetAllQuestionRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by li on 2017/10/11.
 */

public class HomeworkPresenter {
    private HomeworkView homeworkView;
    private HomeworkData homeworkData;
    private final FillHomeworkData fillHomeworkData;

    public HomeworkPresenter(HomeworkView homeworkView) {
        this.homeworkView = homeworkView;
        homeworkData = new HomeworkData();
        fillHomeworkData = new FillHomeworkData();
    }

    public void getHomeworkUnfinishedData(String studentId) {
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.studentId = studentId;
        final HomeworkUnfinishedRequest rq = new HomeworkUnfinishedRequest(requestBean);
        homeworkData.getHomeworkUnfinishedData(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkUnfinishedResultBean resultBean = rq.getResultBean();
                if (resultBean == null) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.login_activity_request_failed));
                    return;
                }
                List<ContentBean> content = resultBean.data.content;
                if (content != null && content.size() > 0) {
                    homeworkView.setUnfinishedData(content);
                }
            }
        });
    }

    public void getHomeworkFinishedData(String studentId, String course, String startTime, String endTime, String type) {
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.status = CloudApiContext.Practices.FINISHED_STATE;
        requestBean.course = course;
        requestBean.endtime = endTime;
        requestBean.starttime = startTime;
        requestBean.studentId = studentId;
        requestBean.type = type;

        final HomeworkFinishedRequest rq = new HomeworkFinishedRequest(requestBean);
        homeworkData.getHomeworkFinishedData(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkFinishedResultBean resultBean = rq.getResultBean();
                if (resultBean == null || resultBean.data == null) {
                CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.login_activity_request_failed));
                    return;
                }

                HomeworkFinishedResultBean.FinishData data = resultBean.data;
                List<FinishContent> content = data.content;
                if (content != null && content.size() > 0) {
                    homeworkView.setFinishedData(content);
                }
            }
        });
    }

    public void getSubjects(final String studentId) {
        final GetSubjectRequest rq = new GetSubjectRequest(Integer.parseInt(studentId));
        homeworkData.getSubjects(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetSubjectBean subjects = rq.getSubjects();
                if (subjects == null) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.login_activity_request_failed));
                    return;
                }
                List<SubjectBean> subjectBeanList = subjects.data;
                if (subjectBeanList != null && subjectBeanList.size() > 0) {
                    homeworkView.setSubjects(subjectBeanList);
                    getExerciseType(studentId, subjectBeanList.get(0).id);
                }
            }
        });
    }

    public void getExerciseType(final String studentId, final int subjectId) {
        final GetExerciseTypeRequest rq = new GetExerciseTypeRequest();
        homeworkData.getExerciseType(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetSubjectBean exerciseTypes = rq.getExerciseTypes();
                if (exerciseTypes == null) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.login_activity_request_failed));
                    return;
                }
                List<SubjectBean> types = exerciseTypes.data;
                if (types != null && types.size() > 0) {
                    homeworkView.setExerciseType(exerciseTypes.data);
                    getHomeworkFinishedData(studentId, subjectId + "", null, null, types.get(0).name);
                }
            }
        });
    }

    public void getStudyReportData(String course) {
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.status = CloudApiContext.Practices.REPORT_STATE;
        requestBean.course = course;
        requestBean.studentId = "2";

        final HomeworkFinishedRequest rq = new HomeworkFinishedRequest(requestBean);
        homeworkData.getHomeworkFinishedData(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkFinishedResultBean resultBean = rq.getResultBean();
                if (resultBean == null || resultBean.data == null) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.login_activity_request_failed));
                    return;
                }

                HomeworkFinishedResultBean.FinishData data = resultBean.data;
                List<FinishContent> content = data.content;
                if (content != null && content.size() > 0) {
                    homeworkView.setReportData(content);
                }
            }
        });
    }

    public void saveTask(String taskId, List<QuestionData> data) {
        if (data == null || data.size() == 0) {
            return;
        }

        for (QuestionData question : data) {
            List<ExerciseBean> exercises = question.exercises;
            for (int i = 0; i < exercises.size(); i++) {
                ExerciseBean exerciseBean = exercises.get(i);
                List<Question> questionBeans = exerciseBean.exercises;
                for (int j = 0; j < questionBeans.size(); j++) {
                    Question questionBean = questionBeans.get(j);
                    FillAnswerRequest fillAnswerRequest = new FillAnswerRequest(taskId, questionBean.id + "", questionBean.content, question.showType, "");
                    fillHomeworkData.insertAnswer(fillAnswerRequest, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {

                        }
                    });
                }
            }
        }
    }

    public void getAllQuestion(final String taskId, final List<QuestionData> data) {
        final GetAllQuestionRequest getAllQuestionRequest = new GetAllQuestionRequest(taskId);
        fillHomeworkData.getAllQuestion(getAllQuestionRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<TaskAndAnswerEntity> taskList = getAllQuestionRequest.getTaskList();
                if (taskList == null || taskList.size() == 0) {
                    saveTask(taskId, data);
                } else {
                    homeworkView.setAnswerRecord(taskList);
                }
            }
        });
    }

    public void getTaskDetail(final int taskId) {
        final TaskDetailRequest rq = new TaskDetailRequest(taskId);
        homeworkData.getTaskDetail(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                TaskBean taskBean = rq.getTaskBean();
                if (taskBean == null || taskBean.data == null) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.login_activity_request_failed));
                    return;
                }
                taskBean.data.taskId = taskId;
                homeworkView.setTaskDetail(taskBean.data);
                getAllQuestion(taskId + "", taskBean.data.volumeExerciseDTOS);
            }
        });
    }

    public void submitAnswer(List<PracticeAnswerBean> answerList, int taskId, int studentId) {
        SubmitPracticeRequestBean submitPracticeRequestBean = new SubmitPracticeRequestBean();
        submitPracticeRequestBean.id = taskId;
        submitPracticeRequestBean.studentId = studentId;
        String answers = JSON.toJSONString(answerList);
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constants.REQUEST_HEAD), answers);
        submitPracticeRequestBean.practiceListBody = requestBody;

        final SubmitPracticeRequest rq = new SubmitPracticeRequest(submitPracticeRequestBean);
        fillHomeworkData.submitAnswers(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SubmitPracticeResultBean result = rq.getResult();
                if (result == null) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.login_activity_request_failed));
                    return;
                }
                if (Constants.OK.equals(result.msg)) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.submit_successful));
                } else {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.submit_failed));
                }
            }
        });
    }

    public void getStudyReportDetail(int id) {
        final GetStudyReportDetailRequest rq = new GetStudyReportDetailRequest(id);
        homeworkData.getStudyReportDetail(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetStudyReportDetailResultBean resultBean = rq.getStudyReportDetailResultBean();
                if (resultBean == null) {
                    return;
                }

                if (resultBean.data != null) {
                    homeworkView.setStudyReportDetail(resultBean.data);
                }
            }
        });
    }
}
