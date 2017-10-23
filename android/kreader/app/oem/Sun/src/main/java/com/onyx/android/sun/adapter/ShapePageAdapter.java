package com.onyx.android.sun.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.databinding.ItemShapePageBinding;
import com.onyx.android.sun.event.ShapePageItemEvent;
import com.onyx.android.sun.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-10-16.
 */

public class ShapePageAdapter extends PageRecyclerView.PageAdapter<ShapePageAdapter.ViewHolder> {
    private int shapes;

    public void setShapes(int shapes) {
        this.shapes = shapes;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.shape_page_adapter_row);
    }

    @Override
    public int getColumnCount() {
        return SunApplication.getInstance().getResources().getInteger(R.integer.shape_page_adapter_col);
    }

    @Override
    public int getDataCount() {
        return shapes;
    }

    @Override
    public ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_shape_page, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onPageBindViewHolder(ViewHolder holder, int position) {
        holder.getBind().setText(String.format(SunApplication.getInstance().getResources().getString(R.string.answer_page_format), (position + 1)));
        holder.getBind().itemShapePage.setOnClickListener(this);
        holder.getBind().itemShapePage.setTag(position);
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        int position = (int) v.getTag();
        EventBus.getDefault().post(new ShapePageItemEvent(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemShapePageBinding bind;

        public ViewHolder(View itemView) {
            super(itemView);
            bind = DataBindingUtil.bind(itemView);
        }

        public ItemShapePageBinding getBind() {
            return bind;
        }
    }
}
