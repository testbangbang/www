package com.onyx.android.dr.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.BringOutBookReportEvent;
import com.onyx.android.dr.event.DeleteBookReportEvent;
import com.onyx.android.dr.reader.common.ToastManage;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by li on 2017/9/15.
 */

public class BookReportListAdapter extends PageRecyclerView.PageAdapter {
    private List<GetBookReportListBean> data;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.report_list_row);
    private int column = DRApplication.getInstance().getResources().getInteger(R.integer.report_list_col);

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
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(DRApplication.getInstance(), R.layout.book_report_list_item, null);
        return new BookReportViewHolder(view);
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BookReportViewHolder viewHolder = (BookReportViewHolder) holder;
        GetBookReportListBean bookReportListBean = data.get(position);
        viewHolder.bookReportListItemBookName.setText(bookReportListBean.name);
        viewHolder.bookReportListItemTime.setText(bookReportListBean.updatedAt + "");
        viewHolder.bookReportListItemPage.setText("000");
        String content = bookReportListBean.content;
        viewHolder.bookReportListItemWordCount.setText(content == null ? "0" : String.valueOf(content.length()));
        viewHolder.bookReportListItemSummary.setText(content == null ? "" : content);
        viewHolder.bookReportListItemBringOut.setOnClickListener(this);
        viewHolder.bookReportListItemBringOut.setTag(position);
        viewHolder.bookReportListItemShare.setOnClickListener(this);
        viewHolder.bookReportListItemShare.setTag(position);
        viewHolder.bookReportListItemDelete.setOnClickListener(this);
        viewHolder.bookReportListItemDelete.setTag(position);
        viewHolder.itemView.setTag(position);
        viewHolder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Object position = v.getTag();
        if(position == null) {
            return;
        }
        GetBookReportListBean bookReportBean = data.get((Integer) position);

        switch (v.getId()) {
            case R.id.book_report_list_item_bring_out:
                EventBus.getDefault().post(new BringOutBookReportEvent(bookReportBean));
                break;
            case R.id.book_report_list_item_share:
                ToastManage.showMessage(DRApplication.getInstance(),"share");
                break;
            case R.id.book_report_list_item_delete:
                EventBus.getDefault().post(new DeleteBookReportEvent(bookReportBean));
                break;
            default:
                startBookReportDetailActivity(bookReportBean);
                break;
        }
    }

    private void startBookReportDetailActivity(GetBookReportListBean bookReportBean) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BOOK_REPORT_DATA, bookReportBean);
        intent.putExtras(bundle);
        ActivityManager.startReadingReportActivity(DRApplication.getInstance(), intent);
    }

    public void setData(List<GetBookReportListBean> data) {
        this.data = data;
        notifyDataSetChanged();
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
        @Bind(R.id.book_report_list_item_bring_out)
        TextView bookReportListItemBringOut;
        @Bind(R.id.book_report_list_item_share)
        TextView bookReportListItemShare;
        @Bind(R.id.book_report_list_item_delete)
        TextView bookReportListItemDelete;

        BookReportViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
