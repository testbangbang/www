package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class NewWordAdapter extends PageRecyclerView.PageAdapter<NewWordAdapter.ViewHolder> implements View.OnClickListener {
    private List<NewWordNoteBookEntity> dataList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClick(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public void setDataList(List<NewWordNoteBookEntity> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.good_sentence_notebook_row);
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
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_new_word, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        NewWordNoteBookEntity bean = dataList.get(position);
        long currentTime = bean.currentTime;
        holder.month.setText(TimeUtils.getCurrentMonth(currentTime));
        holder.week.setText(TimeUtils.getWeekOfMonth(currentTime));
        holder.day.setText(TimeUtils.getCurrentDay(currentTime));
        holder.content.setText(bean.newWord);
        holder.readingMatter.setText(bean.readingMatter);
        holder.dictionaryLookup.setText(bean.dictionaryLookup);
        holder.rootView.setTag(position);
        holder.rootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(v, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.new_word_item_check)
        ImageView checkImageView;
        @Bind(R.id.new_word_item_month)
        TextView month;
        @Bind(R.id.new_word_item_week)
        TextView week;
        @Bind(R.id.new_word_item_day)
        TextView day;
        @Bind(R.id.new_word_item_content)
        TextView content;
        @Bind(R.id.new_word_item_reading_matter)
        TextView readingMatter;
        @Bind(R.id.new_word_item_dictionaryLookup)
        TextView dictionaryLookup;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
