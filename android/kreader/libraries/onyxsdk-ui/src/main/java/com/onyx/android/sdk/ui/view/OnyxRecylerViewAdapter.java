package com.onyx.android.sdk.ui.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by solskjaer49 on 16/6/23 15:42.
 * Adapter Template
 */

public class OnyxRecylerViewAdapter extends RecyclerView.Adapter<OnyxRecylerViewAdapter.OnyxRecyclerViewHolder> {
    private List<String> listData;
    private int childHeight;

    OnyxRecylerViewAdapter(List<String> mList, int childHeight) {
        super();
        this.listData = mList;
        this.childHeight = childHeight;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public OnyxRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = childHeight;
        view.setLayoutParams(params);
        return new OnyxRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OnyxRecyclerViewHolder holder, int position) {
        holder.textView.setText(listData.get(position));
    }


    class OnyxRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        OnyxRecyclerViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(android.R.id.text1);
        }

    }
}
