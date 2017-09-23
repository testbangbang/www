package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.view.PageRecyclerView;
import com.onyx.android.dr.webview.AutoPagedWebView;
import com.onyx.android.sdk.data.model.v2.CommentsBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by li on 2017/9/22.
 */

public class BookMarksAdapter extends PageRecyclerView.PageAdapter {
    private List<CommentsBean> data;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.book_marks_row);
    private int column = DRApplication.getInstance().getResources().getInteger(R.integer.book_marks_col);

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
        View view = View.inflate(DRApplication.getInstance(), R.layout.book_marks_item_layout, null);
        BookMarksHolder bookMarksHolder = new BookMarksHolder(view);
        return bookMarksHolder;
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BookMarksHolder bookMarksHolder = (BookMarksHolder) holder;
        CommentsBean comment = data.get(position);
        bookMarksHolder.bookMarkContent.loadDataWithBaseURL(null, comment.content, "text/html", "utf-8", null);
    }

    @Override
    public void onClick(View v) {

    }

    public void setData(List<CommentsBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class BookMarksHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.book_mark_content)
        AutoPagedWebView bookMarkContent;

        BookMarksHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
