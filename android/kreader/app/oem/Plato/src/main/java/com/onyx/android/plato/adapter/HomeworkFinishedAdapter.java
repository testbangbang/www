package com.onyx.android.plato.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.cloud.bean.HandlerFinishContent;
import com.onyx.android.plato.databinding.ItemContentBinding;
import com.onyx.android.plato.databinding.ItemTimeBinding;
import com.onyx.android.plato.event.ToCorrectEvent;
import com.onyx.android.plato.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by li on 2017/10/10.
 */

public class HomeworkFinishedAdapter extends PageRecyclerView.PageAdapter {
    private List<HandlerFinishContent> contents = new ArrayList<HandlerFinishContent>();
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
            timeBinding.setCorrectTime(contents.get(position).time);
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
        if (tag == null) {
            return;
        }

        int position = (int) tag;
        HandlerFinishContent handlerFinishContent = contents.get(position);
        EventBus.getDefault().post(new ToCorrectEvent(handlerFinishContent.content));
    }

    public void setData(List<ContentBean> data) {
        handleData(data);
        notifyDataSetChanged();
    }

    private void handleData(List<ContentBean> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        ContentBean finishContent = data.get(0);
        HandlerFinishContent handlerContent = new HandlerFinishContent();
        handlerContent.type = TIME_TYPE;
        handlerContent.time = finishContent.submitTime;
        contents.add(handlerContent);

        if (finishContent.submitTime != null) {
            Iterator<ContentBean> iterator = data.iterator();
            while (iterator.hasNext()) {
                ContentBean content = iterator.next();
                if (finishContent.submitTime.equals(content.submitTime)) {
                    HandlerFinishContent handlerFinishContent = new HandlerFinishContent();
                    handlerFinishContent.content = content;
                    handlerFinishContent.type = CONTENT_TYPE;
                    handlerFinishContent.time = content.submitTime;
                    contents.add(handlerFinishContent);
                    iterator.remove();
                } else {
                    handleData(data);
                    iterator = data.iterator();
                }
            }
        }
    }

    public void clear() {
        if (contents != null && contents.size() > 0) {
            contents.clear();
            notifyDataSetChanged();
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
