package com.onyx.kcb.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.ViewType;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.kcb.KCBApplication;
import com.onyx.kcb.R;
import com.onyx.kcb.databinding.ModelItemBinding;
import com.onyx.kcb.databinding.ModelItemDetailsBinding;
import com.onyx.kcb.event.OnModelAdapterRawDataChangeEvent;
import com.onyx.kcb.model.StorageViewModel;

import java.util.List;

/**
 * Created by hehai on 17-11-13.
 */

public class ModelAdapter extends PageAdapter<PageRecyclerView.ViewHolder, DataModel, DataModel> {
    private StorageViewModel storageViewModel;
    private int viewTypeThumbnailRow = KCBApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_row);
    private int viewTypeThumbnailCol = KCBApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_col);
    private int viewTypeDetailsRow = KCBApplication.getInstance().getResources().getInteger(R.integer.library_view_type_details_row);
    private int viewTypeDetailsCol = KCBApplication.getInstance().getResources().getInteger(R.integer.library_view_type_details_col);
    private int multiSelectionMode = SelectionMode.NORMAL_MODE;
    private int row = viewTypeThumbnailRow;
    private int col = viewTypeThumbnailCol;

    public ModelAdapter(StorageViewModel storageViewModel) {
        this.storageViewModel = storageViewModel;
    }

    public ModelAdapter() {

    }

    public void setRowAndCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public int getRowCount() {
        getRowCountBasedViewType();
        return row;
    }

    @Override
    public int getColumnCount() {
        getColCountBasedViewType();
        return col;
    }

    @Override
    public int getDataCount() {
        return getItemVMList().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getViewType() != null) {
            return getViewType().ordinal();
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public PageRecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ViewType.Thumbnail.ordinal()) {
            return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item, null));
        } else {
            return new ModelItemDetailsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item_details, null));
        }
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        final DataModel dataModel = getItemVMList().get(position);
        dataModel.enableSelection.set(canSelected(dataModel));
        if (getItemViewType(position) == ViewType.Thumbnail.ordinal()) {
            ModelViewHolder viewHolder = (ModelViewHolder) holder;
            viewHolder.bindTo(dataModel);
        } else {
            ModelItemDetailsViewHolder viewHolder = (ModelItemDetailsViewHolder) holder;
            viewHolder.bindTo(dataModel);
        }
    }

    private boolean canSelected(DataModel dataModel) {
        return multiSelectionMode == SelectionMode.MULTISELECT_MODE && dataModel.isDocument.get();
    }

    @Override
    public void setRawData(List<DataModel> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
        if (storageViewModel != null){
            storageViewModel.getEventBus().post(new OnModelAdapterRawDataChangeEvent());
        }
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

    static class ModelItemDetailsViewHolder extends PageRecyclerView.ViewHolder {

        private ModelItemDetailsBinding bind;

        ModelItemDetailsViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
        }

        public ModelItemDetailsBinding getBind() {
            return bind;
        }

        public void bindTo(DataModel model) {
            bind.setViewModel(model);
            bind.executePendingBindings();
        }
    }

    private ViewType getViewType() {
        if (storageViewModel != null) {
            return storageViewModel.getCurrentViewType();
        }
        else {
            return null;
        }
    }

    public int getRowCountBasedViewType() {
        return row = getViewType() == null ? viewTypeThumbnailRow : getRowCount(getViewType());
    }

    public int getColCountBasedViewType() {
        return col = getViewType() == null ? viewTypeThumbnailCol : getColCount(getViewType());
    }

    public int getRowCount(ViewType viewType) {
        return viewType == ViewType.Thumbnail ? viewTypeThumbnailRow : viewTypeDetailsRow;
    }

    public int getColCount(ViewType viewType) {
        return viewType == ViewType.Thumbnail ? viewTypeThumbnailCol : viewTypeDetailsCol;
    }
}
