package com.onyx.android.dr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.DeviceStorageInformation;
import com.onyx.android.dr.util.AppConfig;
import com.onyx.android.dr.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzhongping on 17-5-5.
 */
public class DeviceSettingDeviceStorageInformationAdapter extends PageRecyclerView.PageAdapter {
    private static final String TAG = DeviceSettingDeviceStorageInformationAdapter.class.getSimpleName();
    private int row = AppConfig.DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_ROW;
    private int column = AppConfig.DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_COLUMN;
    private List<DeviceStorageInformation> deviceStorageInformations = new ArrayList<>();

    public DeviceSettingDeviceStorageInformationAdapter(Context context) {
        row = AppConfig.sharedInstance(context).getDeviceSettingPageViewRow();
        column = AppConfig.sharedInstance(context).getDeviceSettingPageViewColumn();
    }

    public void setDeviceStorageInformations(List<DeviceStorageInformation> deviceStorageInformations) {
        this.deviceStorageInformations.clear();
        this.deviceStorageInformations.addAll(deviceStorageInformations);
    }

    @Override
    public int getRowCount() {
        if (deviceStorageInformations == null) {
            return row;
        } else {
            return deviceStorageInformations.size();
        }
    }

    @Override
    public int getColumnCount() {
        return column;
    }

    @Override
    public int getDataCount() {
        return deviceStorageInformations == null ? Constants.VALUE_ZERO : deviceStorageInformations.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_storage_information_view_item, parent, false));
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        DeviceStorageInformation deviceStorageInformation = deviceStorageInformations.get(position);
        Holder view = (Holder) holder;
        view.tvTitle.setText(deviceStorageInformation.title);
        view.tvSetting.setText(deviceStorageInformation.information);
    }

    @Override
    public void setOnItemClick(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        super.setOnItemClick(onRecyclerViewItemClickListener);
    }

    @Override
    public void onClick(View v) {
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSetting;

        public Holder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.device_storage_information_title);
            tvSetting = (TextView) view.findViewById(R.id.device_storage_information_setting);
        }
    }
}
