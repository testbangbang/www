package com.onyx.jdread.setting.adapter;

import android.databinding.DataBindingUtil;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemDeviceConfigBinding;
import com.onyx.jdread.setting.event.DeviceConfigEvent;
import com.onyx.jdread.setting.model.DeviceConfigData;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/12/22.
 */

public class DeviceConfigAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private EventBus eventBus;
    private List<DeviceConfigData> data;
    private Map<String, DeviceConfigEvent> configEvents;
    private static final String HOURS_12 = "12";
    private static final String HOURS_24 = "24";

    public DeviceConfigAdapter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public int getRowCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.device_config_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.device_config_adapter_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_config_layout, parent, false);
        return new DeviceConfigViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DeviceConfigViewHolder viewHolder = (DeviceConfigViewHolder) holder;
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.getBinding().itemDataFormatCheck.setChecked(is24Hour());
        viewHolder.getBinding().itemDataFormatCheck.setOnCheckedChangeListener(this);
        viewHolder.bindTo(data.get(position));
    }

    public void setData(List<DeviceConfigData> deviceConfigDataList, Map<String, DeviceConfigEvent> configEvents) {
        this.data = deviceConfigDataList;
        this.configEvents = configEvents;
        notifyDataSetChanged();
    }

    public List<DeviceConfigData> getData() {
        return data;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        DeviceConfigData deviceConfigData = data.get(position);
        DeviceConfigEvent deviceConfigEvent = configEvents.get(deviceConfigData.getConfigName());
        deviceConfigEvent.setDeviceConfigData(deviceConfigData);
        eventBus.post(deviceConfigEvent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        set24Hour(isChecked);
        Utils.sendTimeChangeBroadcast();
    }

    private boolean is24Hour() {
        return DateFormat.is24HourFormat(JDReadApplication.getInstance());
    }

    private void set24Hour(boolean is24Hour) {
        Settings.System.putString(JDReadApplication.getInstance().getContentResolver(),
                Settings.System.TIME_12_24,
                is24Hour ? HOURS_24 : HOURS_12);
    }

    static class DeviceConfigViewHolder extends RecyclerView.ViewHolder {
        private ItemDeviceConfigBinding binding;

        public DeviceConfigViewHolder(View itemView) {
            super(itemView);
            binding = (ItemDeviceConfigBinding) DataBindingUtil.bind(itemView);
        }

        public ItemDeviceConfigBinding getBinding() {
            return binding;
        }

        public void bindTo(DeviceConfigData deviceConfigData) {
            binding.setModel(deviceConfigData);
            binding.executePendingBindings();
        }
    }
}
