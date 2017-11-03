package com.onyx.android.dr.reader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.data.ReadSummaryGoodSentenceReviewBean;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 17-7-25.
 */

public class GoodSentenceReviewListAdapter extends PageRecyclerView.PageAdapter<GoodSentenceReviewListAdapter.ViewHolder> implements View.OnClickListener {
    private List<ReadSummaryGoodSentenceReviewBean> list;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.good_sentence_review_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.good_sentence_review_col);

    public GoodSentenceReviewListAdapter() {

    }

    public void setList(List<ReadSummaryGoodSentenceReviewBean> list) {
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
        return list == null ? 0 : 1;
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_good_sentence_review, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        final ReadSummaryGoodSentenceReviewBean entity = list.get(position);
        holder.goodSentence.setText(entity.sentence);
        holder.goodSentenceSupplements.setText(entity.supplements);
        holder.goodSentenceSupplements.addTextChangedListener(new TextChangeListener() {
            @Override
            void onEditTextChanged(CharSequence s, int start, int before, int count) {
                entity.supplements = s.toString();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.good_sentence)
        TextView goodSentence;
        @Bind(R.id.good_sentence_supplements)
        EditText goodSentenceSupplements;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public String getGoodSentenceJson(){
        return JSON.toJSONString(list);
    }
}
