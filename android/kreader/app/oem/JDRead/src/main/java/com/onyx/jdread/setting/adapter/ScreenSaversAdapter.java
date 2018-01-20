package com.onyx.jdread.setting.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ScreenSaversItemBinding;
import com.onyx.jdread.main.common.PageAdapter;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.setting.model.ScreenSaversModel;

import java.util.List;

/**
 * Created by hehai on 18-1-1.
 */

public class ScreenSaversAdapter extends PageAdapter<ScreenSaversAdapter.ViewHolder, ScreenSaversModel.ItemModel, ScreenSaversModel.ItemModel> {
    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.screen_savers_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.screen_savers_col);
    }

    @Override
    public ScreenSaversAdapter.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.screen_savers_item, null));
    }

    @Override
    public void onPageBindViewHolder(ScreenSaversAdapter.ViewHolder holder, int position) {
        holder.bind(getItemVMList().get(position));
    }

    @Override
    public void setRawData(List<ScreenSaversModel.ItemModel> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ScreenSaversItemBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void bind(ScreenSaversModel.ItemModel itemModel) {
            binding.setItemModel(itemModel);
            binding.executePendingBindings();
        }
    }
}
