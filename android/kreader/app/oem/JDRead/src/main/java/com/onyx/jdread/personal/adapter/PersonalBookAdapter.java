package com.onyx.jdread.personal.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemPersonalBookBinding;
import com.onyx.jdread.main.common.ResManager;

import java.util.List;

/**
 * Created by li on 2018/1/4.
 */

public class PersonalBookAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<Metadata> data;

    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.personal_book_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.personal_book_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_book_layout, parent, false);
        return new PersonalBookViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PersonalBookViewHolder viewHolder = (PersonalBookViewHolder) holder;
        Metadata metadata = data.get(position);
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(metadata);
    }

    public void setData(List<Metadata> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
        }
    }

    public List<Metadata> getData() {
        return data;
    }

    static class PersonalBookViewHolder extends RecyclerView.ViewHolder {
        private ItemPersonalBookBinding binding;

        public PersonalBookViewHolder(View itemView) {
            super(itemView);
            binding = (ItemPersonalBookBinding) DataBindingUtil.bind(itemView);
        }

        public ItemPersonalBookBinding getBinding() {
            return binding;
        }

        public void bindTo(Metadata metadata) {
            binding.setMetadata(metadata);
            binding.executePendingBindings();
        }
    }
}
