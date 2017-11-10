package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.util.Log;
import android.view.View;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.adapter.FillHomeworkAdapter;
import com.onyx.android.sun.cloud.bean.FinishContent;
import com.onyx.android.sun.cloud.bean.QuestionData;
import com.onyx.android.sun.databinding.CorrectDataBinding;
import com.onyx.android.sun.event.BackToHomeworkFragmentEvent;
import com.onyx.android.sun.event.TimerEvent;
import com.onyx.android.sun.interfaces.CorrectView;
import com.onyx.android.sun.presenter.CorrectPresenter;
import com.onyx.android.sun.presenter.HomeworkPresenter;
import com.onyx.android.sun.utils.StringUtil;
import com.onyx.android.sun.view.DisableScrollGridManager;
import com.onyx.android.sun.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by li on 2017/10/16.
 */

public class CorrectFragment extends BaseFragment implements View.OnClickListener, CorrectView {
    private CorrectDataBinding correctDataBinding;
    private FinishContent content;
    private FillHomeworkAdapter adapter;
    private String title;
    private CorrectPresenter presenter;

    @Override
    protected void loadData() {
        presenter = new CorrectPresenter(this);
        presenter.getCorrectData();
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
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnTimerEvent(TimerEvent event) {
        correctDataBinding.setCount(event.getResult());
        if(event.getResult() == 0 && content.correctTime == null) {
            EventBus.getDefault().post(new BackToHomeworkFragmentEvent());
        }
    }

    public void setStartTimer(FinishContent content) {
        this.content = content;
        TimerEvent timerEvent = new TimerEvent();
        timerEvent.timeCountDown(3);
        if(correctDataBinding != null) {
            setVisible();
        }

        if(presenter != null) {
            presenter.getCorrectData();
        }
    }

    private void setVisible() {
        correctDataBinding.setCorrected(content.correctTime != null);
        correctDataBinding.setCount(3);
        correctDataBinding.correctNoCorrect.setVisibility(content.correctTime != null ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new BackToHomeworkFragmentEvent());
    }

    @Override
    public void setCorrectList(List<QuestionData> data) {
        if(adapter != null) {
            adapter.setFinished(true);
            //TODO: adapter.setData(data, title);
        }
    }
}
