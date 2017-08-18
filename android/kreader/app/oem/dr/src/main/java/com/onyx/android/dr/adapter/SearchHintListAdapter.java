package com.onyx.android.dr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.view.PageRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hehai on 2016/12/7.
 */
public class SearchHintListAdapter extends PageRecyclerView.PageAdapter {
    private List<String> list;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.search_history_list_row);
    private int column = DRApplication.getInstance().getResources().getInteger(R.integer.search_history_list_col);
    private OnItemClickListener listener;

    public SearchHintListAdapter() {
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        return list;
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
        return list == null ? 0 : list.size();
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.item_search_hint_listview, null));
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final String string = list.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.itemSearchHint.setText(string);
        viewHolder.itemSearchHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, string);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_search_hint)
        TextView itemSearchHint;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {
        void onClick(View v, String string);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        listener = l;
    }
}
