package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.adapter.FillHomeworkAdapter;
import com.onyx.android.plato.adapter.HomeworkRecordAdapter;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.cloud.bean.FinishContent;
import com.onyx.android.plato.cloud.bean.GetCorrectedTaskBean;
import com.onyx.android.plato.cloud.bean.PracticeAnswerBean;
import com.onyx.android.plato.cloud.bean.QuestionDetail;
import com.onyx.android.plato.cloud.bean.QuestionViewBean;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity;
import com.onyx.android.plato.cloud.bean.StudyReportDetailBean;
import com.onyx.android.plato.databinding.FillHomeworkBinding;
import com.onyx.android.plato.event.BackToHomeworkFragmentEvent;
import com.onyx.android.plato.event.SubjectiveResultEvent;
import com.onyx.android.plato.event.UnansweredEvent;
import com.onyx.android.plato.interfaces.CorrectView;
import com.onyx.android.plato.interfaces.HomeworkView;
import com.onyx.android.plato.presenter.CorrectPresenter;
import com.onyx.android.plato.presenter.HomeworkPresenter;
import com.onyx.android.plato.utils.StringUtil;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2017/10/12.
 */

public class FillHomeworkFragment extends BaseFragment implements HomeworkView, View.OnClickListener, CorrectView {
    private HomeworkPresenter homeworkPresenter;
    private FillHomeworkBinding fillHomeworkBinding;
    private String type;
    private String title;
    private int id;
    private FillHomeworkAdapter fillHomeworkAdapter;
    private HomeworkRecordAdapter homeworkRecordAdapter;
    private QuestionDetail data;
    private CorrectPresenter correctPresenter;

    @Override
    protected void loadData() {
        homeworkPresenter = new HomeworkPresenter(this);
        correctPresenter = new CorrectPresenter(this);
        homeworkPresenter.getTaskDetail(id);
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        fillHomeworkBinding = (FillHomeworkBinding) binding;
        fillHomeworkBinding.fillHomeworkRecycler.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        fillHomeworkBinding.fillHomeworkRecycler.addItemDecoration(dividerItemDecoration);
        fillHomeworkBinding.fillHomeworkTitleBar.setTitle(String.format(getResources().getString(R.string.homework_unfinished_title_format),
                StringUtil.transitionHomeworkType(type), title));
        fillHomeworkBinding.fillHomeworkTitleBar.setRecord(getResources().getString(R.string.file_homework_record));
        fillHomeworkAdapter = new FillHomeworkAdapter();
        fillHomeworkBinding.fillHomeworkRecycler.setAdapter(fillHomeworkAdapter);
        homeworkRecordAdapter = new HomeworkRecordAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void initListener() {
        fillHomeworkBinding.fillHomeworkTitleBar.titleBarTitle.setOnClickListener(this);
        fillHomeworkBinding.fillHomeworkTitleBar.titleBarRecord.setOnClickListener(this);
        fillHomeworkBinding.fillHomeworkTitleBar.titleBarSubmit.setOnClickListener(this);
    }

    @Override
    protected int getRootView() {
        return R.layout.fill_homework_fragment_layout;
    }

    @Override
    public boolean onKeyBack() {
        EventBus.getDefault().post(new BackToHomeworkFragmentEvent());
        return true;
    }

    @Override
    public void setUnfinishedData(List<ContentBean> content) {

    }

    @Override
    public void setFinishedData(List<FinishContent> content) {

    }

    @Override
    public void setReportData(List<FinishContent> content) {

    }

    @Override
    public void setTaskDetail(QuestionDetail data) {
        this.data = data;
        showTaskTitle();
        if (data.volumeExerciseDTOS != null && data.volumeExerciseDTOS.size() > 0) {
            correctPresenter.resolveAdapterData(data.volumeExerciseDTOS, null);
        }
    }

    @Override
    public void setAnswerRecord(List<TaskAndAnswerEntity> taskList) {
        homeworkRecordAdapter.setData(taskList);
    }

    @Override
    public void setStudyReportDetail(StudyReportDetailBean data) {

    }

    @Override
    public void setSubjects(List<SubjectBean> subjects) {

    }

    @Override
    public void setExerciseType(List<SubjectBean> exerciseTypes) {

    }

    public void setTaskId(int id, String type, String title) {
        this.id = id;
        this.type = type;
        this.title = title;
        if (homeworkPresenter != null) {
            homeworkPresenter.getTaskDetail(id);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_title:
                EventBus.getDefault().post(new BackToHomeworkFragmentEvent());
                break;
            case R.id.title_bar_record:
                if (data != null) {
                    setTitleBarRecord();
                }
                break;
            case R.id.title_bar_submit:
                submitAnswer();
                break;
        }
    }

    private void submitAnswer() {
        if (fillHomeworkAdapter != null) {
            List<PracticeAnswerBean> list = new ArrayList<>();
            List<QuestionViewBean> questionList = fillHomeworkAdapter.getQuestionList();
            for (QuestionViewBean bean : questionList) {
                if (StringUtil.isNullOrEmpty(bean.getUserAnswer())) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.submit_all_questions));
                    return;
                }
                PracticeAnswerBean answerBean = new PracticeAnswerBean();
                answerBean.id = bean.getId();
                answerBean.answer = bean.getUserAnswer();
                list.add(answerBean);
            }
            //TODO:fake student id
            homeworkPresenter.submitAnswer(list, id, 1);
        }
    }

    private void showTaskTitle() {
        fillHomeworkBinding.setTaskName(data.name);
        fillHomeworkBinding.setShowTaskType(data.volumeType != 1);
        fillHomeworkBinding.setWholeScore(data.volumeScore);
    }

    private void setTitleBarRecord() {
        if (getResources().getString(R.string.file_homework_record).equals(fillHomeworkBinding.fillHomeworkTitleBar.getRecord())) {
            fillHomeworkBinding.fillHomeworkTitleBar.setRecord(getResources().getString(R.string.question));
            fillHomeworkBinding.fillHomeworkRecycler.setIntercepted(true);
            fillHomeworkBinding.fillHomeworkRecycler.setAdapter(homeworkRecordAdapter);
            homeworkPresenter.getAllQuestion(data.taskId + "", null);
        } else {
            fillHomeworkBinding.fillHomeworkTitleBar.setRecord(getResources().getString(R.string.file_homework_record));
            fillHomeworkBinding.fillHomeworkRecycler.setIntercepted(false);
            fillHomeworkBinding.fillHomeworkRecycler.setAdapter(fillHomeworkAdapter);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnansweredEvent(UnansweredEvent event) {
        setTitleBarRecord();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubjectiveResultEvent(SubjectiveResultEvent event) {
        if (fillHomeworkAdapter == null) {
            return;
        }

        List<QuestionViewBean> questionList = fillHomeworkAdapter.getQuestionList();
        if (questionList != null && questionList.size() > 0) {
            for (QuestionViewBean bean : questionList) {
                if (bean.getId() == Integer.parseInt(event.getQuestionId())) {
                    bean.setUserAnswer(event.getQuestionId());
                    fillHomeworkAdapter.insertAnswer(data.taskId, bean);
                }
            }
        }
        fillHomeworkAdapter.notifyDataSetChanged();
    }

    @Override
    public void setCorrectData(GetCorrectedTaskBean data) {

    }

    @Override
    public void setQuestionBeanList(List<QuestionViewBean> questionList) {
        fillHomeworkBinding.fillHomeworkRecycler.scrollToPosition(0);
        if (fillHomeworkAdapter != null) {
            fillHomeworkAdapter.setData(questionList, title, data.taskId);
        }
    }
}
