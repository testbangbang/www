package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class SpeechTimeAdapter extends PageRecyclerView.PageAdapter<SpeechTimeAdapter.ViewHolder> implements View.OnClickListener {
    private List<String> dataList = new ArrayList<>();
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private int selectedPosition = -1;

    public void setMenuDataList(List<String> dataList) {
        this.dataList.addAll(dataList);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClick(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.speech_time_tab_row);
    }

    @Override
    public int getColumnCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.good_sentence_tab_column);
    }

    @Override
    public int getDataCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_speech_time, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        String content = dataList.get(position);
        holder.tabMenuTitle.setText(content);
        holder.rootView.setTag(position);
        if (selectedPosition == position) {
            holder.tabMenuTitle.setBackgroundResource(R.drawable.rectangle_stroke_focused);
            holder.tabMenuTitle.setTextColor(DRApplication.getInstance().getResources().getColor(R.color.white));
        } else {
            holder.tabMenuTitle.setBackgroundResource(R.drawable.rectangle_stroke);
            holder.tabMenuTitle.setTextColor(DRApplication.getInstance().getResources().getColor(R.color.black));
        }
        holder.rootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        selectedPosition = position;
        notifyDataSetChanged();
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(v, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.speech_time_item_time)
        TextView tabMenuTitle;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
