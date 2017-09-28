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
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.model.v2.ListBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by li on 2017/9/28.
 */

public class ShareToMemberAdapter extends PageRecyclerView.PageAdapter {
    private List<ListBean> data;
    private List<ListBean> list;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.share_to_member_row);
    private int column = DRApplication.getInstance().getResources().getInteger(R.integer.share_to_member_col);

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
        View view = View.inflate(DRApplication.getInstance(), R.layout.item_share_to_member_layout, null);
        return new ShareToMemberViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ShareToMemberViewHolder viewHolder = (ShareToMemberViewHolder) holder;
        final ListBean listBean = data.get(position);
        viewHolder.shareToMemberItemName.setText(listBean.child.name);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = viewHolder.shareToMemberItemChecked.isChecked();
                viewHolder.shareToMemberItemChecked.setChecked(!checked);
            }
        });
        viewHolder.shareToMemberItemChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listBean.isChecked = isChecked;
            }
        });
    }

    public List<ListBean> getSelectedData() {
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }

        for (ListBean bean : data) {
            if (bean.isChecked) {
                list.add(bean);
            }
        }
        return list;
    }

    @Override
    public void onClick(View v) {

    }

    public void setData(List<ListBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class ShareToMemberViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.share_to_member_item_name)
        TextView shareToMemberItemName;
        @Bind(R.id.share_to_member_item_checked)
        CheckBox shareToMemberItemChecked;

        ShareToMemberViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
