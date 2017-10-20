package com.onyx.android.sun.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.sun.R;
import com.onyx.android.sun.devicesetting.AppConfig;
import com.onyx.android.sun.view.PageRecyclerView;


/**
 * Created by huxiaomao on 2016/12/13.
 */

public class DeviceSettingLockScreenTimeAdapter extends PageRecyclerView.PageAdapter {
    private static final String TAG = DeviceSettingLockScreenTimeAdapter.class.getSimpleName();
    private int row = AppConfig.DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_ROW;
    private int column = AppConfig.DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_COLUMN;
    private CharSequence[] times;
    private CharSequence[] values;
    private CharSequence currentScreenTimeout;
    private int selectItem = 0;

    public DeviceSettingLockScreenTimeAdapter(Context context) {
        row = AppConfig.sharedInstance(context).getDeviceSettingPageViewRow();
        column = AppConfig.sharedInstance(context).getDeviceSettingPageViewColumn();
    }

    public CharSequence getCurrentScreenTimeout() {
        return currentScreenTimeout;
    }

    public void setTimes(final CharSequence[] times, final CharSequence[] values, final CharSequence currentScreenTimeout) {
        this.times = times;
        this.values = values;
        this.currentScreenTimeout = currentScreenTimeout;
    }

    @Override
    public int getRowCount() {
        if (times == null) {
            return row;
        } else {
            return times.length;
        }
    }

    @Override
    public int getColumnCount() {
        return column;
    }

    @Override
    public int getDataCount() {
        if (times == null) {
            return 0;
        } else {
            return times.length;
        }
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lock_screen_time_view_item, parent, false));
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CharSequence title = times[position];
        Holder view = (Holder) holder;
        view.tvTime.setText(title);
        if (currentScreenTimeout.equals(values[position])) {
            view.ivCheck.setVisibility(View.VISIBLE);
        } else {
            view.ivCheck.setVisibility(View.GONE);
        }
        view.itemView.setTag(position);
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
        selectItem = (Integer) object;
        currentScreenTimeout = values[selectItem];
        notifyDataSetChanged();
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView tvTime;
        ImageView ivCheck;

        public Holder(View view) {
            super(view);
            tvTime = (TextView) view.findViewById(R.id.lock_screen_time_time);
            ivCheck = (ImageView) view.findViewById(R.id.lock_screen_time_check);
            view.setOnClickListener(DeviceSettingLockScreenTimeAdapter.this);
        }
    }
}
