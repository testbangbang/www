package com.onyx.jdread.setting.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemDeviceConfigBinding;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.event.UpdateTimeFormatEvent;
import com.onyx.jdread.main.model.MainBundle;
import com.onyx.jdread.main.model.SystemBarModel;
import com.onyx.jdread.setting.event.DeviceConfigEvent;
import com.onyx.jdread.setting.model.DeviceConfigData;
import com.onyx.jdread.util.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/12/22.
 */

public class DeviceConfigAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private EventBus eventBus;
    private List<DeviceConfigData> data;
    private Map<String, DeviceConfigEvent> configEvents;
    private final static int FORMAT_24 = 24;
    private final static int FORMAT_12 = 12;

    public DeviceConfigAdapter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public int getRowCount() {
        return ResManager.getInteger(R.integer.device_config_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return ResManager.getInteger(R.integer.device_config_adapter_col);
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
        DeviceConfigData deviceConfigData = data.get(position);
        boolean is24Hour = JDPreferenceManager.getBooleanValue(R.string.is_24_hour_key, true);
        deviceConfigData.setTimeFormat(is24Hour ? FORMAT_24 : FORMAT_12);
        viewHolder.getBinding().itemDataFormatCheck.setChecked(is24Hour);
        viewHolder.getBinding().itemDataFormatCheck.setOnCheckedChangeListener(this);
        viewHolder.bindTo(deviceConfigData);
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
        if (deviceConfigEvent != null) {
            deviceConfigEvent.setDeviceConfigData(deviceConfigData);
            eventBus.post(deviceConfigEvent);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        JDPreferenceManager.setBooleanValue(R.string.is_24_hour_key, isChecked);
        eventBus.post(new UpdateTimeFormatEvent());
        data.get(0).setTimeFormat(isChecked ? FORMAT_24 : FORMAT_12);
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
