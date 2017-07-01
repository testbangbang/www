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

    public DeviceBindAdapter.DeviceBindHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceBindAdapter.DeviceBindHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_bind_item, null));
    }

    @Override
    public void onBindViewHolder(DeviceBindAdapter.DeviceBindHolder holder, int position) {
        holder.macTv.setText(String.valueOf(deviceBindList.get(position).mac));
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.getSize(deviceBindList);
    }

    class DeviceBindHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.mac_tv)
        TextView macTv;

        public DeviceBindHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
