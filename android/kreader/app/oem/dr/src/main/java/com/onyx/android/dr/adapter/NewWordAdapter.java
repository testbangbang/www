package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class NewWordAdapter extends PageRecyclerView.PageAdapter<NewWordAdapter.ViewHolder> {
    private List<NewWordNoteBookEntity> dataList;
    private List<Boolean> listCheck;
    private OnItemClickListener onItemClickListener;

    public void setDataList(List<NewWordNoteBookEntity> dataList, List<Boolean> listCheck) {
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
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.item_new_word, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        final NewWordNoteBookEntity bean = dataList.get(position);
        long currentTime = bean.currentTime;
        holder.time.setText(TimeUtils.getDate(currentTime));
        holder.content.setText(bean.newWord);
        if (StringUtils.isNullOrEmpty(bean.readingMatter)) {
            holder.readingMatter.setText(R.string.nothing);
        } else {
            holder.readingMatter.setText(bean.readingMatter);
        }
        if (StringUtils.isNullOrEmpty(bean.dictionaryLookup)) {
            holder.dictionaryLookup.setText(R.string.nothing);
        } else {
            holder.dictionaryLookup.setText(bean.dictionaryLookup);
        }
        holder.orderNumber.setText(String.valueOf(position + 1));
        holder.paraphrase.setText(bean.paraphrase);
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
        holder.paraphrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityManager.startNewWordQueryActivity(DRApplication.getInstance(), bean.newWord);
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
        @Bind(R.id.new_word_item_check)
        CheckBox checkBox;
        @Bind(R.id.new_word_item_time)
        TextView time;
        @Bind(R.id.new_word_item_content)
        TextView content;
        @Bind(R.id.new_word_item_reading_matter)
        TextView readingMatter;
        @Bind(R.id.new_word_item_dictionaryLookup)
        TextView dictionaryLookup;
        @Bind(R.id.new_word_item_paraphrase)
        TextView paraphrase;
        @Bind(R.id.new_word_item_order_number)
        TextView orderNumber;
        @Bind(R.id.item_new_word_linearlayout)
        LinearLayout itemLinearLayout;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
