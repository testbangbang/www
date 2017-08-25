package com.onyx.android.dr.reader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.data.ReadSummaryNewWordReviewBean;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

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
        holder.newWord.setText(String.format(DRApplication.getInstance().getString(R.string.item_new_word_first_string), entity.word));
        holder.wordPropertyEdit.setText(entity.property);
        holder.wordCommonlyUsedEdit.setText(entity.commonlyUsed);
        holder.wordInterpretationEdit.setText(entity.interpretation);
        holder.wordPropertyEdit.addTextChangedListener(new TextChangeListener() {
            @Override
            void onEditTextChanged(CharSequence s, int start, int before, int count) {
                entity.property = s.toString();
            }
        });
        holder.wordCommonlyUsedEdit.addTextChangedListener(new TextChangeListener() {
            @Override
            void onEditTextChanged(CharSequence s, int start, int before, int count) {
                entity.commonlyUsed = s.toString();
            }
        });
        holder.wordInterpretationEdit.addTextChangedListener(new TextChangeListener() {
            @Override
            void onEditTextChanged(CharSequence s, int start, int before, int count) {
                entity.interpretation = s.toString();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.new_word)
        TextView newWord;
        @Bind(R.id.word_property_edit)
        EditText wordPropertyEdit;
        @Bind(R.id.word_commonlyUsed_edit)
        EditText wordCommonlyUsedEdit;
        @Bind(R.id.word_interpretation_edit)
        EditText wordInterpretationEdit;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public String getNewWordListJson() {
        return JSON.toJSONString(list);
    }
}
