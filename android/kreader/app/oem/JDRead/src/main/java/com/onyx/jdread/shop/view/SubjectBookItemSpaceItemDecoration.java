
package com.onyx.jdread.shop.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.onyx.jdread.main.common.Constants;

/**
 * Created by jackdeng on 2018/2/1.
 */

public class SubjectBookItemSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private boolean isBnner;

    public SubjectBookItemSpaceItemDecoration(boolean isBnner, int space) {
        this.space = space;
        this.isBnner = isBnner;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (isBnner) {
            if (parent.getChildPosition(view) == Constants.SHOP_MAIN_INDEX_ZERO) {
                outRect.right = space / 2;
            } else {
                outRect.left = space / 2;
            }
        } else {
            if (parent.getChildPosition(view) == Constants.SHOP_MAIN_INDEX_ZERO) {
                outRect.right = (int) (space * (3 / 7.0f));
            } else if (parent.getChildPosition(view) == Constants.SHOP_MAIN_INDEX_ONE) {
                outRect.left = (int) (space * (3 / 7.0f));
                outRect.right = (int) (space * (4 / 7.0f));
            } else if (parent.getChildPosition(view) == Constants.SHOP_MAIN_INDEX_TWO) {
                outRect.left = (int) (space * (4 / 7.0f));
                outRect.right = (int) (space * (3 / 7.0f));
            } else if (parent.getChildPosition(view) == Constants.SHOP_MAIN_INDEX_THREE) {
                outRect.left = (int) (space * (3 / 7.0f));
            }
        }
    }
}