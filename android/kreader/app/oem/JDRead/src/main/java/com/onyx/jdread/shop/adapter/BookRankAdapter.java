package com.onyx.jdread.shop.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.SubjectCommonBinding;
import com.onyx.jdread.shop.model.SubjectViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class BookRankAdapter extends PageAdapter<PageRecyclerView.ViewHolder, SubjectViewModel, SubjectViewModel> {

    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.book_rank_subject_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.book_rank_subject_col);

    public BookRankAdapter() {
    }

    public void setRowAndCol(int row, int col) {
        this.row = row;
        this.col = col;
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
        return getItemVMList().size();
    }

    @Override
    public PageRecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_common, parent, false));
    }

    @Override
    public void onPageBindViewHolder(PageRecyclerView.ViewHolder holder, int position) {
        final SubjectViewModel bookBean = getItemVMList().get(position);
        ModelViewHolder viewHolder = (ModelViewHolder) holder;
        viewHolder.bindTo(bookBean);
    }

    @Override
    public void setRawData(List<SubjectViewModel> rawData, Context context) {
        super.setRawData(rawData, context);
        setItemVMList(rawData);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    static class ModelViewHolder extends PageRecyclerView.ViewHolder {

        private final SubjectCommonBinding bind;

        public ModelViewHolder(View view) {
            super(view);
            bind = DataBindingUtil.bind(view);
            SubjectAdapter recyclerViewOneAdapter = new SubjectAdapter(EventBus.getDefault());
            PageRecyclerView recyclerViewSuject = bind.recyclerViewSuject;
            recyclerViewSuject.setLayoutManager(new DisableScrollGridManager(JDReadApplication.getInstance()));
            recyclerViewSuject.setAdapter(recyclerViewOneAdapter);
        }

        public SubjectCommonBinding getBind() {
            return bind;
        }

        public void bindTo(SubjectViewModel viewModel) {
            bind.setViewModel(viewModel);
            bind.executePendingBindings();
        }
    }
}