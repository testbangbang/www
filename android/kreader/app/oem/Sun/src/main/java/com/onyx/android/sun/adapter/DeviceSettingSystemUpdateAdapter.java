package com.onyx.android.sun.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.sun.R;
import com.onyx.android.sun.data.DeviceSettingData;
import com.onyx.android.sun.data.SystemVersionInformation;
import com.onyx.android.sun.devicesetting.AppConfig;
import com.onyx.android.sun.event.DeviceSettingViewBaseEvent;
import com.onyx.android.sun.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2016/12/13.
 */

public class DeviceSettingSystemUpdateAdapter extends PageRecyclerView.PageAdapter {
    private static final String TAG = DeviceSettingSystemUpdateAdapter.class.getSimpleName();
    private int row = AppConfig.DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_ROW;
    private int column = AppConfig.DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_COLUMN;
    private List<SystemVersionInformation> systemVersionInformationList = new ArrayList<>();

    public DeviceSettingSystemUpdateAdapter(Context context) {
        row = AppConfig.sharedInstance(context).getDeviceSettingPageViewRow();
        column = AppConfig.sharedInstance(context).getDeviceSettingPageViewColumn();
    }

    public void setSystemVersionInformationList(List<SystemVersionInformation> systemVersionInformationList) {
        this.systemVersionInformationList.addAll(systemVersionInformationList);
    }

    @Override
    public int getRowCount() {
        if (systemVersionInformationList == null) {
            return row;
        } else {
            return systemVersionInformationList.size();
        }
    }

    @Override
    public int getColumnCount() {
        return column;
    }

    @Override
    public int getDataCount() {
        if (systemVersionInformationList == null) {
            return 0;
        } else {
            return systemVersionInformationList.size();
        }
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.system_update_view_item, parent, false));
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        SystemVersionInformation systemVersionInformation = systemVersionInformationList.get(position);
        Holder view = (Holder) holder;
        view.tvTitle.setText(systemVersionInformation.title);
        view.tvSetting.setText(systemVersionInformation.information);
        if (systemVersionInformation.isChild) {
            view.ivDetails.setVisibility(View.VISIBLE);
            view.itemView.setOnClickListener(this);
            view.itemView.setTag(systemVersionInformation.title);
        } else {
            view.ivDetails.setVisibility(View.GONE);
            view.itemView.setTag(null);
        }
    }

    @Override
    public void setOnItemClick(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        super.setOnItemClick(onRecyclerViewItemClickListener);
    }

    @Override
    public void onClick(View v) {
        Object object = v.getTag();
        if (object == null) {
            return;
        }
        String title = (String) object;
        DeviceSettingViewBaseEvent.DeviceSettingBaseEvent deviceSettingBaseEvent = DeviceSettingData.getDeviceSettingViewEvent(title);
        if (deviceSettingBaseEvent != null) {
            EventBus.getDefault().post(deviceSettingBaseEvent);
        }

        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(v, title);
        }
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSetting;
        ImageView ivDetails;

        public Holder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.system_update_title);
            tvSetting = (TextView) view.findViewById(R.id.system_update_setting);
            ivDetails = (ImageView) view.findViewById(R.id.system_update_details);
        }
    }
}
