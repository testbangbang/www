package com.onyx.android.sdk.ui.view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by ming on 2017/7/25.
 */

public abstract class AdaptiveHeightPageAdapter<VH extends RecyclerView.ViewHolder> extends PageRecyclerView.PageAdapter<VH> {

    private SparseArray<Integer> itemHeightMap;

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
        int itemHeight = holder.itemView.getHeight();
        if (itemHeight == 0) {
            return;
        }
        int row = getPagePaginator().rowInCurrentPage(holder.getAdapterPosition());
        Integer value = getItemHeightMap().get(row);
        value = (value != null && value > itemHeight) ? value : itemHeight;
        getItemHeightMap().put(row, value);
        updateParentHeight(holder);
    }

    private void updateParentHeight(VH holder) {
        Log.d("", "updateParentHeight: ");
        if (getPagePaginator().getCurrentPageEnd() != holder.getAdapterPosition()) {
            return;
        }
        ViewGroup.LayoutParams params = pageRecyclerView.getLayoutParams();
        params.height = calculateAdaptiveParentHeight();
        pageRecyclerView.setLayoutParams(params);
    }


    private int calculateAdaptiveParentHeight() {
        final int paddingBottom = pageRecyclerView.getOriginPaddingBottom();
        final int paddingTop = pageRecyclerView.getPaddingTop();
        int startRow = getPagePaginator().getCurrentPageBeginRow();
        int endRow = getPagePaginator().getCurrentPageEndRow();
        int height = 0;

        for (int i = startRow; i <= endRow; i++) {
            Integer value =  getItemHeightMap().get(i);
            if (value != null) {
                height += value;
            }
        }
        height = height + paddingBottom + paddingTop + getRowCount() * pageRecyclerView.getItemDecorationHeight();
        return height;
    }

    private SparseArray<Integer> getItemHeightMap() {
        if (itemHeightMap == null) {
            itemHeightMap = new SparseArray<>();
        }
        return itemHeightMap;
    }
}
