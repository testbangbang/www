package com.onyx.android.plato.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.AnswerRecordBean;
import com.onyx.android.plato.common.Constants;
import com.onyx.android.plato.data.database.TaskAndAnswerEntity;
import com.onyx.android.plato.databinding.ItemHomeworkRecordBinding;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;
import com.onyx.android.plato.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by li on 2017/10/13.
 */

public class HomeworkRecordAdapter extends PageRecyclerView.PageAdapter {
    private DividerItemDecoration dividerItemDecoration;
    private final List<AnswerRecordBean> data = new ArrayList<>();

    public HomeworkRecordAdapter() {
        dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
    }

    @Override
    public int getRowCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.homework_record_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.homework_record_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework_record, parent, false);
        return new HomeworkRecordViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeworkRecordViewHolder viewHolder = (HomeworkRecordViewHolder) holder;
        ItemHomeworkRecordBinding homeworkRecordBinding = viewHolder.getHomeworkRecordBinding();
        AnswerRecordBean answerRecordBean = data.get(position);
        homeworkRecordBinding.setTitle(answerRecordBean.title);
        HomeworkResultAdapter homeworkResultAdapter = new HomeworkResultAdapter();
        homeworkRecordBinding.homeworkRecordRecycler.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        homeworkRecordBinding.homeworkRecordRecycler.addItemDecoration(dividerItemDecoration);
        if (SunApplication.getInstance().getResources().getString(R.string.objective_item).equals(answerRecordBean.title)) {
            homeworkResultAdapter.setRowOrCol(SunApplication.getInstance().getResources().getInteger(R.integer.homework_result_objective_row),
                    SunApplication.getInstance().getResources().getInteger(R.integer.homework_result_objective_col));
        } else {
            homeworkResultAdapter.setRowOrCol(SunApplication.getInstance().getResources().getInteger(R.integer.homework_result_subjective_row),
                    SunApplication.getInstance().getResources().getInteger(R.integer.homework_result_subjective_col));
        }
        homeworkRecordBinding.homeworkRecordRecycler.setAdapter(homeworkResultAdapter);
        homeworkResultAdapter.setData(answerRecordBean.list);
        homeworkRecordBinding.executePendingBindings();
    }

    @Override
    public void onClick(View view) {

    }

    public void setData(List<TaskAndAnswerEntity> taskAndAnswerList) {
        if (data != null && data.size() > 0) {
            data.clear();
        }
        handleData(Constants.QUESTION_TYPE_CHOICE, Constants.QUESTION_TYPE_CHOICE, SunApplication.getInstance().getResources().getString(R.string.objective_item), taskAndAnswerList);
        notifyDataSetChanged();
    }

    private void handleData(String type, String anotherType, String title, List<TaskAndAnswerEntity> taskAndAnswerList) {
        boolean isIntoLoop = false;
        AnswerRecordBean bean = new AnswerRecordBean();
        List<TaskAndAnswerEntity> list = new ArrayList<>();
        Iterator<TaskAndAnswerEntity> iterator = taskAndAnswerList.iterator();
        while (iterator.hasNext()) {
            TaskAndAnswerEntity entity = iterator.next();
            if (type.equals(entity.type) || anotherType.equals(entity.type)) {
                isIntoLoop = true;
                bean.title = title;
                list.add(entity);
                iterator.remove();
            }
        }
        bean.list = list;
        data.add(bean);
        if (taskAndAnswerList != null && taskAndAnswerList.size() > 0 && isIntoLoop) {
            handleData(Constants.QUESTION_TYPE_OBJECTIVE, Constants.QUESTION_TYPE_BLANK, SunApplication.getInstance().getResources().getString(R.string.subjective_item), taskAndAnswerList);
        }
    }

    static class HomeworkRecordViewHolder extends RecyclerView.ViewHolder {
        private ItemHomeworkRecordBinding homeworkRecordBinding;

        public HomeworkRecordViewHolder(View itemView) {
            super(itemView);
            homeworkRecordBinding = (ItemHomeworkRecordBinding) DataBindingUtil.bind(itemView);
        }

        public ItemHomeworkRecordBinding getHomeworkRecordBinding() {
            return homeworkRecordBinding;
        }
    }
}
