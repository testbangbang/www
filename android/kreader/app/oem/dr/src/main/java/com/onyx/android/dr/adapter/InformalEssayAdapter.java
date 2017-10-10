package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.data.database.InformalEssayEntity;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.view.PageRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class InformalEssayAdapter extends PageRecyclerView.PageAdapter<InformalEssayAdapter.ViewHolder> {
    private List<InformalEssayEntity> dataList;
    private List<Boolean> listCheck;
    private OnItemClickListener onItemClickListener;

    public void setDataList(List<InformalEssayEntity> dataList, List<Boolean> listCheck) {
        this.dataList = dataList;
        this.listCheck = listCheck;
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
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_infromal_essay, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        InformalEssayEntity bean = dataList.get(position);
        long currentTime = bean.currentTime;
        holder.content.setText(bean.content);
        holder.time.setText(TimeUtils.getDate(currentTime));
        holder.title.setText(bean.title);
        holder.wordNumber.setText(bean.wordNumber);
        holder.checkBox.setChecked(listCheck.get(position));
        holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (onItemClickListener != null) {
                    onItemClickListener.setOnItemCheckedChanged(position, b);
                }
            }
        });
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    if (holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                        onItemClickListener.setOnItemClick(position, false);
                    } else {
                        holder.checkBox.setChecked(true);
                        onItemClickListener.setOnItemClick(position, true);
                    }
                }
            }
        });
        holder.wordNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityManager.startAddInformalEssayActivity(DRApplication.getInstance());
            }
        });
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
        @Bind(R.id.infromal_essay_item_check)
        CheckBox checkBox;
        @Bind(R.id.infromal_essay_item_content)
        TextView content;
        @Bind(R.id.infromal_essay_item_time)
        TextView time;
        @Bind(R.id.infromal_essay_item_title)
        TextView title;
        @Bind(R.id.infromal_essay_item_word_number)
        TextView wordNumber;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
