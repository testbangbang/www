package com.onyx.jdread.setting.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ItemSettingBinding;
import com.onyx.jdread.setting.model.SettingItemData;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * Created by li on 2017/12/19.
 */

public class SettingAdapter extends PageRecyclerView.PageAdapter implements View.OnClickListener {
    private EventBus eventBus;
    private List<SettingItemData> data;
    private Map<String, Object> event;

    public SettingAdapter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public int getRowCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.setting_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return JDReadApplication.getInstance().getResources().getInteger(R.integer.setting_adapter_col);
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_layout, parent, false);
        return new SettingViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SettingViewHolder viewHolder = (SettingViewHolder) holder;
        SettingItemData settingItemData = data.get(position);
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(position);
        viewHolder.bindTo(settingItemData);
    }

    public void setData(List<SettingItemData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        int position = (int) tag;
        SettingItemData settingItemData = data.get(position);
        eventBus.post(event.get(settingItemData.getSettingName()));
    }

    public void setEvent(Map<String, Object> event) {
        this.event = event;
    }

    static class SettingViewHolder extends RecyclerView.ViewHolder {
        private ItemSettingBinding bind;

        public SettingViewHolder(View itemView) {
            super(itemView);
            bind = (ItemSettingBinding) DataBindingUtil.bind(itemView);
        }

        public ItemSettingBinding getBind() {
            return bind;
        }

        public void bindTo(SettingItemData data) {
            bind.setData(data);
            bind.executePendingBindings();
        }
    }
}
