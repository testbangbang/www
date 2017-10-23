package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.FinishContent;
import com.onyx.android.sun.cloud.bean.HandlerFinishContent;
import com.onyx.android.sun.databinding.ItemContentBinding;
import com.onyx.android.sun.databinding.ItemTimeBinding;
import com.onyx.android.sun.event.ToCorrectEvent;
import com.onyx.android.sun.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by li on 2017/10/10.
 */

public class HomeworkFinishedAdapter extends PageRecyclerView.PageAdapter {
    private List<HandlerFinishContent> contents = new ArrayList<HandlerFinishContent>();
    private List<String> submitTimes = new ArrayList<>();
    private static final int TIME_TYPE = 0;
    private static final int CONTENT_TYPE = 1;

    @Override
    public int getRowCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.homework_finish_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.homework_finish_col);
    }

    @Override
    public int getDataCount() {
        return contents == null ? 0 : contents.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < contents.size()) {
            return contents.get(position).type;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case TIME_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework_finished_time_layout, parent, false);
                return new TimeViewHolder(view);
            case CONTENT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework_finished_content_layout, parent, false);
                return new ContentViewHolder(view);
        }
        return null;
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TimeViewHolder) {
            TimeViewHolder timeViewHolder = (TimeViewHolder) holder;
            ItemTimeBinding timeBinding = timeViewHolder.getTimeBinding();
            timeBinding.setCorrectTime(submitTimes.get(position));
            timeBinding.executePendingBindings();
        } else if (holder instanceof ContentViewHolder) {
            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            ItemContentBinding contentBinding = contentViewHolder.getContentBinding();
            contentBinding.setContent(contents.get(position));
            contentViewHolder.itemView.setOnClickListener(this);
            contentViewHolder.itemView.setTag(position);
            contentBinding.executePendingBindings();
        }
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if(tag == null) {
            return;
        }

        int position = (int) tag;
        HandlerFinishContent handlerFinishContent = contents.get(position);
        EventBus.getDefault().post(new ToCorrectEvent(handlerFinishContent.content.correctTime != null));
    }

    public void setData(List<FinishContent> data) {
        handleData(data);
        notifyDataSetChanged();
    }

    private void handleData(List<FinishContent> data) {
        FinishContent finishContent = data.get(0);
        HandlerFinishContent handlerContent = new HandlerFinishContent();
        handlerContent.type = TIME_TYPE;
        contents.add(handlerContent);

        if (finishContent.submitTime != null) {
            submitTimes.add(finishContent.submitTime);
            Iterator<FinishContent> iterator = data.iterator();
            while (iterator.hasNext()) {
                FinishContent content = iterator.next();
                if (finishContent.submitTime.equals(content.submitTime)) {
                    HandlerFinishContent handlerFinishContent = new HandlerFinishContent();
                    handlerFinishContent.content = content;
                    handlerFinishContent.type = CONTENT_TYPE;
                    contents.add(handlerFinishContent);
                    iterator.remove();
                } else {
                    handleData(data);
                }
            }
        }
    }

    static class TimeViewHolder extends RecyclerView.ViewHolder {
        private ItemTimeBinding timeBinding;

        public TimeViewHolder(View itemView) {
            super(itemView);
            timeBinding = (ItemTimeBinding) DataBindingUtil.bind(itemView);
        }

        public ItemTimeBinding getTimeBinding() {
            return timeBinding;
        }
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        private ItemContentBinding contentBinding;

        public ContentViewHolder(View itemView) {
            super(itemView);
            contentBinding = (ItemContentBinding) DataBindingUtil.bind(itemView);
        }

        public ItemContentBinding getContentBinding() {
            return contentBinding;
        }
    }
}
