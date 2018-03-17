package com.onyx.jdread.shop.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.SubjectListBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.event.BookItemClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2018/1/3.
 */

public class SubjectListAdapter extends PageAdapter<PageRecyclerView.ViewHolder, ResultBookBean, ResultBookBean> {

    private EventBus eventBus;
    private int row = ResManager.getInteger(R.integer.subject_list_recycle_viw_row);
    private int col = ResManager.getInteger(R.integer.subject_list_recycle_viw_col);

    public SubjectListAdapter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setRowAndCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return col;
    }

    @Override
    public int getDataCount() {
        return getItemVMList().size();
    }

    @Override
    public PageRecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_list, null));
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        final ResultBookBean bookBean = getItemVMList().get(position);
        ModelViewHolder viewHolder = (ModelViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(bookBean);
    }

    @Override
    public void setRawData(List<ResultBookBean> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        if (eventBus != null && getItemVMList() != null) {
            eventBus.post(new BookItemClickEvent(getItemVMList().get(position)));
        }
    }

    static class ModelViewHolder extends PageRecyclerView.ViewHolder {

        private final SubjectListBinding bind;

        public ModelViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public SubjectListBinding getBind() {
            return bind;
        }

        public void bindTo(ResultBookBean bookBean) {
            bind.setBookBean(bookBean);
            bind.executePendingBindings();
        }
    }
}