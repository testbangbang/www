package com.onyx.edu.manager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.manager.R;

/**
 * Created by suicheng on 2017/7/8.
 */

public class GroupSelectAdapter extends RecyclerView.Adapter<GroupSelectAdapter.GroupViewHolder> {

    private ItemClickListener itemClickListener;
    private CloudGroup groupContainer;

    public GroupSelectAdapter(CloudGroup group) {
        this.groupContainer = group;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setGroupContainer(CloudGroup group) {
        this.groupContainer = group;
        notifyDataSetChanged();
    }

    @Override
    public GroupSelectAdapter.GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_select_item, parent, false);
        return new GroupSelectAdapter.GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupSelectAdapter.GroupViewHolder holder, int position) {
        holder.itemView.setTag(position);
        String groupName = null;
        CloudGroup group = groupContainer.children.get(position);
        if (group != null) {
            groupName = group.name;
        }
        holder.groupName.setText(groupName);
    }

    @Override
    public int getItemCount() {
        if (groupContainer == null) {
            return 0;
        }
        return CollectionUtils.getSize(groupContainer.children);
    }

    private void processItemClick(View view) {
        if (itemClickListener != null) {
            int position = (Integer) view.getTag();
            itemClickListener.onClick(position, view);
        }
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        public TextView groupName;

        public GroupViewHolder(View itemView) {
            super(itemView);
            groupName = (TextView) itemView.findViewById(R.id.group_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemClick(v);
                }
            });
        }
    }
}
