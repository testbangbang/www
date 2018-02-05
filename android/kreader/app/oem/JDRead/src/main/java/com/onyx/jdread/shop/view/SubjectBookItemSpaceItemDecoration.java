
package com.onyx.jdread.shop.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;

/**
 * Created by jackdeng on 2018/2/1.
 */

public class SubjectBookItemSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private boolean isBanner;
    private int col = ResManager.getInteger(R.integer.book_shop_subject_col);
    private int half = ResManager.getInteger(R.integer.calculate_half);

    public SubjectBookItemSpaceItemDecoration(boolean isBanner, int space) {
        this.space = space;
        this.isBanner = isBanner;
        initCol();
    }

    private void initCol() {
        if (col <= 0) {
            col = Constants.SHOP_MAIN_INDEX_ONE;
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (isBanner) {
            if (parent.getChildPosition(view) == Constants.SHOP_MAIN_INDEX_ZERO) {
                outRect.right = space / half;
            } else {
                outRect.left = space / half;
            }
        } else {
            int position = parent.getChildAdapterPosition(view);
            int column = position % col;
            outRect.left = column * space / col;
            outRect.right = space - (column + Constants.PAGE_STEP) * space / col;
        }
    }
}