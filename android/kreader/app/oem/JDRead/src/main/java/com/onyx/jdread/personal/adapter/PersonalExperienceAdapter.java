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
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.personal.cloud.entity.jdbean.RecommendItemBean;
import com.onyx.jdread.shop.ui.BookDetailFragment;

import java.util.List;

/**
 * Created by li on 2017/12/29.
 */

public class PersonalExperienceAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<RecommendItemBean> data;

    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.personal_experience_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.personal_experience_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_experience_layout, parent, false);
        return new PersonalExperienceViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PersonalExperienceViewHolder viewHolder = (PersonalExperienceViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        RecommendItemBean recommendItemBean = data.get(position);
        viewHolder.bindTo(recommendItemBean);
    }

    public void setData(List<RecommendItemBean> data) {
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
        RecommendItemBean recommendItemBean = data.get(position);
        JDPreferenceManager.setLongValue(Constants.SP_KEY_BOOK_ID, recommendItemBean.ebook_id);
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
        }
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

        public void bindTo(RecommendItemBean bean) {
            binding.setBean(bean);
            binding.executePendingBindings();
        }
    }
}
