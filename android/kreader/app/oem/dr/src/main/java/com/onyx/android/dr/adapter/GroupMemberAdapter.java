package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.data.model.ChildBean;
import com.onyx.android.sdk.data.model.v2.ListBean;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class GroupMemberAdapter extends PageRecyclerView.PageAdapter<GroupMemberAdapter.ViewHolder> {
    private List<ListBean> dataList = new ArrayList<>();
    public static boolean isShow = false;
    private List<Boolean> listCheck;
    private OnItemClickListener onItemClickListener;

    public void setMenuDataList(List<ListBean> dataList, List<Boolean> listCheck) {
        this.dataList = dataList;
        this.listCheck = listCheck;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.group_member_tab_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.group_member_tab_column);
    }

    @Override
    public int getDataCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_group_member, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        ListBean bean = dataList.get(position);
        final ChildBean child = bean.child;
        String userAccount = DRPreferenceManager.getUserAccount(DRApplication.getInstance(), "");
        if (isShow) {
            if (child.name.equals(userAccount)) {
                holder.checkContainer.setVisibility(View.INVISIBLE);
            } else {
                holder.checkContainer.setVisibility(View.VISIBLE);
            }
        } else {
            holder.checkContainer.setVisibility(View.INVISIBLE);
        }
        holder.tabMenuTitle.setText(child.name);
        holder.tabMenuTitle.setTextColor(DRApplication.getInstance().getResources().getColor(R.color.black));
        holder.checkBox.setChecked(listCheck.get(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (onItemClickListener != null) {
                    onItemClickListener.setOnItemCheckedChanged(position, b);
                }
            }
        });
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    if (isShow) {
                        if (holder.checkBox.isChecked()) {
                            holder.checkBox.setChecked(false);
                            onItemClickListener.setOnItemClick(position, false);
                        } else {
                            holder.checkBox.setChecked(true);
                            onItemClickListener.setOnItemClick(position, true);
                        }
                    } else {
                        String role = dataList.get(dataList.size() - 1).child.role;
                        if (role.equals(Constants.USER) || role.equals(Constants.TEACHER)) {
                            if (child.role.equals(Constants.C_STUDENT) || child.role.equals(Constants.STUDENT)) {
                                ActivityManager.startReadingRateActivity(DRApplication.getInstance(), child.library);
                            }
                        }
                    }
                }
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
        @Bind(R.id.group_member_item_name)
        TextView tabMenuTitle;
        @Bind(R.id.group_member_item_check_container)
        LinearLayout checkContainer;
        @Bind(R.id.group_member_item_check)
        CheckBox checkBox;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
