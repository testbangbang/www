package com.onyx.android.dr.reader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.data.ReadSummaryNewWordReviewBean;
import com.onyx.android.dr.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 17-7-25.
 */

public class NewWordsReviewListAdapter extends PageRecyclerView.PageAdapter<NewWordsReviewListAdapter.ViewHolder> implements View.OnClickListener {
    private List<ReadSummaryNewWordReviewBean> list;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.new_word_review_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.new_word_review_col);

    public NewWordsReviewListAdapter() {
    }

    public void setList(List<ReadSummaryNewWordReviewBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return col;
    }

    @Override
    public int getDataCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_new_word_review, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        final ReadSummaryNewWordReviewBean entity = list.get(position);
        holder.newWord.setText(entity.word);
        holder.checkbox.setChecked(entity.isChecked());
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                entity.setChecked(isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.new_word)
        TextView newWord;
        @Bind(R.id.item_new_word_review_checkbox)
        CheckBox checkbox;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public List<ReadSummaryNewWordReviewBean> getSelectedList() {
        List<ReadSummaryNewWordReviewBean> selectedList = new ArrayList<>();
        for (ReadSummaryNewWordReviewBean entity : list) {
            if (entity.isChecked()) {
                selectedList.add(entity);
            }
        }
        return selectedList;
    }

    public String getNewWordListJson() {
        return JSON.toJSONString(list);
    }
}
