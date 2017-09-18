package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.view.PageRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by li on 2017/9/15.
 */

public class BookReportListAdapter extends PageRecyclerView.PageAdapter {
    @Override
    public int getRowCount() {
        return 7;
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public int getDataCount() {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(DRApplication.getInstance(), R.layout.book_report_list_item, null);
        return new BookReportViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onClick(View v) {

    }

    static class BookReportViewHolder extends RecyclerView.ViewHolder {
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
        @Bind(R.id.book_report_list_item_manage)
        TextView bookReportListItemManage;

        BookReportViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
