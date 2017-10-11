package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.model.v2.GroupBean;

import java.util.ArrayList;
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
    private List<GroupBean> selected;
    private String impressionId;
    private String[] childrenId;

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
        final GroupBean groupBean = data.get(position);
        viewHolder.groupName.setText(groupBean.name);
        viewHolder.groupInto.setOnClickListener(this);
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.groupInto.setTag(position);
        viewHolder.checkBook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                groupBean.isChecked = isChecked;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if(tag == null) {
            return;
        }
        int position = (int) tag;
        GroupBean groupBean = data.get(position);
        ActivityManager.startShareToMemberActivity(DRApplication.getInstance(), groupBean._id, groupBean.name, impressionId, childrenId);
    }

    public List<GroupBean> getSelectData() {
        if(selected == null) {
            selected = new ArrayList<>();
        }else {
            selected.clear();
        }

        for (GroupBean bean : data) {
            if (bean.isChecked) {
                selected.add(bean);
            }
        }
        return selected;
    }

    public void setData(List<GroupBean> data, String impressionId, String[] childrenId) {
        this.data = data;
        this.impressionId = impressionId;
        this.childrenId = childrenId;
        notifyDataSetChanged();
    }

    static class ShareBookReportViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.share_book_report_group_item)
        TextView groupName;
        @Bind(R.id.share_book_report_check_item)
        CheckBox checkBook;
        @Bind(R.id.share_book_report_group_member)
        ImageView groupInto;

        ShareBookReportViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
