package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.adapter.FillHomeworkAdapter;
import com.onyx.android.sun.adapter.HomeworkRecordAdapter;
import com.onyx.android.sun.cloud.bean.ContentBean;
import com.onyx.android.sun.cloud.bean.FinishContent;
import com.onyx.android.sun.cloud.bean.QuestionData;
import com.onyx.android.sun.cloud.bean.QuestionDetail;
import com.onyx.android.sun.databinding.FillHomeworkBinding;
import com.onyx.android.sun.event.BackToHomeworkFragmentEvent;
import com.onyx.android.sun.interfaces.HomeworkView;
import com.onyx.android.sun.presenter.HomeworkPresenter;
import com.onyx.android.sun.utils.StringUtil;
import com.onyx.android.sun.view.DisableScrollGridManager;
import com.onyx.android.sun.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by li on 2017/10/12.
 */

public class FillHomeworkFragment extends BaseFragment implements HomeworkView, View.OnClickListener {
    private HomeworkPresenter homeworkPresenter;
    private FillHomeworkBinding fillHomeworkBinding;
    private String type;
    private String title;
    private int id;
    private FillHomeworkAdapter fillHomeworkAdapter;
    private HomeworkRecordAdapter homeworkRecordAdapter;

    @Override
    protected void loadData() {
        homeworkPresenter = new HomeworkPresenter(this);
        homeworkPresenter.getTaskDetail(id);
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        fillHomeworkBinding = (FillHomeworkBinding) binding;
        fillHomeworkBinding.fillHomeworkRecycler.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        fillHomeworkBinding.fillHomeworkRecycler.addItemDecoration(dividerItemDecoration);
        fillHomeworkBinding.fillHomeworkTitleBar.titleBarTitle.setText(
                String.format(getResources().getString(R.string.homework_unfinished_title_format), StringUtil.transitionHomeworkType(type), title));
        fillHomeworkAdapter = new FillHomeworkAdapter();
        fillHomeworkBinding.fillHomeworkRecycler.setAdapter(fillHomeworkAdapter);
        fillHomeworkBinding.fillHomeworkTitleBar.titleBarTitle.setOnClickListener(this);
        fillHomeworkBinding.fillHomeworkTitleBar.titleBarRecord.setOnClickListener(this);
        fillHomeworkBinding.fillHomeworkTitleBar.titleBarSubmit.setOnClickListener(this);
        homeworkRecordAdapter = new HomeworkRecordAdapter();
    }

    @Override
    protected void initListener() {

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
        List<QuestionData> questions = data.data;
        if (fillHomeworkAdapter != null && questions != null && questions.size() > 0) {
            fillHomeworkAdapter.setData(questions);
        }
    }

    public void setTaskId(int id, String type, String title) {
        this.id = id;
        this.type = type;
        this.title = title;
        if(homeworkPresenter != null) {
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
                fillHomeworkBinding.fillHomeworkRecycler.setAdapter(homeworkRecordAdapter);
                break;
            case R.id.title_bar_submit:
                break;
        }
    }
}
