package com.onyx.android.plato.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.onyx.android.sdk.wifi.AccessPoint;
import com.onyx.android.plato.R;
import com.onyx.android.plato.devicesetting.AppConfig;
import com.onyx.android.plato.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huxiaomao on 2016/12/13.
 */

public class WifiSettingAdapter extends PageRecyclerView.PageAdapter {
    private static final String TAG = WifiSettingAdapter.class.getSimpleName();
    private int row = AppConfig.DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_ROW;
    private int column = AppConfig.DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_COLUMN;
    private List<AccessPoint> scanResult = new ArrayList<>();
    private int textColor;
    private static Map<Integer, Integer> WIFI_SIGNAL_IMAGE = new HashMap<>();
    private static final int WIFI_SIGNAL_0 = 0;
    private static final int WIFI_SIGNAL_1 = 1;
    private static final int WIFI_SIGNAL_2 = 2;
    private static final int WIFI_SIGNAL_3 = 3;

    static {
        WIFI_SIGNAL_IMAGE.put(WIFI_SIGNAL_0, R.drawable.ic_wifi_signal_1);
        WIFI_SIGNAL_IMAGE.put(WIFI_SIGNAL_1, R.drawable.ic_wifi_signal_2);
        WIFI_SIGNAL_IMAGE.put(WIFI_SIGNAL_2, R.drawable.ic_wifi_signal_3);
        WIFI_SIGNAL_IMAGE.put(WIFI_SIGNAL_3, R.drawable.ic_wifi_signal_4);
    }

    public WifiSettingAdapter(Context context) {
        row = AppConfig.sharedInstance(context).getDeviceSettingWifiSettingsPageViewRow();
        column = AppConfig.sharedInstance(context).getDeviceSettingWifiSettingsPageViewColumn();
        textColor = context.getResources().getColor(R.color.black);
    }

    public void setDataList(List<AccessPoint> scanResult) {
        this.scanResult.clear();
        this.scanResult.addAll(scanResult);
    }

    public List<AccessPoint> getDataList() {
        return scanResult;
    }

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return column;
    }

    @Override
    public int getDataCount() {
        if (scanResult == null) {
            return 0;
        } else {
            return scanResult.size();
        }
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_setting_view_item, parent, false));
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        AccessPoint accessPoint = scanResult.get(position);
        Holder view = (Holder) holder;
        view.tvTitle.setText(accessPoint.getScanResult().SSID);
        view.tvTitle.setTextColor(textColor);

        view.tvSetting.setText(accessPoint.getSecurityString());
        view.tvSetting.setTextColor(textColor);

        if (accessPoint.getSignalLevel() < WIFI_SIGNAL_0 || accessPoint.getSignalLevel() > WIFI_SIGNAL_3) {
            view.ivWifiSignal.setImageResource(R.drawable.ic_wifi_signal_1);
        } else {
            view.ivWifiSignal.setImageResource(WIFI_SIGNAL_IMAGE.get(accessPoint.getSignalLevel()));
        }
        if (accessPoint.getSecurity() == 2) {
            view.ivWifiLock.setVisibility(View.VISIBLE);
        } else {
            view.ivWifiLock.setVisibility(View.GONE);
        }

        view.itemView.setOnClickListener(this);
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
        int position = (Integer) object;
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(v, position);
        }
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSetting;
        ImageView ivWifiLock;
        ImageView ivWifiSignal;

        public Holder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.wifi_setting_title);
            tvSetting = (TextView) view.findViewById(R.id.wifi_setting_setting);
            ivWifiSignal = (ImageView) view.findViewById(R.id.wifi_setting_wifi_signal);
            ivWifiLock = (ImageView) view.findViewById(R.id.wifi_setting_wifi_lock);
        }
    }
}
