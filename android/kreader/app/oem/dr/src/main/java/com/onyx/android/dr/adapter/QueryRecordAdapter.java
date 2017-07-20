package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.data.database.QueryRecordEntity;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-6-28.
 */

public class QueryRecordAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<QueryRecordEntity> list;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClick(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public void setDatas(List<QueryRecordEntity> list) {
        this.list = list;
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.query_recor_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.query_record_column);
    }

    @Override
    public int getDataCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_dict_query, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        QueryRecordEntity bean = list.get(position);
        viewHolder.tabMenuTitle.setText(bean.word);
        viewHolder.rootView.setTag(position);
        viewHolder.rootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(v, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tab_dict_title)
        TextView tabMenuTitle;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
