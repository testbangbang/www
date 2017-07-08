package com.onyx.edu.manager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.model.FuncItemEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/7/6.
 */

public class FuncSelectAdapter extends RecyclerView.Adapter<FuncSelectAdapter.FuncViewHolder> {

    private List<FuncItemEntity> funcItemEntityList = new ArrayList<>();
    private ItemClickListener itemClickListener;

    public FuncSelectAdapter(List<FuncItemEntity> list) {
        this.funcItemEntityList = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public FuncViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.func_item, parent, false);
        return new FuncViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FuncViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.itemTextLabel.setText(funcItemEntityList.get(position).labelText);
        holder.itemIconLabel.setImageResource(funcItemEntityList.get(position).labelIconRes);
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.getSize(funcItemEntityList);
    }

    private void processItemClick(View view) {
        if (itemClickListener == null) {
            return;
        }
        int position = (Integer) view.getTag();
        itemClickListener.onClick(position, view);
    }

    class FuncViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_text_label)
        public TextView itemTextLabel;
        @Bind(R.id.item_icon_label)
        public ImageView itemIconLabel;

        public FuncViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processItemClick(v);
                }
            });
        }
    }
}
