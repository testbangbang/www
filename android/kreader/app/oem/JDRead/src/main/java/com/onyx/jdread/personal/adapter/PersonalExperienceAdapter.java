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
import com.onyx.jdread.databinding.ItemExperienceBinding;
import com.onyx.jdread.main.common.ResManager;

/**
 * Created by li on 2017/12/29.
 */

public class PersonalExperienceAdapter extends PageRecyclerView.PageAdapter {
    @Override
    public int getRowCount() {
        return ResManager.getResManager().getInteger(R.integer.personal_experience_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getResManager().getInteger(R.integer.personal_experience_col);
    }

    @Override
    public int getDataCount() {
        return 6;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_experience_layout, parent, false);
        return new PersonalExperienceViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    static class PersonalExperienceViewHolder extends RecyclerView.ViewHolder {
        private ItemExperienceBinding binding;

        public PersonalExperienceViewHolder(View itemView) {
            super(itemView);
            binding = (ItemExperienceBinding) DataBindingUtil.bind(itemView);
        }

        public ItemExperienceBinding getBinding() {
            return binding;
        }

        public void bindTo() {

        }
    }
}
