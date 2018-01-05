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
import com.onyx.jdread.databinding.ItemPersonalBookBinding;

/**
 * Created by li on 2018/1/4.
 */

public class PersonalBookAdapter extends PageRecyclerView.PageAdapter {
    @Override
    public int getRowCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.personal_book_row);
    }

    @Override
    public int getColumnCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.personal_book_col);
    }

    @Override
    public int getDataCount() {
        return 5;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_book_layout, parent, false);
        return new PersonalBookViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {

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

        public void bindTo() {

        }
    }
}
