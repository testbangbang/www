package com.onyx.jdread.personal.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemPersonalNoteBinding;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.personal.cloud.entity.jdbean.NoteBean;

import java.util.List;

/**
 * Created by li on 2018/1/3.
 */

public class PersonalNoteAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private boolean show;
    private List<NoteBean> data;

    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.personal_note_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.personal_note_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_note_layout, parent, false);
        return new PersonalNoteViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PersonalNoteViewHolder viewHolder = (PersonalNoteViewHolder) holder;
        ItemPersonalNoteBinding binding = viewHolder.getBinding();
        binding.itemNoteCheck.setOnClickListener(this);
        binding.itemNoteCheck.setTag(position);
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(data.get(position));
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        NoteBean noteBean = data.get(position);
        noteBean.checked = !noteBean.checked;
        notifyItemChanged(position);
    }

    public void setData(List<NoteBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public List<NoteBean> getData() {
        return data;
    }

    static class PersonalNoteViewHolder extends RecyclerView.ViewHolder {
        private ItemPersonalNoteBinding binding;

        public PersonalNoteViewHolder(View itemView) {
            super(itemView);
            binding = (ItemPersonalNoteBinding) DataBindingUtil.bind(itemView);
        }

        public ItemPersonalNoteBinding getBinding() {
            return binding;
        }

        public void bindTo(NoteBean bean) {
            binding.setBean(bean);
            binding.executePendingBindings();
        }
    }
}
