package com.onyx.android.dr.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;
import com.onyx.android.sdk.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by li on 2017/9/15.
 */

public class BookReportListAdapter extends PageRecyclerView.PageAdapter<BookReportListAdapter.ViewHolder> {
    private List<GetBookReportListBean> data = new ArrayList<>();
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.report_list_row);
    private int column = DRApplication.getInstance().getResources().getInteger(R.integer.report_list_col);
    private OnItemClickListener onItemClickListener;
    private List<Boolean> listCheck;

    public void setData(List<GetBookReportListBean> data, List<Boolean> listCheck) {
        this.data = data;
        this.listCheck = listCheck;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return column;
    }

    @Override
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(DRApplication.getInstance(), R.layout.book_report_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(final ViewHolder holder, final int position) {
        final GetBookReportListBean bookReportListBean = data.get(position);
        String time = DateTimeUtil.formatDate(bookReportListBean.updatedAt, DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM);
        holder.bookReportListItemTime.setText(time);
        holder.bookReportListItemBookName.setText(bookReportListBean.title);
        holder.bookReportListItemPage.setText(bookReportListBean.pageNumber);
        String content = bookReportListBean.content;
        holder.bookReportListItemWordCount.setText(content == null ? "0" : String.valueOf(content.length()));
        holder.bookReportListItemSummary.setText(content == null ? "" : content);
        holder.checkBox.setChecked(listCheck.get(position));
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
                startBookReportDetailActivity(bookReportListBean);
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    public interface OnItemClickListener {
        void setOnItemClick(int position, boolean isCheck);

        void setOnItemCheckedChanged(int position, boolean isCheck);
    }

    public void setOnItemListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void startBookReportDetailActivity(GetBookReportListBean bookReportBean) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BOOK_REPORT_DATA, bookReportBean);
        intent.putExtras(bundle);
        intent.putExtra(Constants.JUMP_SOURCE, Constants.READER_RESPONSE_SOURCE_TAG);
        ActivityManager.startReadingReportActivity(DRApplication.getInstance(), intent);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.book_report_list_item_time)
        TextView bookReportListItemTime;
        @Bind(R.id.book_report_list_item_book_name)
        TextView bookReportListItemBookName;
        @Bind(R.id.book_report_list_item_page)
        TextView bookReportListItemPage;
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
