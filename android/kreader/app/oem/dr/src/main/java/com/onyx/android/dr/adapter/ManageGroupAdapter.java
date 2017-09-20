package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.GroupMemberBean;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 2017/8/31.
 */
public class ManageGroupAdapter extends PageRecyclerView.PageAdapter<ManageGroupAdapter.ViewHolder> {
    private List<GroupMemberBean> dataList;
    private OnItemClickListener onItemClickListener;

    public void setDataList(List<GroupMemberBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.item_group_info_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.good_sentence_tab_column);
    }

    @Override
    public int getDataCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_manage_group_info, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        GroupMemberBean bean = dataList.get(position);
        holder.state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    public interface OnItemClickListener {
        void setOnItemClick(int position, boolean isCheck);
        void setOnItemCheckedChanged(int position, boolean isCheck);
    }

    public void setOnItemListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.manage_group_info_item_serial_number)
        TextView serialNumber;
        @Bind(R.id.manage_group_info_item_group_name)
        TextView groupName;
        @Bind(R.id.manage_group_info_item_identity)
        TextView identity;
        @Bind(R.id.manage_group_info_item_people_number)
        TextView peopleNumber;
        @Bind(R.id.manage_group_info_item_news)
        TextView news;
        @Bind(R.id.manage_group_info_item_state)
        TextView state;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
