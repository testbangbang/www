package com.onyx.android.sun.presenter;

import com.onyx.android.sun.cloud.bean.ContentBean;
import com.onyx.android.sun.cloud.bean.FinishContent;
import com.onyx.android.sun.cloud.bean.HomeworkFinishedResultBean;
import com.onyx.android.sun.cloud.bean.HomeworkRequestBean;
import com.onyx.android.sun.cloud.bean.QuestionData;
import com.onyx.android.sun.cloud.bean.TaskBean;
import com.onyx.android.sun.common.CloudApiContext;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.data.FillHomeworkData;
import com.onyx.android.sun.data.HomeworkData;
import com.onyx.android.sun.data.database.TaskAndAnswerEntity;
import com.onyx.android.sun.interfaces.HomeworkView;
import com.onyx.android.sun.requests.cloud.HomeworkFinishedRequest;
import com.onyx.android.sun.requests.cloud.HomeworkUnfinishedRequest;
import com.onyx.android.sun.requests.cloud.TaskDetailRequest;
import com.onyx.android.sun.requests.local.GetAllQuestionRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

import java.util.ArrayList;
import java.util.List;

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

    public void getHomeworkUnfinishedData() {
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.status = CloudApiContext.Practices.UNFINISHED_STATE;
        requestBean.studentId = "2";
        final HomeworkUnfinishedRequest rq = new HomeworkUnfinishedRequest(requestBean);
        homeworkData.getHomeworkUnfinishedData(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                /*HomeworkUnfinishedResultBean resultBean = rq.getResultBean();
                if (resultBean == null) {
                    return;
                }
                List<ContentBean> content = resultBean.data.content;
                if (content != null && content.size() > 0) {
                    homeworkView.setUnfinishedData(content);
                }*/

                //fake data
                List<ContentBean> list = new ArrayList<ContentBean>();
                for (int i = 0; i < 5; i++) {
                    ContentBean bean = new ContentBean();
                    bean.title = "unit3单元测试" + i;
                    bean.type = "task";
                    bean.deadline = "2017-10-11";
                    bean.auth = "李老师";
                    bean.course = "英语";
                    bean.status = "exp";
                    bean.id = i;
                    list.add(bean);
                }
                homeworkView.setUnfinishedData(list);
            }
        });
    }

    public void getHomeworkFinishedData(String course, String startTime, String endTime, String type) {
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.status = CloudApiContext.Practices.FINISHED_STATE;
        requestBean.course = course;
        requestBean.endtime = endTime;
        requestBean.starttime = startTime;
        requestBean.studentId = "2";
        requestBean.type = type;

        final HomeworkFinishedRequest rq = new HomeworkFinishedRequest(requestBean);
        homeworkData.getHomeworkFinishedData(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                /*HomeworkFinishedResultBean resultBean = rq.getResultBean();
                if (resultBean == null || resultBean.data == null) {
                    return;
                }

                HomeworkFinishedResultBean.FinishData data = resultBean.data;
                List<FinishContent> content = data.content;
                if (content != null && content.size() > 0) {
                    homeworkView.setFinishedData(content);
                }*/
                List<FinishContent> list = new ArrayList<FinishContent>();
                for (int i = 0; i < 10; i++) {
                    FinishContent content = new FinishContent();
                    if (i > 7) {
                        content.correctTime = "2017-10-25";
                        content.submitTime = "2017-10-25";
                        content.id = i;
                        content.course = "yuwen";
                        content.auth = "yuwen";
                        content.deadline = "2017-10-25";
                        content.title = "yuwenyuwenyuwen";
                        content.type = "task";
                        content.status = "corrected";
                    } else if (i > 3) {
                        content.correctTime = "2017-10-24";
                        content.submitTime = "2017-10-24";
                        content.id = i;
                        content.course = "shuxue";
                        content.auth = "shuxue";
                        content.deadline = "2017-10-24";
                        content.title = "shuxueshuxueshuxue";
                        content.type = "task";
                        content.status = "corrected";
                    } else {
                        content.correctTime = null;
                        content.submitTime = "2017-10-23";
                        content.id = i;
                        content.course = "yingyu";
                        content.auth = "yingyu";
                        content.deadline = "2017-10-24";
                        content.title = "yingyuyingyuyingyu";
                        content.type = "task";
                    }
                    list.add(content);
                }
                homeworkView.setFinishedData(list);
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

    public void saveTask(List<QuestionData> data) {
        //TODO:to complete params   fake data
        if (data == null || data.size() == 0) {
            return;
        }

        /*for (QuestionData question : data) {
            Question exercise = question.exercise;
            FillAnswerRequest fillAnswerRequest = new FillAnswerRequest("1", exercise.id + "", exercise.type, exercise.question, exercise.userAnswer);
            fillHomeworkData.insertAnswer(fillAnswerRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {

                }
            });
        }*/
    }

    public void getAllQuestion(final String taskId, final List<QuestionData> data) {
        final GetAllQuestionRequest getAllQuestionRequest = new GetAllQuestionRequest(taskId);
        fillHomeworkData.getAllQuestion(getAllQuestionRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<TaskAndAnswerEntity> taskList = getAllQuestionRequest.getTaskList();
                if (taskList == null || taskList.size() == 0) {
                    saveTask(data);
                } else {
                    homeworkView.setAnswerRecord(taskList);
                }
            }
        });
    }

    public void getTaskDetail(int id) {
        final TaskDetailRequest rq = new TaskDetailRequest(id);
        homeworkData.getTaskDetail(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                TaskBean taskBean = rq.getTaskBean();
                if (taskBean == null || taskBean.data == null) {
                    CommonNotices.show("请求数据错误");
                    return;
                }

                homeworkView.setTaskDetail(taskBean.data);
                //TODO: getAllQuestion(taskBean.data);
            }
        });

        //fake data
        /*QuestionDetail questionDetail = new QuestionDetail();
        List<QuestionData> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            QuestionData data = new QuestionData();
            Question question = new Question();
            question.id = i;
            question.question = "shi wan ge wei shen me?";
            if (i == 7) {
                question.type = "objective";
                data.exercise = question;
                list.add(data);
                break;
            }
            question.type = "choice";
            List<Map<String, String>> selection = new ArrayList<>();

            Map<String, String> map = new HashMap<>();
            map.put("key", "A");
            map.put("value", "aaaa");
            selection.add(map);

            map = new HashMap<>();
            map.put("key", "B");
            map.put("value", "bbbb");
            selection.add(map);

            map = new HashMap<>();
            map.put("key", "C");
            map.put("value", "cccc");
            selection.add(map);

            map = new HashMap<>();
            map.put("key", "D");
            map.put("value", "dddd");
            selection.add(map);
            question.selection = selection;
            data.exercise = question;
            list.add(data);
        }
        questionDetail.data = list;
        homeworkView.setTaskDetail(questionDetail);
        getAllQuestion("1", questionDetail.data);*/
    }
}
