package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.adapter.HomeworkAdapter;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.databinding.HomeworkBinding;
import com.onyx.android.sun.view.DisableScrollGridManager;
import com.onyx.android.sun.view.DividerItemDecoration;

/**
 * Created by li on 2017/9/30.
 */

public class HomeWorkFragment extends BaseFragment implements View.OnClickListener {
    private HomeworkBinding homeworkBinding;

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(ViewDataBinding binding) {
        homeworkBinding = (HomeworkBinding)binding;
        homeworkBinding.homeworkRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstence()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstence(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        homeworkBinding.homeworkRecyclerView.addItemDecoration(dividerItemDecoration);
        HomeworkAdapter homeworkAdapter = new HomeworkAdapter();
        homeworkBinding.homeworkRecyclerView.setAdapter(homeworkAdapter);
        homeworkBinding.setListener(this);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getRootView() {
        return R.layout.homework_layout;
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homework_finished:
                setSelected(R.id.homework_finished);
                break;
            case R.id.homework_unfinished:
                setSelected(R.id.homework_unfinished);
                break;
            case R.id.homework_study_report:
                setSelected(R.id.homework_study_report);
                break;
        }
    }

    private void setSelected(int id) {
        homeworkBinding.homeworkFinished.setSelected(R.id.homework_finished == id);
        homeworkBinding.homeworkUnfinished.setSelected(R.id.homework_unfinished == id);
        homeworkBinding.homeworkStudyReport.setSelected(R.id.homework_study_report == id);
    }
}
