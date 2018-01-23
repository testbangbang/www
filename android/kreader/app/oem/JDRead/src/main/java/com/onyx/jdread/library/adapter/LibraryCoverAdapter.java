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
import com.onyx.jdread.databinding.LibraryCoverItemBinding;
import com.onyx.jdread.main.common.PageAdapter;

import java.util.List;

/**
 * Created by hehai on 17-11-13.
 */

public class LibraryCoverAdapter extends PageAdapter<LibraryCoverAdapter.ViewHolder, DataModel, DataModel> {
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.library_cover_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.library_cover_col);

    public LibraryCoverAdapter() {

    }

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
    public LibraryCoverAdapter.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.library_cover_item, null));
    }

    @Override
    public void onPageBindViewHolder(LibraryCoverAdapter.ViewHolder holder, int position) {
        final DataModel dataModel = getItemVMList().get(position);
        holder.bindTo(dataModel);
    }

    @Override
    public void setRawData(List<DataModel> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    static class ViewHolder extends PageRecyclerView.ViewHolder {

        private final LibraryCoverItemBinding bind;

        public ViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public LibraryCoverItemBinding getBind() {
            return bind;
        }

        public void bindTo(DataModel model) {
            bind.setModel(model);
            bind.executePendingBindings();
        }
    }
}
