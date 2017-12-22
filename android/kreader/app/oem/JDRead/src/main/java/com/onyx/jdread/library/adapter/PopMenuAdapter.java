package com.onyx.jdread.library.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.PopMenuModel;

import java.util.List;

/**
 * Created by hehai on 17-12-21.
 */

public class PopMenuAdapter extends PageRecyclerView.PageAdapter<PopMenuAdapter.ViewHolder> {
    private List<PopMenuModel> list;
    private int row = 4;
    private int col = 1;
    private ItemClickListener ItemListener;

    public PopMenuAdapter(List<PopMenuModel> list, int row, int col) {
        this.list = list;
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
        return list == null ? 0 : list.size();
    }

    @Override
    public PopMenuAdapter.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.pop_menu_item, null));
    }

    @Override
    public void onPageBindViewHolder(PopMenuAdapter.ViewHolder holder, int position) {
        final String text = list.get(position).getText();
        final Object event = list.get(position).getEvent();
        holder.item.setText(text);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemListener.onItemClicked(event);
            }
        });
    }

    public interface ItemClickListener {
        void onItemClicked(Object event);
    }

    public void setItemListener(ItemClickListener itemListener) {
        ItemListener = itemListener;
    }

    static class ViewHolder extends PageRecyclerView.ViewHolder {
        TextView item;

        private ViewHolder(View view) {
            super(view);
            item = (TextView) view.findViewById(R.id.pop_menu_item);
        }
    }
}
