package com.onyx.jdread.setting.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DeviceInfoItemBinding;
import com.onyx.jdread.main.common.PageAdapter;
import com.onyx.jdread.setting.model.DeviceInformationModel;

import java.util.List;

/**
 * Created by hehai on 18-1-2.
 */

public class DeviceInfoAdapter extends PageAdapter<DeviceInfoAdapter.ViewHolder, DeviceInformationModel.ItemModel, DeviceInformationModel.ItemModel> {
    @Override
    public int getRowCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.device_info_row);
    }

    @Override
    public int getColumnCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.device_info_col);
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.device_info_item, null));
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        DeviceInformationModel.ItemModel itemModel = getItemVMList().get(position);
        holder.bind(itemModel);
    }

    @Override
    public void setRawData(List<DeviceInformationModel.ItemModel> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final DeviceInfoItemBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void bind(DeviceInformationModel.ItemModel itemModel) {
            binding.setItemModel(itemModel);
            binding.executePendingBindings();
        }
    }
}
