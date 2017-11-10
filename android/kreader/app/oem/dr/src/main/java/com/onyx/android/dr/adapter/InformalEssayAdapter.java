package com.onyx.android.dr.adapter;

import android.content.Intent;
import android.os.Bundle;
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
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class InformalEssayAdapter extends PageRecyclerView.PageAdapter<InformalEssayAdapter.ViewHolder> {
    private List<CreateInformalEssayBean> dataList;
    private List<Boolean> listCheck;
    private OnItemClickListener onItemClickListener;

    public void setDataList(List<CreateInformalEssayBean> dataList, List<Boolean> listCheck) {
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
        View inflate = View.inflate(DRApplication.getInstance(), R.layout.informal_essay_list_item, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        final CreateInformalEssayBean bean = dataList.get(position);
        long currentTime = bean.currentTime;
        holder.bookReportListItemSummary.setText(bean.content);
        holder.bookReportListItemTime.setText(TimeUtils.getDate(currentTime));
        holder.bookReportListItemBookName.setText(bean.title);
        holder.bookReportListItemWordCount.setText(bean.wordNumber);
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
                startBookReportDetailActivity(bean);
            }
        });
    }

    private void startBookReportDetailActivity(CreateInformalEssayBean bookReportBean) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BOOK_REPORT_DATA, bookReportBean);
        intent.putExtras(bundle);
        intent.putExtra(Constants.JUMP_SOURCE, Constants.INFORMAL_ESSAY_SOURCE_TAG);
        ActivityManager.startReadingReportActivity(DRApplication.getInstance(), intent);
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
        @Bind(R.id.book_report_list_item_time)
        TextView bookReportListItemTime;
        @Bind(R.id.book_report_list_item_book_name)
        TextView bookReportListItemBookName;
        @Bind(R.id.book_report_list_item_summary)
        TextView bookReportListItemSummary;
        @Bind(R.id.book_report_list_item_word_count)
        TextView bookReportListItemWordCount;
        @Bind(R.id.book_report_list_item_check)
        CheckBox checkBox;
        View rootView;

        ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }
    }
}
