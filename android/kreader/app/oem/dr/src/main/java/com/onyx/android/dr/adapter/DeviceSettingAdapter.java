package com.onyx.android.dr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.DeviceSettingData;
import com.onyx.android.dr.event.DeviceSettingViewBaseEvent;
import com.onyx.android.dr.util.AppConfig;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangzhongping on 17-5-5.
 */
public class DeviceSettingAdapter extends PageRecyclerView.PageAdapter {
    private static final String TAG = DeviceSettingAdapter.class.getSimpleName();
    private int row = AppConfig.DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_ROW;
    private int column = AppConfig.DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_COLUMN;
    private String[] titles;
    private Map<String,String> values = new HashMap<>();

    public DeviceSettingAdapter(Context context) {
        row = AppConfig.sharedInstance(context).getDeviceSettingPageViewRow();
        column = AppConfig.sharedInstance(context).getDeviceSettingPageViewColumn();
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public void setValues(Map<String,String> values){
        this.values.clear();
        this.values.putAll(values);
    }

    @Override
    public int getRowCount() {
        if (titles == null) {
            return row;
        } else {
            return titles.length;
        }
    }

    @Override
    public int getColumnCount() {
        return column;
    }

    @Override
    public int getDataCount() {
        return titles == null ? Constants.VALUE_ZERO : titles.length;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_setting_page_view_item, parent, false));
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String title = titles[position];
        Holder view = (Holder) holder;
        view.tvTitle.setText(title);
        String value = values.get(title);
        if(StringUtils.isNotBlank(value)){
            view.tvSetting.setText(value);
        }
        view.itemView.setOnClickListener(this);
        view.itemView.setTag(title);
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
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSetting;
        ImageView ivDetails;

        public Holder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.device_setting_title);
            tvSetting = (TextView) view.findViewById(R.id.device_setting_setting);
            ivDetails = (ImageView) view.findViewById(R.id.device_setting_details);
        }
    }
}
