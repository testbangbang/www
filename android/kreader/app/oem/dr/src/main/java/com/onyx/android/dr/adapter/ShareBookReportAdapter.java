package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.model.v2.GroupBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by li on 2017/9/27.
 */

public class ShareBookReportAdapter extends PageRecyclerView.PageAdapter {
    private List<GroupBean> data;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.share_book_report_row);
    private int column = DRApplication.getInstance().getResources().getInteger(R.integer.share_book_report_col);

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return column;
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(DRApplication.getInstance(), R.layout.share_book_report_item, null);
        return new ShareBookReportViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ShareBookReportViewHolder viewHolder = (ShareBookReportViewHolder) holder;
        GroupBean groupBean = data.get(position);
        viewHolder.groupName.setText(groupBean.name);
    }

    @Override
    public void onClick(View v) {

    }

    public void setData(List<GroupBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class ShareBookReportViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.share_book_report_group_item)
        TextView groupName;
        @Bind(R.id.share_book_report_check_item)
        CheckBox checkBook;

        ShareBookReportViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
