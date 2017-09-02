package com.onyx.edu.manager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.manager.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/6/27.
 */

public class DeviceBindAdapter extends RecyclerView.Adapter<DeviceBindAdapter.DeviceBindHolder> {

    private List<DeviceBind> deviceBindList = new ArrayList<>();

    public DeviceBindAdapter(List<DeviceBind> list) {
        this.deviceBindList = list;
    }

    public DeviceBindHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceBindHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_bind_item, null));
    }

    @Override
    public void onBindViewHolder(DeviceBindHolder holder, int position) {
        DeviceBind item = deviceBindList.get(position);
        holder.macTv.setText(item.mac);
        holder.tagTv.setText(item.tag);
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.getSize(deviceBindList);
    }

    class DeviceBindHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.mac_tv)
        TextView macTv;
        @Bind(R.id.tag_tv)
        TextView tagTv;

        public DeviceBindHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
