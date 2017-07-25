package com.onyx.android.sdk.ui.view;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by ming on 2017/7/25.
 */

public abstract class AdaptiveWidthPageAdapter<VH extends RecyclerView.ViewHolder> extends PageRecyclerView.PageAdapter<VH> {

    private static final String TAG = "AdaptiveWidthPageAdapter";
    private SparseArray<Integer> itemWidthMap;

    public void onViewAttachedToWindow(final VH holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.post(new Runnable() {
            @Override
            public void run() {
                adjustParentViewLayout(holder);
            }
        });
    }

    @Override
    protected void adjustParentViewLayout(VH holder) {
        int itemWidth = holder.itemView.getWidth();
        if (itemWidth == 0) {
            return;
        }
        getItemWidthMap().put(holder.getAdapterPosition(), itemWidth);
        ViewGroup.LayoutParams params = pageRecyclerView.getLayoutParams();
        params.width = calculateAdaptiveParentWidth();
        pageRecyclerView.setLayoutParams(params);
    }

    private int calculateAdaptiveParentWidth() {
        final int paddingLeft = pageRecyclerView.getPaddingLeft();
        final int paddingRight = pageRecyclerView.getPaddingRight();
        int start = pageRecyclerView.getPaginator().getCurrentPageBegin();
        int end = pageRecyclerView.getPaginator().getCurrentPageEnd();
        int width = 0;
        for (int i = start; i <= end; i++) {
            Integer value =  getItemWidthMap().get(i);
            if (value != null) {
                width += value;
            }
        }
        width = width + paddingLeft + paddingRight;
        return width;
    }

    private SparseArray<Integer> getItemWidthMap() {
        if (itemWidthMap == null) {
            itemWidthMap = new SparseArray<>();
        }
        return itemWidthMap;
    }
}
