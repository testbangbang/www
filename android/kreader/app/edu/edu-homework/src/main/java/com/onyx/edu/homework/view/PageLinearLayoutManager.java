package com.onyx.edu.homework.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.onyx.edu.homework.utils.ViewUtils;

/**
 * Created by lxm on 2017/12/6.
 */

public class PageLinearLayoutManager extends LinearLayoutManager {

    public PageLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        int lastCompletelyVisibleItem = findLastCompletelyVisibleItemPosition();
        recycler.getViewForPosition(lastCompletelyVisibleItem).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
    }


    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
//        for (int i = 0; i < getChildCount(); i++) {
//            View view = getChildAt(i);
//            view.setVisibility(ViewUtils.isVisibleLocal(view) ? View.VISIBLE : View.INVISIBLE);
//        }

    }
}
