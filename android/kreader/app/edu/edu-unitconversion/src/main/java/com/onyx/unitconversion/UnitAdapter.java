package com.onyx.unitconversion;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.CommonViewHolder;

import java.util.List;

/**
 * Created by ming on 2017/5/18.
 */

public class UnitAdapter extends RecyclerView.Adapter {

    private List<Pair<String, String>> conversionResult;

    public UnitAdapter(List<Pair<String, String>> unitTypeStringMap) {
        this.conversionResult = unitTypeStringMap;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_result_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CommonViewHolder viewHolder = (CommonViewHolder) holder;
        final String name = conversionResult.get(position).first;
        final String unit = conversionResult.get(position).second;
        viewHolder.setText(R.id.name, name);
        viewHolder.setText(R.id.unit, unit);
    }

    @Override
    public int getItemCount() {
        return conversionResult.size();
    }
}
