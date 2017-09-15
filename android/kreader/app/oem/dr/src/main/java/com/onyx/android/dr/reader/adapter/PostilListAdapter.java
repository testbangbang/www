package com.onyx.android.dr.reader.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.dialog.DialogAnnotation;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 17-7-25.
 */

public class PostilListAdapter extends PageRecyclerView.PageAdapter<PostilListAdapter.ViewHolder> implements View.OnClickListener {
    private List<PageAnnotation> list;
    private ReaderPresenter readerPresenter;

    public PostilListAdapter(ReaderPresenter readerPresenter, List<PageAnnotation> list) {
        this.list = list;
        this.readerPresenter = readerPresenter;
    }

    public void setList(List<PageAnnotation> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return readerPresenter.getReaderView().getViewContext().getResources().getInteger(R.integer.postil_list_row);
    }

    @Override
    public int getColumnCount() {
        return readerPresenter.getReaderView().getViewContext().getResources().getInteger(R.integer.postil_list_col);
    }

    @Override
    public int getDataCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_postil_list, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        PageAnnotation pageAnnotation = list.get(position);
        holder.postilTitle.setText(readerPresenter.getReaderView().getViewContext().getString(R.string.postil) + ":" + +position);
        holder.poistilContent.setText(pageAnnotation.getAnnotation().getNote());
        TextPaint paint = holder.poistilContent.getPaint();
        paint.setFakeBoldText(true);
        holder.rootView.setTag(position);
        holder.rootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) {
            return;
        }
        int position = (int) v.getTag();
        PageAnnotation pageAnnotation = list.get(position);
        readerPresenter.setPageAnnotation(pageAnnotation);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.postil_title)
        TextView postilTitle;
        @Bind(R.id.poistil_content)
        TextView poistilContent;
        View rootView;

        ViewHolder(View view) {
            super(view);
            rootView = view;
            ButterKnife.bind(this, view);
        }
    }

    public void updateData(Annotation annotation) {
        Iterator<PageAnnotation> iterator = list.iterator();
        while (iterator.hasNext()) {
            PageAnnotation ann = iterator.next();
            if (ann.getAnnotation().equals(annotation)) {
                if (StringUtils.isNullOrEmpty(annotation.getNote())) {
                    iterator.remove();
                } else {
                    ann.getAnnotation().setNote(annotation.getNote());
                }
            }
        }
        notifyDataSetChanged();
    }
}
