package com.onyx.jdread.library.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ModelItemBinding;

import java.util.List;

import static android.media.CamcorderProfile.get;

/**
 * Created by hehai on 17-11-13.
 */

public class ModelAdapter extends PageAdapter<PageRecyclerView.ViewHolder, DataModel, DataModel> {
    private int viewTypeThumbnailRow = JDReadApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_row);
    private int viewTypeThumbnailCol = JDReadApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_col);
    private int multiSelectionMode = SelectionMode.NORMAL_MODE;
    private int row = viewTypeThumbnailRow;
    private int col = viewTypeThumbnailCol;

    public ModelAdapter() {

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
    public PageRecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item, null));
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        final DataModel dataModel = getItemVMList().get(position);
        setEnableSelection(dataModel);
        ModelViewHolder viewHolder = (ModelViewHolder) holder;
        viewHolder.bindTo(dataModel);
    }


    private void setEnableSelection(DataModel dataModel) {
        if (isSelectable(dataModel)) {
            dataModel.setEnableSelection(false);
        } else {
            dataModel.setEnableSelection(isMultiSelectionMode());
        }
    }

    private boolean isSelectable(DataModel dataModel) {
        return dataModel.getFileModel() != null && dataModel.getFileModel().isGoUpType();
    }

    @Override
    public void setRawData(List<DataModel> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    public void setMultiSelectionMode(int multiSelectionMode) {
        this.multiSelectionMode = multiSelectionMode;
    }

    public boolean isMultiSelectionMode() {
        return multiSelectionMode == SelectionMode.MULTISELECT_MODE;
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

        public void bindTo(DataModel model) {
            bind.setModel(model);
            bind.executePendingBindings();
        }
    }
}
