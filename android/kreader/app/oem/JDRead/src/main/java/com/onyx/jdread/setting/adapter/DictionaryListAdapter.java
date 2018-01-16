package com.onyx.jdread.setting.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DeviceInfoItemBinding;
import com.onyx.jdread.databinding.DictionaryItemBinding;
import com.onyx.jdread.main.common.PageAdapter;
import com.onyx.jdread.setting.model.DeviceInformationModel;
import com.onyx.jdread.setting.model.DictionaryModel;

import java.util.List;

/**
 * Created by hehai on 18-1-2.
 */

public class DictionaryListAdapter extends PageAdapter<DictionaryListAdapter.ViewHolder, DictionaryModel.DictionaryItem, DictionaryModel.DictionaryItem> {
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.dictionary_list_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.dictionary_list_col);

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
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.dictionary_item, null));
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        DictionaryModel.DictionaryItem itemModel = getItemVMList().get(position);
        holder.bind(itemModel);
    }

    @Override
    public void setRawData(List<DictionaryModel.DictionaryItem> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final DictionaryItemBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void bind(DictionaryModel.DictionaryItem itemModel) {
            binding.setItemModel(itemModel);
            binding.executePendingBindings();
        }
    }
}
