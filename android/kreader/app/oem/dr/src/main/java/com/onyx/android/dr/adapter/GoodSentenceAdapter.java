package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.event.OpenDialogEvent;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class GoodSentenceAdapter extends PageRecyclerView.PageAdapter<GoodSentenceAdapter.ViewHolder> {
    private List<GoodSentenceNoteEntity> dataList;
    private OnItemClickListener onItemClickListener;
    private List<Boolean> listCheck;

    public void setDataList(List<GoodSentenceNoteEntity> dataList, List<Boolean> listCheck) {
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
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_good_sentence, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        final GoodSentenceNoteEntity goodSentenceEntity = dataList.get(position);
		long currentTime = goodSentenceEntity.currentTime;
        holder.time.setText(TimeUtils.getDate(currentTime));
        holder.content.setText(goodSentenceEntity.details);
        holder.readingMatter.setText(goodSentenceEntity.readingMatter);
        if (StringUtils.isNullOrEmpty(goodSentenceEntity.pageNumber)) {
            holder.pageNumber.setText(DRApplication.getInstance().getString(R.string.nothing));
        } else {
            holder.pageNumber.setText(goodSentenceEntity.pageNumber);
        }
        holder.checkBox.setChecked(listCheck.get(position));
        holder.orderNumber.setText(String.valueOf(position + 1));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new OpenDialogEvent(goodSentenceEntity.details));
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
        @Bind(R.id.good_sentence_item_check)
        CheckBox checkBox;
        @Bind(R.id.good_sentence_item_time)
        TextView time;
        @Bind(R.id.good_sentence_item_content)
        TextView content;
        @Bind(R.id.good_sentence_item_reading_matter)
        TextView readingMatter;
        @Bind(R.id.good_sentence_item_page_number)
        TextView pageNumber;
        @Bind(R.id.new_word_item_order_number)
        TextView orderNumber;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
