package com.onyx.jdread.setting.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.wifi.AccessPoint;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemWifiBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/12/20.
 */

public class WifiSettingAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private List<AccessPoint> scanResult = new ArrayList<>();
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

    @Override
    public int getRowCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.wifi_setting_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.wifi_setting_adapter_col);
    }

    @Override
    public int getDataCount() {
        return scanResult == null ? 0 : scanResult.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_setting, parent, false);
        return new WifiViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WifiViewHolder viewHolder = (WifiViewHolder) holder;
        ItemWifiBinding binding = viewHolder.getBinding();
        AccessPoint accessPoint = scanResult.get(position);
        if (accessPoint.getSignalLevel() < WIFI_SIGNAL_0 || accessPoint.getSignalLevel() > WIFI_SIGNAL_3) {
            binding.wifiSettingWifiSignal.setImageResource(R.drawable.ic_wifi_signal_1);
        } else {
            binding.wifiSettingWifiSignal.setImageResource(WIFI_SIGNAL_IMAGE.get(accessPoint.getSignalLevel()));
        }
        viewHolder.bindTo(accessPoint);
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
    }

    public void setScanResult(List<AccessPoint> list) {
        if (scanResult != null && scanResult.size() > 0) {
            scanResult.clear();
        }
        scanResult.addAll(list);
        notifyDataSetChanged();
    }

    public List<AccessPoint> getScanResult() {
        return scanResult;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
        }
    }

    static class WifiViewHolder extends RecyclerView.ViewHolder {
        private ItemWifiBinding binding;

        public WifiViewHolder(View itemView) {
            super(itemView);
            binding = (ItemWifiBinding) DataBindingUtil.bind(itemView);
        }

        public ItemWifiBinding getBinding() {
            return binding;
        }

        public void bindTo(AccessPoint accessPoint) {
            binding.setAccessPoint(accessPoint);
            binding.executePendingBindings();
        }
    }
}
