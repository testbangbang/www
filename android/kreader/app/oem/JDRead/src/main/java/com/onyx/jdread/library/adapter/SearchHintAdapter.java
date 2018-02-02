package com.onyx.jdread.library.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.SearchHintItemBinding;
import com.onyx.jdread.main.common.PageAdapter;
import com.onyx.jdread.main.common.ResManager;

import java.util.List;

/**
 * Created by hehai on 18-1-18.
 */

public class SearchHintAdapter extends PageAdapter<SearchHintAdapter.ViewHolder, DataModel, DataModel> {
    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.search_hint_recycler_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.search_hint_recycler_col);
    }

    @Override
    public int getDataCount() {
        return getItemVMList().size();
    }

    @Override
    public SearchHintAdapter.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_hint_item, null));
    }

    @Override
    public void onPageBindViewHolder(SearchHintAdapter.ViewHolder holder, int position) {
        DataModel dataModel = getItemVMList().get(position);
        holder.bind.setDataModel(dataModel);
    }

    @Override
    public void setRawData(List<DataModel> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    static class ViewHolder extends PageRecyclerView.ViewHolder {

        private final SearchHintItemBinding bind;

        public ViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public SearchHintItemBinding getBind() {
            return bind;
        }

        public void bindTo(DataModel model) {
            bind.setDataModel(model);
            bind.executePendingBindings();
        }
    }
}
