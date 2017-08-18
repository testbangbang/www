package com.onyx.android.dr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.util.AppConfig;
import com.onyx.android.dr.view.PageRecyclerView;

/**
 * Created by huxiaomao on 2016/12/13.
 */

public class DeviceSettingPageRefreshesAdapter extends PageRecyclerView.PageAdapter {
    private static final String TAG = DeviceSettingPageRefreshesAdapter.class.getSimpleName();
    private int row = AppConfig.DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_ROW;
    private int column = AppConfig.DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_COLUMN;
    private String[] refreshRate;
    private String selectItem;
    private boolean a2Checked;

    public DeviceSettingPageRefreshesAdapter(Context context) {
        row = AppConfig.sharedInstance(context).getDeviceSettingPageViewRow();
        column = AppConfig.sharedInstance(context).getDeviceSettingPageViewColumn();
    }

    public String getSelectItem() {
        return selectItem;
    }

    public void setRefreshRate(String[] refreshRate) {
        this.refreshRate = refreshRate;
    }

    @Override
    public int getRowCount() {
        if (refreshRate == null) {
            return row;
        } else {
            return refreshRate.length;
        }
    }

    @Override
    public int getColumnCount() {
        return column;
    }

    @Override
    public int getDataCount() {
        if (refreshRate == null) {
            return 0;
        } else {
            return refreshRate.length;
        }
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.page_refreshes_view_item, parent, false));
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String title = refreshRate[position];
        Holder view = (Holder) holder;
        view.tvRefreshRate.setText(title);
        if (title.equals(selectItem)) {
            view.ivCheck.setVisibility(View.VISIBLE);
        } else {
            view.ivCheck.setVisibility(View.GONE);
        }
        view.itemView.setTag(title);
    }

    @Override
    public void setOnItemClick(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        super.setOnItemClick(onRecyclerViewItemClickListener);
    }

    @Override
    public void onClick(View v) {
        if(!a2Checked){
            Object object = v.getTag();
            if (object == null) {
                return;
            }
            selectItem = (String) object;
            if (onRecyclerViewItemClickListener != null) {
                onRecyclerViewItemClickListener.onItemClick(v, selectItem);
            }
            notifyDataSetChanged();
        }
    }

    public void setA2Checked(boolean a2Checked) {
        this.a2Checked = a2Checked;
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView tvRefreshRate;
        ImageView ivCheck;

        public Holder(View view) {
            super(view);
            tvRefreshRate = (TextView) view.findViewById(R.id.page_refreshes_refresh_rate);
            ivCheck = (ImageView) view.findViewById(R.id.page_refreshes_check);
            view.setOnClickListener(DeviceSettingPageRefreshesAdapter.this);
        }
    }
}
