package com.onyx.jdread.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FunctionBarItemBinding;
import com.onyx.jdread.library.adapter.PageAdapter;
import com.onyx.jdread.model.FunctionBarItem;

import java.util.List;

/**
 * Created by hehai on 17-12-11.
 */

public class FunctionBarAdapter extends PageAdapter<FunctionBarAdapter.ViewHolder, FunctionBarItem, FunctionBarItem> {
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.function_bar_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.function_bar_col);

    public void setRowAndCol(int row, int col) {
        this.row = row;
        this.col = col;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return col;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public FunctionBarAdapter.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.function_bar_item, null));
    }

    @Override
    public void onPageBindViewHolder(FunctionBarAdapter.ViewHolder holder, int position) {
        holder.bindTo(getItemVMList().get(position));
    }

    @Override
    public void setRawData(List<FunctionBarItem> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    static class ViewHolder extends PageRecyclerView.ViewHolder {

        private final FunctionBarItemBinding bind;

        public ViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public FunctionBarItemBinding getBind() {
            return bind;
        }

        public void bindTo(FunctionBarItem model) {
            bind.setFunctionBarItem(model);
            bind.executePendingBindings();
        }
    }
}
