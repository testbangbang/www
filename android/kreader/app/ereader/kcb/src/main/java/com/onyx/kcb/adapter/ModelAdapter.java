package com.onyx.kcb.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.kcb.R;
import com.onyx.kcb.databinding.ModelItemBinding;
import com.onyx.kcb.model.DataModel;

import java.util.List;

/**
 * Created by hehai on 17-11-13.
 */

public class ModelAdapter extends PageAdapter<ModelAdapter.ModelViewHolder, DataModel, DataModel> {
    private int row = 3;
    private int col = 3;

    public void setRowAndCol(int row, int col) {
        this.row = row;
        this.col = col;
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
    public int getDataCount() {
        return getItemVMList().size();
    }

    @Override
    public ModelViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item, null));
    }

    @Override
    public void onPageBindViewHolder(ModelViewHolder holder, int position) {
        final DataModel dataModel = getItemVMList().get(position);
        holder.getBind().setModel(dataModel);
        holder.getBind().notifyChange();
    }

    @Override
    public void setRawData(List<DataModel> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    static class ModelViewHolder extends PageRecyclerView.ViewHolder {

        private final ModelItemBinding bind;

        public ModelViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public ModelItemBinding getBind() {
            return bind;
        }
    }
}
