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

/**
 * Created by li on 2018/1/3.
 */

public class PersonalNoteAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private boolean show;

    @Override
    public int getRowCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.personal_note_row);
    }

    @Override
    public int getColumnCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.personal_note_col);
    }

    @Override
    public int getDataCount() {
        return 5;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_note_layout, parent, false);
        return new PersonalNoteViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PersonalNoteViewHolder viewHolder = (PersonalNoteViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.getBinding().setShowBox(show);
    }

    public void showBox(boolean show) {
        this.show = show;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        // TODO: 2018/1/11
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

        public void bindTo() {

        }
    }
}
