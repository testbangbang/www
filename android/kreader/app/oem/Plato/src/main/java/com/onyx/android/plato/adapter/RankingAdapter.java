package com.onyx.android.plato.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.plato.R;
import com.onyx.android.plato.databinding.ItemRankingAdapterBinding;
import com.onyx.android.plato.view.PageRecyclerView;

/**
 * Created by hehai on 17-10-17.
 */

public class RankingAdapter extends PageRecyclerView.PageAdapter<RankingAdapter.ViewHolder> {
    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public int getDataCount() {
        return 0;
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View.inflate(parent.getContext(), R.layout.item_ranking_adapter, null);
        return null;
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public void onClick(View v) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemRankingAdapterBinding bind;

        public ViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public ItemRankingAdapterBinding getBind() {
            return bind;
        }
    }
}
