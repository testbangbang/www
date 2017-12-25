package com.onyx.jdread.library.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.LibraryListDialogItemBinding;

import java.util.List;

/**
 * Created by hehai on 17-12-19.
 */

public class LibraryListDialogAdapter extends PageAdapter<LibraryListDialogAdapter.ViewHolder, DataModel, DataModel> {
    private int row;
    private int col;
    private ItemClickListener itemClickListener;

    public void setRowAndCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
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
    public LibraryListDialogAdapter.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new LibraryListDialogAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.library_list_dialog_item, null));
    }

    @Override
    public void onPageBindViewHolder(LibraryListDialogAdapter.ViewHolder holder, int position) {
        final DataModel dataModel = getItemVMList().get(position);
        holder.bindTo(dataModel);
        holder.bind.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(dataModel);
            }
        });
    }

    @Override
    public void setRawData(List<DataModel> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClicked(DataModel dataModel);
    }

    static class ViewHolder extends PageRecyclerView.ViewHolder {

        private final LibraryListDialogItemBinding bind;

        public ViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public LibraryListDialogItemBinding getBind() {
            return bind;
        }

        public void bindTo(DataModel model) {
            bind.setLibrary(model);
            bind.executePendingBindings();
        }
    }
}
