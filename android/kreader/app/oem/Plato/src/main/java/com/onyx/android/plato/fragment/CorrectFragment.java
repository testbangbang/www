package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.adapter.FillHomeworkAdapter;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.cloud.bean.ExerciseMessageBean;
import com.onyx.android.plato.cloud.bean.GetCorrectedTaskBean;
import com.onyx.android.plato.cloud.bean.QuestionData;
import com.onyx.android.plato.cloud.bean.QuestionViewBean;
import com.onyx.android.plato.databinding.CorrectDataBinding;
import com.onyx.android.plato.event.HomeworkFinishedEvent;
import com.onyx.android.plato.event.TimerEvent;
import com.onyx.android.plato.interfaces.CorrectView;
import com.onyx.android.plato.presenter.CorrectPresenter;
import com.onyx.android.plato.utils.StringUtil;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by li on 2017/10/16.
 */

public class CorrectFragment extends BaseFragment implements View.OnClickListener, CorrectView {
    private CorrectDataBinding correctDataBinding;
    private ContentBean content;
    private FillHomeworkAdapter adapter;
    private String title;
    private CorrectPresenter presenter;

    @Override
    protected void loadData() {
        if (presenter == null) {
            presenter = new CorrectPresenter(this);
        }
        if (content.correctTime != null) {
            presenter.getCorrectData(content.id);
        }
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        correctDataBinding = (CorrectDataBinding) binding;
        title = String.format(SunApplication.getInstance().
                getString(R.string.homework_unfinished_title_format), StringUtil
                .transitionHomeworkType(content.type), content.title);
        correctDataBinding.correctTitleBar.setTitle(title);
        correctDataBinding.correctRecycler.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        correctDataBinding.correctRecycler.addItemDecoration(itemDecoration);
        adapter = new FillHomeworkAdapter();
        correctDataBinding.correctRecycler.setAdapter(adapter);
        setVisible();
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
        correctDataBinding.correctTitleBar.titleBarTitle.setOnClickListener(this);
        correctDataBinding.correctTitleBar.titleBarSubmit.setVisibility(View.GONE);
        correctDataBinding.correctTitleBar.titleBarRecord.setVisibility(View.GONE);
    }

    @Override
    protected int getRootView() {
        return R.layout.correct_fragment_layout;
    }

    @Override
    public boolean onKeyBack() {
        EventBus.getDefault().post(new HomeworkFinishedEvent());
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnTimerEvent(TimerEvent event) {
        correctDataBinding.setCount(event.getResult());
        if (event.getResult() == 0 && content.correctTime == null) {
            EventBus.getDefault().post(new HomeworkFinishedEvent());
        }
    }

    public void setStartTimer(ContentBean content) {
        this.content = content;
        TimerEvent timerEvent = new TimerEvent();
        timerEvent.timeCountDown(3);
        if (correctDataBinding != null) {
            setVisible();
        }

        if (presenter != null && content.correctTime != null) {
            presenter.getCorrectData(content.id);
        }
    }

    private void setVisible() {
        correctDataBinding.setCorrected(content.correctTime != null);
        correctDataBinding.setCount(3);
        correctDataBinding.correctNoCorrect.setVisibility(content.correctTime != null ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new HomeworkFinishedEvent());
    }

    @Override
    public void setCorrectData(GetCorrectedTaskBean data) {
        List<QuestionData> questionDataList = data.volumeExerciseIdsDTO.volumeExerciseDTOS;
        List<ExerciseMessageBean> questionMessages = data.exerciseMessageDtos;
        if (questionDataList != null && questionDataList.size() > 0 &&
                questionMessages != null && questionMessages.size() > 0) {
            resolveAdapterData(questionDataList, questionMessages, content.practiceId);
        }
    }

    @Override
    public void setQuestionBeanList(List<QuestionViewBean> questionList) {
        if (adapter == null) {
            return;
        }
        correctDataBinding.correctRecycler.scrollToPosition(0);
        adapter.setFinished(true);
        adapter.setData(questionList, title, content.practiceId);
    }

    @Override
    public void clearAdapter() {
        if (adapter != null) {
            adapter.clearData();
        }
    }

    private void resolveAdapterData(List<QuestionData> questionDataList, List<ExerciseMessageBean> questionMessages, int id) {
        presenter.resolveAdapterData(questionDataList, questionMessages, id);
    }
}
