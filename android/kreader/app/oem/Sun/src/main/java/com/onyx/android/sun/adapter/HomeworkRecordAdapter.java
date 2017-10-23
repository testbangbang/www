package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.AnswerRecordBean;
import com.onyx.android.sun.cloud.bean.Question;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.data.database.TaskAndAnswerEntity;
import com.onyx.android.sun.databinding.ItemHomeworkRecordBinding;
import com.onyx.android.sun.view.DisableScrollGridManager;
import com.onyx.android.sun.view.DividerItemDecoration;
import com.onyx.android.sun.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/10/13.
 */

public class HomeworkRecordAdapter extends PageRecyclerView.PageAdapter {
    private DividerItemDecoration dividerItemDecoration;
    private final List<AnswerRecordBean> data = new ArrayList<>();

    public HomeworkRecordAdapter() {
        dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(true);
        /*for (int i = 0; i < 2; i++) {
            AnswerRecordBean bean = new AnswerRecordBean();
            if(i == 0) {
                List<Question> list = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    Question question = new Question();
                    question.id = j;
                    question.userAnswer = j == 5 ? null : "A";
                    question.type = "choice";
                    list.add(question);
                }
                bean.list = list;
                bean.title = ;
            } else {
                List<Question> list = new ArrayList<>();
                for (int j = 0; j < 4; j++) {
                    Question question = new Question();
                    question.id = j;
                    question.userAnswer = j == 2 ? null : "B";
                    question.type = "objective";
                    list.add(question);
                }
                bean.list = list;
                bean.title = "主观题";
            }
            data.add(bean);
        }*/
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
        if("客观题".equals(answerRecordBean.title)) {
            homeworkResultAdapter.setRowOrCol(2, 9);
        }else {
            homeworkResultAdapter.setRowOrCol(2, 3);
        }
        homeworkRecordBinding.homeworkRecordRecycler.setAdapter(homeworkResultAdapter);
        homeworkResultAdapter.setData(answerRecordBean.list);
        homeworkRecordBinding.executePendingBindings();
    }

    @Override
    public void onClick(View view) {

    }

    public void setData(List<TaskAndAnswerEntity> taskAndAnswerList) {
        handleData(Constants.QUESTION_TYPE_CHOICE, "客观题", taskAndAnswerList);
        notifyDataSetChanged();
    }

    private void handleData(String type, String title, List<TaskAndAnswerEntity> taskAndAnswerList) {
        AnswerRecordBean bean = new AnswerRecordBean();
        List<TaskAndAnswerEntity> list = new ArrayList<>();
        Iterator<TaskAndAnswerEntity> iterator = taskAndAnswerList.iterator();
        while (iterator.hasNext()) {
            TaskAndAnswerEntity entity = iterator.next();
            if (type.equals(entity.type)){
                bean.title = title;
                list.add(entity);
                iterator.remove();
            }
        }
        bean.list = list;
        data.add(bean);
        if(taskAndAnswerList != null && taskAndAnswerList.size() > 0) {
            handleData(Constants.QUESTION_TYPE_OBJECTIVE, "主观题", taskAndAnswerList);
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
