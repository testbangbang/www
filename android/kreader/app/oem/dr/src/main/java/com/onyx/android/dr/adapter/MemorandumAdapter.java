package com.onyx.android.dr.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.database.MemorandumEntity;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class MemorandumAdapter extends PageRecyclerView.PageAdapter<MemorandumAdapter.ViewHolder> {
    private List<MemorandumEntity> dataList;
    private OnItemClickListener onItemClickListener;

    public void setDataList(List<MemorandumEntity> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getRowCount() {
        return DRApplication.getInstance().getResources().getInteger(R.integer.memorandum_row);
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
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_memorandum, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        final  MemorandumEntity bean = dataList.get(position);
        if (StringUtils.isNullOrEmpty(bean.matter)) {
            holder.matter.setText(R.string.nothing);
        } else {
            holder.matter.setText(bean.matter);
        }
        holder.time.setText(bean.getDate());
        holder.dayOfWeek.setText(bean.getDayOfWeek());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddMemorandum(bean);
            }
        });
    }

    private void startAddMemorandum(MemorandumEntity bean) {
        Intent intent = new Intent();
        intent.putExtra(Constants.MEMORANDUM_DAY_OF_WEEK, bean.getDayOfWeek());
        intent.putExtra(Constants.MEMORANDUM_TIME, bean.getDate());
        intent.putExtra(Constants.MEMORANDUM_MATTER, bean.matter);
        intent.putExtra(Constants.MEMORANDUM_CURRENT_TIME, bean.currentTime);
        ActivityManager.startAddMemorandumActivity(DRApplication.getInstance(), intent);
    }

    @Override
    public void onClick(View view) {
    }

    public interface OnItemClickListener {
        void setOnItemClick(int position, boolean isCheck);

        void setOnItemCheckedChanged(int position, boolean isCheck);
    }

    public void setOnItemListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.memorandum_item_matter)
        TextView matter;
        @Bind(R.id.memorandum_item_time_time)
        TextView time;
        @Bind(R.id.memorandum_item_day_of_week)
        TextView dayOfWeek;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
